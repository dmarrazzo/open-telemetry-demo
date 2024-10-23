package com.redhat.ssa.resources;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.redhat.ssa.client.GreetingsClient;
import com.redhat.ssa.client.WeatherClient;
import com.redhat.ssa.model.Person;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public class FrontResource {

    private static final Logger LOG = Logger.getLogger(FrontResource.class);

    @Inject
    @RestClient
    GreetingsClient greetings;

    @Inject
    @RestClient
    WeatherClient weather;

    @GET
    @Transactional
    public String hello(@QueryParam("name") String name) {
        LOG.info("hello "+name);

        String greetingsText = greetings.greetings();

        // store the name
        if (name != null && ! name.isEmpty()) {
            greetingsText += " " + name;

            PanacheQuery<Person> query = Person.find("name", name);
            
            if (query.count()==0) {
                Person person = new Person();
                person.name = name;
                person.persist();
            }
        }
        greetingsText += ", today is " + weather.weather();

        return greetingsText;
    }
}