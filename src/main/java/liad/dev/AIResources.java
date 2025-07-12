package liad.dev;

import org.jboss.resteasy.reactive.RestForm;

import jakarta.ws.rs.Produces;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class AIResources {

    @Inject
    AIClient aiClient;

    @Path("/generate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AIResponse generateString(Prompt prompt) {
        return new AIResponse(aiClient.generate(prompt.prompt, null));
    }

    public record Prompt(String prompt) {}
    
    public record AIResponse(String AiResponse){}
    
}
