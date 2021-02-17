package model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Villain /*extends PanacheEntity*/ {

    public String name;
    public int level;
    public String image;


    public static List<Villain> villaiList = new ArrayList<>() {{
        add(new Villain() {{
            name = "Artur";
            level = 1;
            image = "Artur image";
        }});
        add(new Villain() {{
            name = "Max";
            level = 2;
            image = "Max image";
        }});

    }};


    public static Uni<Villain> findRandom() {
        Random random = new Random();

        //with Panache
        /*return Villian.count()
                .onItem().transform(x->random.nextInt(x.intValue()))
                .onItem().transformToUni(index-> Villian.findAll().page(index,1).firstResult());*/

        return Uni.createFrom().item(villaiList.get(random.nextInt(villaiList.size())));
    }
}
