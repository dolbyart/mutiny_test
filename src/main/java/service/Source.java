package service;

import io.smallrye.mutiny.Multi;

import java.time.Duration;
import java.util.Random;


public class Source {
    private static Random random = new Random();

    public static Multi<Double> getSource() {
        int counter = 10;

        return Multi.createFrom().ticks().every(Duration.ofMillis(100))
                //.map(x->random.nextDouble());
                .onItem().transform(x -> random.nextDouble()).transform().byTakingFirstItems(counter)/*.select().first(counter)*/;
    }
}
