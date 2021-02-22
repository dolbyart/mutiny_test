import io.smallrye.mutiny.Multi;
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
import java.time.Duration;

@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class PlayGroundResource {

    @Inject
    @RestClient
    PlaygroundRestService playgroundRestService;

    @GET
    @Path("/teen")
    public Uni<String> teen(){
        System.out.println("=============");

        /*return playgroundRestService.hello()
                .onItem().invoke(item -> System.out.println("Got response for teen: " + item))
                .ifNoItem().after(Duration.ofMillis(500)).recoverWithItem("timout")
                .onFailure().recoverWithItem("failure");*/

        return playgroundRestService.hello()
                .onSubscribe().invoke(x-> System.out.println("subscribing"))
                .onItem().invoke(item -> System.out.println("Got response for teen: " + item))
                .ifNoItem().after(Duration.ofMillis(500))
                .fail()
                .onFailure().retry().indefinitely();


        /*return Uni.createFrom().deferred(() -> playgroundRestService.hello())
                .onSubscribe().invoke(item -> System.out.println("Subscribing"))
                 .onItem().invoke(item -> System.out.println("Got response for teen: " + item))
                .ifNoItem().after(Duration.ofMillis(500))
                .fail()
                .onFailure().retry().indefinitely();*/
    }

    @GET
    @Path("allhi")
    public Multi<String> allPlayground() {
        System.out.println("=============");

        Multi<String> items = Multi.createFrom().items("hello", "privet", "hola");

        //The concatenate in order, merge unordered
        /*return items.onItem().transformToUni(x->playgroundRestService.mirror(x)).concatenate();*/
        /* return items.onItem().transformToUniAndConcatenate(x -> playgroundRestService.mirror(x));*/
        /*return items.onItem().transformToUni(x->playgroundRestService.mirror(x)).merge(1);*/
        //all three above do the same, because merge(1) will use only one rest pool at the time

        // when use merge() mean use all rest pool
        /*return items.onItem().transformToUni(x->playgroundRestService.mirror(x)).merge();*/

        //
        return items.onItem()
                .transformToUni(x->playgroundRestService.mirror(x))
                // without collectFailures calls will stop on first failure
                .collectFailures()
                .merge(1);
    }

    @GET
    public Uni<String> playground() {
        System.out.println("=============");

        return playgroundRestService.mirror("Artur")
                .onItem().invoke(response -> System.out.println("Got response 1: " + response))
                .onItem().transformToUni(item -> playgroundRestService.mirror(item)
                        .onItem().invoke(response -> System.out.println("Got response 2: " + response)));

        /*Uni<String> m1 = playgroundRestService.mirror("Artur")
                .onItem().invoke(item -> System.out.println("Got response for hello: " + item))
                //.onItem().invoke(res -> playgroundRestService.mirror("foo"))
                .onItem().transform(x -> x.toUpperCase());

        Uni<String> m2 = playgroundRestService.mirror("Garaev")
                .onItem().invoke(item -> System.out.println("Got response for hello: " + item));

        return Uni.combine().any().of(m1, m2);*/

       /* return Uni.combine().all().unis(m1, m2)
                .asTuple()
                .onItem().transform(objects ->
                        objects.getItem1() + " " + objects.getItem2());*/

        /*return Uni.combine().all().unis(m1,m2)
                .combinedWith((item1,item2) ->{
                    return item1+item2;
                });*/
    }
}
