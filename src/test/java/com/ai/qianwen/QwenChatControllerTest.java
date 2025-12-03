package com.ai.qianwen;

import com.ai.config.RestTemplateConfig;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import dev.ai4j.openai4j.embedding.EmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;


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

    @Test
    public void vectorToStore() {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

            AllMiniLmL6V2EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        TextSegment segment1 = TextSegment.from("预订航班：\n" +
                "•通过我们的网站或移动麼用程序预订。\n" +
                "• 预订时需要全额付款。\n" +
                "- 确保个人信息（姓名、ID等）的准确性，因为更正可能会产生 25的费用。");
        dev.langchain4j.data.embedding.@NonNull Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("取消预订：\n" +
                "- 最晚在航班起飞前48小时\n" +
                "- 取消费用：经济舱75美元，豪华经济舱 50美元，商务舱25美元\n" +
                "- 退款将在7个工作日内处理");
        dev.langchain4j.data.embedding.@NonNull Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        TextSegment segment3 = TextSegment.from("补偿策略：\n" +
                "- a 补偿\n" +
                "- b 补偿\n" +
                "- c 补偿");
        dev.langchain4j.data.embedding.@NonNull Embedding embedding3 = embeddingModel.embed(segment3).content();
        embeddingStore.add(embedding3, segment3);

        @NonNull Embedding queryEmbedding = embeddingModel.embed("补偿").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);

        System.out.println(embeddingMatch.score()); // 0.8144288515898701
        System.out.println(embeddingMatch.embedded().text()); // I like football.
    }
}