package liad.dev.web.resource;


import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import liad.dev.ai.chat.IChatSession;
import liad.dev.ai.client.AIClient;
import liad.dev.ai.services.AIService;

@Path("/api")
public class AIResources {

    @Inject
    AIClient aiClient;

    @Inject
    AIService aiService;

    @Path("/generate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AIResponse generateString(Prompt prompt) {
        return new AIResponse(aiClient.generate(prompt.prompt, null));
    }

    @Path("/chat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AIResponse chat(Prompt prompt) {
        aiService.initialize();
        IChatSession chatSession = aiService.getChatSession();
        return new AIResponse(chatSession.sendMessage(prompt.prompt).getMessageText());
    }

    public record Prompt(String prompt) {}
    
    public record AIResponse(String AiResponse){}
    
}
