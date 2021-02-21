import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Fight;
import model.Hero;
import model.Villain;
import org.jboss.resteasy.annotations.SseElementType;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SupesResource {

    // Hero and Villain methods
    @GET
    @Path("/hero")
    public Uni<Hero> hero() {
        return Hero.findRandom();
    }

    @GET
    @Path("/villain")
    public Uni<Villain> villain() {
        return Villain.findRandom();
    }

    // Stream of heroes
    @GET
    @Path("/heroes")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Multi<Hero> stream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUni(x -> hero()).merge();
    }

    @GET
    @Path("/villain/getall")
    public Multi<Villain> getAllVillains() {
        /*return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToMulti(x-> Multi.createFrom().items(Villain.villaiList.stream())).merge().select().first(10);*/

        //Blocking way
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToMulti(x -> Multi.createFrom().items(Villain.findRandom().await().indefinitely(), Villain.findRandom().await().indefinitely())).merge().transform().byTakingFirstItems(10);/*.select().first(10);*/
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    @Path("/villain/randomtwo/stream")
    public Multi<List<Villain>> getRandomViilansStream() {

        //Non Blocking way
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUni(x-> Uni.combine().all().unis(villain(), villain())
                        .combinedWith(((villain, villain2) -> {
                            List<Villain> villainList = new ArrayList<>();
                            villainList.add(villain);
                            villainList.add(villain2);
                            return villainList;
                        }))).merge().transform().byTakingFirstItems(10);/*.select().first(10);*/

        /*return Uni.combine().all().unis(villainUni,villainUni2)
                .combinedWith((villain, villain2) -> {
                    List<Villain> villainList = new ArrayList<>();
                    villainList.add(villain);
                    villainList.add(villain2);
                    return villainList;
                });*/
    }

    @GET
    @Path("/villain/randomtwo")
    public Uni<List<Villain>> getRandomViilans() {

        Uni<Villain> villainUni = villain();
        Uni<Villain> villainUni2 = villain();

        return Uni.combine().all().unis(villainUni,villainUni2)
                .combinedWith((villain, villain2) -> {
                    List<Villain> villainList = new ArrayList<>();
                    villainList.add(villain);
                    villainList.add(villain2);
                    return villainList;
                });
    }

    @GET
    @Path("/fight")
    public Uni<Fight> fight() {

        //Non blocking way
        // Retrieve hero and villain
        Uni<Hero> hero = hero()
                .ifNoItem().after(Duration.ofMillis(500)).fail()
                .onFailure().recoverWithItem(Hero.FALLBACK);

        Uni<Villain> villain = villain()
                .ifNoItem().after(Duration.ofMillis(500)).fail()
                .onFailure().recoverWithItem(Villain.FALLBACK);

        // Combine both and call computeFightOutcome
        return Uni.combine().all().unis(hero, villain)
                .combinedWith((hero_cb, villain_cb) -> {
                    Fight fight = new Fight();
                    fight.hero = hero_cb;
                    fight.villain = villain_cb;
                    return fight;
                });
    }

    @GET
    @Path("/fight/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Multi<Fight> fightStream() {

        Multi<Long> ticks = Multi.createFrom().ticks()
                .every(Duration.ofSeconds(1));
        return ticks
                .onItem().transformToUniAndMerge(x -> fight());
    }

    @GET
    @Path("/villain/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @SseElementType(MediaType.APPLICATION_JSON)
    public Multi<Villain> getVillainsStream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onItem().transformToUni(x -> Villain.findRandom()).merge();
    }
}
