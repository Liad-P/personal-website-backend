package liad.dev.web.resource;


import jakarta.ws.rs.Produces;

import java.time.LocalTime;
import java.util.Random;

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

    Random randomNumberGen = new Random(LocalTime.now().toNanoOfDay());

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
        aiService.initializeChatSession();
        IChatSession chatSession = aiService.getChatSession();
        return new AIResponse(chatSession.sendMessage(prompt.prompt).getMessageText());
    }

    @Path("/create-chat")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AIResponse createChat(Prompt prompt) {

        // create ID for new chat session
        String sessionId = "session-" + randomNumberGen.nextInt(100_000);
        aiService.initializeChatSession(sessionId);
        IChatSession chatSession = aiService.getChatSession(sessionId);
        return new AIResponse(chatSession.sendMessage(prompt.prompt).getMessageText());
    }



    public record Prompt(String prompt) {}
    
    public record AIResponse(String AiResponse){}
    
}
