package com.redhat.ssa.resources;

import java.util.List;
import java.util.Random;

import org.jboss.logging.Logger;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class GreetingsResource {

    private static final Logger LOG = Logger.getLogger(GreetingsResource.class);
    private static List<String> greetings = List.of("Hey", "Hello", "Good morning");

    @Path("/greetings")
    @GET
    public String greetings() {
        LOG.debug("greetings");
        Random random = new Random();
        int randomIndex = random.nextInt(greetings.size());
        return greetings.get(randomIndex);
    }

}