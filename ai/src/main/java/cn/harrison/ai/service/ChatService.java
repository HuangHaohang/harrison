package cn.harrison.ai.service;

import cn.harrison.ai.config.ChatModelFactory;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.StreamingChatModel;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatModelFactory chatModelFactory;
    private final RedissonClient redissonClient;

    public ChatService(ChatModelFactory chatModelFactory, RedissonClient redissonClient) {
        this.chatModelFactory = chatModelFactory;
        this.redissonClient = redissonClient;
    }

    public String ensureThreadId(String threadId) {
        return StringUtils.hasText(threadId) ? threadId : UUID.randomUUID().toString();
    }

    public Flux<String> stream(String question, String modelId, String threadId) {
        String tid = ensureThreadId(threadId);
        DashScopeChatModel chatModel = chatModelFactory.getChatModel(modelId);

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage("你是一个有帮助的工程助手。回答应简洁、分点清晰，必要时给出简短示例。优先中文作答。"));

        List<ChatMessage> history = readHistory(tid);
        for (ChatMessage m : history) {
            if ("user".equals(m.role)) {
                messages.add(new UserMessage(m.content));
            } else if ("assistant".equals(m.role)) {
                messages.add(new AssistantMessage(m.content));
            }
        }

        messages.add(new UserMessage(question));

        Prompt prompt = new Prompt(messages);
        Flux<ChatResponse> responseFlux = ((StreamingChatModel) chatModel).stream(prompt);
        StringBuilder sb = new StringBuilder();
        return responseFlux
                .map(r -> r.getResult().getOutput().getText())
                .doOnNext(sb::append)
                .doOnComplete(() -> writeHistory(tid, question, sb.toString()));
    }

    private List<ChatMessage> readHistory(String threadId) {
        if (redissonClient == null) return List.of();
        RList<ChatMessage> list = redissonClient.getList("ai:chat:" + threadId);
        return new ArrayList<>(list);
    }

    private void writeHistory(String threadId, String userText, String assistantText) {
        if (redissonClient == null) return;
        RList<ChatMessage> list = redissonClient.getList("ai:chat:" + threadId);
        list.add(new ChatMessage("user", userText));
        list.add(new ChatMessage("assistant", assistantText));
    }

    

    public static class ChatMessage {
        public String role;
        public String content;

        public ChatMessage() {}

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
