package com.ai.qianwen;

import com.aliyuncs.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qwen/chat")
public class QwenChatController {

    private final ChatClient chatClient;

    public QwenChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/base")
    public String chat(@RequestParam(name = "message") String message) {
        ChatClient.ChatClientRequestSpec prompt = this.chatClient.prompt();
        if (!StringUtils.isEmpty(message)) {
            message = "你好";
        }

        return prompt.user(message).call().content();
    }
}
