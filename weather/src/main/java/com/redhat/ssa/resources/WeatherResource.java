package com.redhat.ssa.resources;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
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
    private AtomicLong counter = new AtomicLong(0);

    @Path("/weather")
    @GET
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 5000)
    public String weather() {
        LOG.info("weather invoked");
        Random random = new Random();
        int randomIndex = random.nextInt(weather.size());
        maybeFail();
        LOG.debug(randomIndex);
        return weather.get(randomIndex);
    }

    private void maybeFail() {
        // introduce some artificial failures
        LOG.info("maybeFail");
        final Long invocationNumber = counter.getAndIncrement();
        if (invocationNumber % 4 > 1) { // alternate 2 successful and 2 failing invocations
            LOG.error("simulated failure");
            throw new RuntimeException("Service failed.");
        }
    }
}