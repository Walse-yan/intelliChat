package org.example.chatserver;

import org.example.chatserver.api.entity.Admin;
import org.example.chatserver.dao.AdminDao;
import org.example.chatserver.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminServiceTest {
    @Autowired
    private AdminServiceImpl adminService; // Inject the mock into the service implementation

    @Test
    void testInsertAdmin() {
        // Arrange: Create a mock Admin object
        String password = "yanjq2024";
        // 用 BCryptPasswordEncoder()加密
        String password_bcrypt = new BCryptPasswordEncoder().encode(password);
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setNickname("管理员");
        admin.setPassword(password_bcrypt);
//        admin.setPassword(password);
        admin.setUserProfile("http:100.100.53.107:8889/group1/M00/00/00/ZGQ1a2g5lyOAZufgAAC-watWw_Y189.png");

        adminService.insert(admin);
    }
}