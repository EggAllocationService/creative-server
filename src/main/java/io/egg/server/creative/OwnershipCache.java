package io.egg.server.creative;

import io.egg.server.database.Database;
import io.egg.server.loading.World;

import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class OwnershipCache {
    public static HashMap<String, ArrayList<String>> cache = new HashMap<>();
    public static ArrayList<String> getWorlds(String name) {
        if (cache.containsKey(name)) return cache.get(name);
        update(name);
        return cache.get(name);

    }
    private static void update(String name) {
        cache.remove(name);
        cache.put(name, new ArrayList<>());
        for (World w : Database.getInstance().worlds.find(eq("owner", name))) {
            cache.get(name).add(w.id);
        }
    }
    public static void poke(String name) {
        update(name);
    }
}
