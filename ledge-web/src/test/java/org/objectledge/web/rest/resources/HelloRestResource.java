package org.objectledge.web.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hello")
public class HelloRestResource {
    @GET
    @Produces("text/plain")
    public String getHelloMessage() {
        return "Hello World!";    
    }

}
