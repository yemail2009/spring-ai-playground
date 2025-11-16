package com.ai.qianwen;

import com.ai.config.AliyunBailianConfig;
import com.ai.service.AliyunBailianService;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@Slf4j
public class BailianAIController {

    @Autowired
    private AliyunBailianService bailianService;

    @Autowired
    private AliyunBailianConfig aliyunBailianConfig;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prpromptompt");
            String response = bailianService.callQwen(prompt);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("对话处理失败", e);

            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(500).body(result);
        }
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@RequestBody Map<String, String> request) {
        SseEmitter emitter = new SseEmitter(120000L); // 2分钟超时

        String prompt = request.get("prompt");

        bailianService.callQwenStream(prompt, chunk -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("content", chunk);
                data.put("timestamp", System.currentTimeMillis());

                emitter.send(SseEmitter.event().data(data).id(UUID.randomUUID().toString()));
            } catch (IOException e) {
                log.error("SSE发送失败", e);
            }
        });

        emitter.onCompletion(() -> log.info("SSE连接完成"));
        emitter.onTimeout(() -> log.warn("SSE连接超时"));
        emitter.onError(throwable -> log.error("SSE连接错误", throwable));

        return emitter;
    }

    @GetMapping(value = "/text/image", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public String text2ImageStreamChat(@RequestParam(name = "message") String message) {
        if (StringUtils.isEmpty(message)) {
            message = "近景镜头，18岁的中国女孩"+message+"，现代服饰，瓜子脸，长发及腰，坐在新疆喀纳斯湖边";
        }
        ImageSynthesisParam param = ImageSynthesisParam.builder()
                .apiKey(aliyunBailianConfig.getApiKey())
                .model(ImageSynthesis.Models.WANX_V1)
                .prompt(message)
                .style("<watercolor>")
                .n(1)
                .size("1024*1024")
                .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));

        return JsonUtils.toJson(result);
    }
}
