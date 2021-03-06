package model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Greeting extends PanacheEntity {

    public String message;

    public static Uni<String> greeting(String name) {
        return Uni.createFrom().item("Hello " + name);
    }

    public static Uni<Greeting> findRandom() {
        Random random = new Random();

        // Mutiny style
       /* return Greeting.count()
                .onItem().transform(x-> random.nextInt(x.intValue()))
                .onItem().transformToUni(index ->{
                    return Greeting.findAll().page(index,1).firstResult();
                });*/

        //JavaRx style
        return Greeting.count()
                .map(x -> random.nextInt(x.intValue()))
                .flatMap(index -> {
                    return Greeting.findAll().page(index, 1).firstResult();
                });
    }
}
