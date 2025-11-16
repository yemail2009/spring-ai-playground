package com.ai.qianwen;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.aliyuncs.utils.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/qwen/chat")
public class QwenChatController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ChatClient chatClient;

    // 预置的「知乎高赞」风格系统提示词模板
    private final PromptTemplate zhihuStyleTemplate = new PromptTemplate(
            """
            你是一位知乎高赞答主，请按以下规范用中文回答：
            -先给出1;2句话的明确结论（置顶）。
            - 然后分点阐述，每点包含：观点->逻辑链->例证或数据。
            - 如存在争议，简要列出不同观点及适用场景。
            -最后给出可执行建议或检查清单。
            -风格：专业、克制、结构清晰，避免堆砌术语。
            - 允许使用Markdown 标题（##）与列表，但避免过度装饰。
            -禁止杜撰事实；不确定处请标注假设或推测。
            -回答必须围绕用户问题展开，不进行无关延伸。
            用户问题：｛question｝
        """
    );

//    public QwenChatController(ChatClient.Builder chatClient) {
//        this.chatClient = chatClient.build();
//    }

    @GetMapping("/base")
    public String chat(@RequestParam(name = "message") String message) {

        System.out.println(restTemplate);

        ChatClient.ChatClientRequestSpec prompt = this.chatClient.prompt();
        if (StringUtils.isEmpty(message)) {
            message = "你好";
        }

        long l = System.currentTimeMillis();
        String content = prompt
                .user(message)
                .system("寒楚曾就职于思科集团，是一个程序员！")
                .call()
                .content();
        long l1 = System.currentTimeMillis() - l;
        System.out.println("返回时间："+ l1);

        return content;
    }

    @GetMapping(value = "/stream", produces = "text/markdown;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam(name = "message") String message) {

        ChatClient.ChatClientRequestSpec prompt = this.chatClient.prompt();
        if (StringUtils.isEmpty(message)) {
            return null;
        }

        long l = System.currentTimeMillis();
        Flux<String> content = prompt
                .user(message)
                .stream()
                .content();
        long l1 = System.currentTimeMillis() - l;
        System.out.println("stream call 返回时间："+ l1);

        return content;
    }

    @GetMapping(value = "/text2image", produces = "text/image;charset=UTF-8")
    public Flux<String> text2image(@RequestParam(name = "message") String message) {

        // 设置parameters参数
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("prompt_extend", true);
        parameters.put("watermark", false);
        parameters.put("seed", 12345);

//        ImageSynthesisParam param =
//                ImageSynthesisParam.builder()
//                        .apiKey(apiKey)
//                        .model("wan2.5-t2i-preview")
//                        .prompt("一间有着精致窗户的花店，漂亮的木质门，摆放着花朵")
//                        .n(1)
//                        .size("1024*1024")
//                        .negativePrompt("")
//                        .parameters(parameters)
//                        .build();
//
//        ImageSynthesis imageSynthesis = new ImageSynthesis();
//        ImageSynthesisResult result = null;
//        try {
//            System.out.println("---sync call, please wait a moment----");
//            result = imageSynthesis.call(param);
//        } catch (ApiException | NoApiKeyException e){
//            throw new RuntimeException(e.getMessage());
//        }
//        System.out.println(JsonUtils.toJson(result));
        return null;
    }

    @GetMapping("/answer")
    public String zhihuAnswer(@RequestParam(name = "message") String message,
                              @RequestParam(name = "model", required = false) String model) {

        if (!StringUtils.isEmpty(message)) {
            message = "如何评价微服务对团队协作效率的影响";
        }

        String system = zhihuStyleTemplate.render(Map.of("question", message));
        ChatClient.ChatClientRequestSpec promt = this.chatClient
                .prompt()
                .system(system)
                .user(message);

        String effectiveModel = StringUtils.isEmpty(model) ? "qwen-plus" : model;

        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(effectiveModel)
                .build();

        promt = promt.options(options);

        return promt.call().content();
    }
}
