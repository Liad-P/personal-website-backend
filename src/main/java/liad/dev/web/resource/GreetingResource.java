package liad.dev.web.resource;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @ConfigProperty(name = "api.secret.key", defaultValue = "Unknown API key")
    String apiKey;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        System.out.println(apiKey);
        return "Hello from Quarkus REST";
    }
}
