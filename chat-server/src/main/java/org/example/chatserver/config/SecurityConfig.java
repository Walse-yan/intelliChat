package org.example.chatserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.example.chatserver.api.entity.Admin;
import org.example.chatserver.api.entity.RespBean;
import org.example.chatserver.api.entity.User;
import org.example.chatserver.service.impl.AdminServiceImpl;
import org.example.chatserver.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    SecurityFilterChain adminSecurityFilterChain(HttpSecurity http,
                                                 AdminServiceImpl adminService,
                                                 VerificationCodeFilter verificationCodeFilter,
                                                 MyAuthenticationFailureHandler myAuthenticationFailureHandler,
                                                 MyLogoutSuccessHandler myLogoutSuccessHandler) throws Exception {
        http
                .securityMatcher("/admin/**")
                .addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/fonts/**", "/img/**", "/js/**", "/favicon.ico", "/index.html", "/admin/login", "/admin/mailVerifyCode").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/doLogin")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((req, resp, authentication) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            try (PrintWriter out = resp.getWriter()) {
                                Admin admin = (Admin) authentication.getPrincipal();
                                admin.setPassword(null);
                                RespBean ok = RespBean.ok("登录成功", admin);
                                out.write(new ObjectMapper().writeValueAsString(ok));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .failureHandler(myAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessHandler(myLogoutSuccessHandler)
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "未授权");
                        })
                );

        http.userDetailsService(adminService);
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain userSecurityFilterChain(HttpSecurity http,
                                                UserServiceImpl userService,
                                                VerificationCodeFilter verificationCodeFilter,
                                                MyAuthenticationFailureHandler myAuthenticationFailureHandler,
                                                SimpMessagingTemplate simpMessagingTemplate) throws Exception {
        http
                .securityMatcher("/**")
                .addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/verifyCode", "/file", "/user/register", "/user/checkUsername", "/user/checkNickname").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/user/doLogin")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((req, resp, authentication) -> {
                            resp.setContentType("application/json;charset=utf-8");
                            try (PrintWriter out = resp.getWriter()) {
                                System.out.println("login success : " + authentication.getName());
                                User user = (User) authentication.getPrincipal();
                                user.setPassword(null);
                                userService.setUserStateToOn(user.getId());
                                user.setUserStateId(1);
                                simpMessagingTemplate.convertAndSend("/topic/notification", "系统消息：用户【" + user.getNickname() + "】进入了聊天室");
                                RespBean ok = RespBean.ok("登录成功", user);
                                out.write(new ObjectMapper().writeValueAsString(ok));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .failureHandler(myAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((req, resp, authentication) -> {
                            User user = (User) authentication.getPrincipal();
                            userService.setUserStateToLeave(user.getId());
                            simpMessagingTemplate.convertAndSend("/topic/notification", "系统消息：用户【" + user.getNickname() + "】退出了聊天室");
                            resp.setContentType("application/json;charset=utf-8");
                            try (PrintWriter out = resp.getWriter()) {
                                out.write(new ObjectMapper().writeValueAsString(RespBean.ok("成功退出！")));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .permitAll()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpStatus.UNAUTHORIZED.value(), "未授权");
                        })
                );

        http.userDetailsService(userService);
        return http.build();
    }
}