package liad.dev.ai.client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Part;

import io.quarkus.logging.Log;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import liad.dev.ai.chat.ChatSessionGemini;
import liad.dev.ai.chat.IChatSession;

@ApplicationScoped
public class AIGoogleClient implements AIClient {

    @ConfigProperty(name = "AI_API_KEY", defaultValue = "Unknown API key")
    String apikeyString;

    @ConfigProperty(name = "OWNER_NAME", defaultValue = "Unknown API key")
    String ownerName;
    
    private Client client;

    private final String modelString;
    
    AIGoogleClient(@ConfigProperty(name = "DEFAULT_LLM", defaultValue = "gemini-2.5-flash-lite") String AIModelString) {
        Log.info("Set Google model client to use model: " + AIModelString);
        this.modelString = AIModelString;
        Log.info(this.modelString);
    }

    @PostConstruct
    void initialize() {
        if (apikeyString == null || apikeyString.equals("Unknown API key")) {
            Log.error("API key is not set. Please configure 'AI_API_KEY' in your application properties.");
            throw new IllegalStateException(
                    "API key is not set. Please configure 'AI_API_KEY' in your application properties.");
        }
        Log.info("Initializing AIGoogleClient with API key.");
        Log.info(apikeyString);
        this.client = Client.builder().apiKey(apikeyString).build();
    }

    Chat initializeChat() {
        return this.initializeChat(this.modelString);
    }

    Chat initializeChat(String modelToUse) {
        Log.info("Initializing chat with model: " + modelToUse);
        GenerateContentConfig config = GenerateContentConfig.builder()
            .systemInstruction(Content.fromParts(Part.fromText(AIClient.generateSystemPrompt(
                        ownerName))))
                .build();
        return this.client.chats.create(modelToUse, config);
    }

    @Override
    public String generate(String prompt, Map<String, Object> options) {

        if (options != null && options.containsKey("model")) {
            return this.client.models.generateContent(options.get("model").toString(), prompt, null).text();
        }else{
            return this.client.models.generateContent(this.modelString, prompt, null).text();
        }
    }

    @Override
    public Stream<String> generateStream(String prompt, Map<String, Object> options) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateStream'");
    }

    @Override
    public Map<String, String> chat(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chat'");
    }

    @Override
    public Stream<Map<String, String>> chatStream(List<Map<String, String>> messages, Map<String, Object> options) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chatStream'");
    }

    @Override
    public CompletableFuture<String> generateAsync(String prompt, Map<String, Object> options) {
         CompletableFuture<String> completableFuture =CompletableFuture.supplyAsync(() -> {
            if (options != null && options.containsKey("model")) {
                return this.client.models.generateContent(options.get("model").toString(), prompt, null).text();
            } else {
                return this.client.models.generateContent(this.modelString, prompt, null).text();
            }
        });

        return completableFuture;
    }

    @Override
    public CompletableFuture<Map<String, String>> chatAsync(List<Map<String, String>> messages,
            Map<String, Object> options) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chatAsync'");
    }

    @Override
    public List<Float> embed(String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'embed'");
    }

    @Override
    public List<List<Float>> embedBatch(List<String> texts) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'embedBatch'");
    }

    @Override
    public IChatSession createChatSession() {
        return new ChatSessionGemini(this.initializeChat());
    }

    @Override
    public IChatSession createChatSession(String sessionId) {
        return new ChatSessionGemini(this.initializeChat(), sessionId);
    }
    
}
