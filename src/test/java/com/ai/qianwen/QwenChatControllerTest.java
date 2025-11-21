package com.ai.qianwen;

import com.ai.config.RestTemplateConfig;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@SpringBootTest
@Import(RestTemplateConfig.class) // 显式导入配置类
public class QwenChatControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = true)
    private RestTemplate restTemplate;

    @Test
    public void chat() {
        System.out.println(restTemplate);
    }

    @Test
    public void RAG() {
        String inputTexts = "衣服的质量杠杠的";
        Constants.apiKey = "sk-180606b601eb47f7806caa8a46ef8dfe";
        try {
            // 构建请求参数
            TextEmbeddingParam param = TextEmbeddingParam
                    .builder()
                    .model("text-embedding-v4")
                    // 输入文本
                    .texts(Collections.singleton(inputTexts))
                    .build();

            // 创建模型实例并调用
            TextEmbedding textEmbedding = new TextEmbedding();
            TextEmbeddingResult result = textEmbedding.call(param);

            // 输出结果
            System.out.println(result);

        } catch (NoApiKeyException e) {
            // 捕获并处理API Key未设置的异常
            System.err.println("调用 API 时发生异常: " + e.getMessage());
            System.err.println("请检查您的 API Key 是否已正确配置。");
            e.printStackTrace();
        }
    }
}