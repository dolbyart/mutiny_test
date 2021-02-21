import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import service.PlaygroundRestService;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class PlayGroundResource {

    @Inject @RestClient
    PlaygroundRestService playgroundRestService;

    @GET
    public Uni<String> playground(){
        System.out.print("=============");
        //return Uni.createFrom().item("hello");
        return playgroundRestService.mirror("Artur")
                .onItem().invoke(item -> System.out.println("Got response for hello: " + item))
                .onItem().invoke(item -> playgroundRestService.mirror("foo"));
    }
}
