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
public class WeatherResource {

    private static final Logger LOG = Logger.getLogger(WeatherResource.class);

    private static List<String> weather = List.of("Sunny", "Cloudy", "Rainy", "Mild", "Clear");

    @Path("/weather")
    @GET
    public String weather() {
        LOG.info("weather");
        Random random = new Random();
        int randomIndex = random.nextInt(weather.size());
        LOG.debug(randomIndex);
        return weather.get(randomIndex);
    }

}