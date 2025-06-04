package org.example.chatserver.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.chatserver.config.OllamaConfig;
import org.example.chatserver.service.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 使用 Ollama 本地模型（如 qwen2:7b）实现聊天机器人服务
 */
@Service
public class OllamaChatServiceImpl implements ChatBotService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private OllamaConfig ollamaConfig;

    @Override
    public String getBotResponse(String userMessage) {
        String apiUrl = ollamaConfig.getUrl();   // e.g. http://localhost:11434/api/chat
        String model = ollamaConfig.getModel();  // e.g. qwen2:7b

        try {
            // 构造 JSON 请求体
            ObjectNode root = MAPPER.createObjectNode();
            root.put("model", model);
            root.put("stream", true); // 开启流式响应（默认也是 true，但显式设置更稳妥）

            ArrayNode messages = MAPPER.createArrayNode();
            ObjectNode userMsg = MAPPER.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            root.set("messages", messages);

            String requestJson = MAPPER.writeValueAsString(root);

            // 发送 HTTP 请求
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestJson.getBytes(StandardCharsets.UTF_8));
            }

            // 处理响应：逐行读取流式内容并拼接
            if (conn.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder fullResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        JsonNode node = MAPPER.readTree(line);
                        if (node.has("message") && node.get("message").has("content")) {
                            fullResponse.append(node.get("message").get("content").asText());
                        }
                        if (node.has("done") && node.get("done").asBoolean()) {
                            break;
                        }
                    }
                    return fullResponse.toString();
                }
            } else {
                // 打印错误内容以便调试
                try (InputStream errorStream = conn.getErrorStream()) {
                    if (errorStream != null) {
                        String errorMsg = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                        System.err.println("Ollama 返回错误: " + errorMsg);
                    }
                }
                return "机器人服务返回错误，状态码：" + conn.getResponseCode();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "机器人服务异常，无法获取回复。";
        }
    }
}
