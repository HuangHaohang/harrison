package cn.harrison.ai.config;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.stereotype.Component;

@Component
public class ChatModelFactory {
    private final DashScopeApi dashScopeApi;

    public ChatModelFactory(DashScopeApi dashScopeApi) {
        this.dashScopeApi = dashScopeApi;
    }

    public DashScopeChatModel getChatModel(String modelId) {
        String mid = (modelId == null || modelId.isBlank()) ? "qwen3-max" : modelId;
        DashScopeChatOptions options = DashScopeChatOptions.builder()
                .withModel(mid)
                .withTemperature(0.7)
                .withMaxToken(2000)
                .withTopP(0.9)
                .build();
        return DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(options)
                .build();
    }
}
