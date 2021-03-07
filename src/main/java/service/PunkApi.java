package service;

import io.smallrye.mutiny.Uni;
import model.Beer;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@RegisterRestClient(baseUri = "https://api.punkapi.com/v2/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PunkApi {

    @GET
    @Path("/beers")
    Uni<List<Beer>> get(@QueryParam("page") int page);
}
