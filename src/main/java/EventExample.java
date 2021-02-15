import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import model.Greeting;
import service.Source;

import java.time.Duration;

public class EventExample {
    public static void main(String[] args) {

        //UNIs
        Uni<String> uni = Greeting.greeting("artur");

        uni
                .onItem().transform(item -> item.toUpperCase())
                // simulate timeout
                .onItem().delayIt().by(Duration.ofSeconds(3))
                // do fail
                /*.onItem().failWith(fail-> new NullPointerException() )*/
                // simulate error
                /*.onItem().transform(err -> {
            throw new NullPointerException("boom");
        })*/
                // comment this line if want print stacktrace
                .onFailure().recoverWithItem("Failure")
                .subscribe().with(result -> System.out.println(result), failure -> failure.printStackTrace());

        Uni<String> hello = uni;

        hello.subscribe().with(result -> System.out.println(result), failure -> failure.printStackTrace());
        hello.subscribe().with(result -> System.out.println(result), failure -> failure.printStackTrace());

        //Void uni
        Uni<Void> uniOfVoid = Uni.createFrom().voidItem();

        uniOfVoid.subscribe().with(result -> System.out.println("uniOfVOid: " + result));

        uni.map(x -> null)
                .subscribe().with(result -> System.out.println("uni mapped to void: " + result));
        //the same with transform
        uni.onItem().transform(x -> null)
                .subscribe().with(result -> System.out.println("uni transform to void: " + result));

        // MULTIs
        Multi<Double> multi = Source.getSource();
        multi.onItem().transform(d -> d * 0.55)
                .subscribe().with(d -> System.out.println("item is: " + d));
    }
}
