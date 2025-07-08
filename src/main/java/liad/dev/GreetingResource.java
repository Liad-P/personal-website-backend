package liad.dev;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    @ConfigProperty(name = "api.secret.key")
    String apiKey;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        System.out.println(apiKey);
        return "Hello from Quarkus REST";
    }

    @Path("/api")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getAPIKey() {
        return apiKey;
    }
}
