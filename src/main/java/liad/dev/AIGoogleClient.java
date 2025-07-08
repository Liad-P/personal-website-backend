package liad.dev;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import com.google.genai.Client;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AIGoogleClient implements AIClient {

    @ConfigProperty(name = "ai.api.key", defaultValue = "Unknown API key")
    String apikeyString;

    private Client client;
    private final String modelString;

    AIGoogleClient() {
        this.modelString = "gemini-2.0-flash-001";
    }

    @PostConstruct
    void initialize() {
        this.client = Client.builder().apiKey(apikeyString).build();
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
    
}
