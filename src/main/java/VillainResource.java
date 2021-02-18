import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Villain;
import org.jboss.resteasy.reactive.RestSseElementType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/villains")
@Produces(MediaType.APPLICATION_JSON)
public class VillainResource {

    @GET
    @Path("/getrandom")
    public Uni<Villain> getRandomVillain() {
        return Villain.findRandom();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Villain> getAllVillains() {
        /*return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToMulti(x-> Multi.createFrom().items(Villain.villaiList.stream())).merge().select().first(10);*/

        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToMulti(x -> Multi.createFrom().items(Villain.findRandom().await().indefinitely(), Villain.findRandom().await().indefinitely())).merge().select().first(50);

    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType(MediaType.APPLICATION_JSON)
    public Multi<Villain> getVillainsStream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUni(x -> Villain.findRandom()).merge();
    }
}
