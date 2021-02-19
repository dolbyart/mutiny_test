package model;

import io.smallrye.mutiny.Uni;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hero /*extends PanacheEntity*/{

    public String name;
    public int level;
    public String image;

    public static final Hero FALLBACK;

    static {
        FALLBACK = new Hero();
        FALLBACK.name = "Donatello (fallback)";
        FALLBACK.image = "Fallback image";
        FALLBACK.level = 15;
    }

    public static List<Hero> heroList = new ArrayList<>() {{
        add(new Hero() {{
            name = "Damian";
            level = 1;
            image = "Damian image";
        }});
        add(new Hero() {{
            name = "Ruby";
            level = 2;
            image = "Ruby image";
        }});

    }};

    public static Uni<Hero> findRandom() {
        Random random = new Random();

        //with Panache
        /*return Hero.count()
                .map(l -> random.nextInt(l.intValue()))
                .flatMap(index -> {
                    return Hero.findAll().page(index, 1).firstResult();
                });*/

        return Uni.createFrom().item(heroList.get(random.nextInt(heroList.size())));
    }
}
