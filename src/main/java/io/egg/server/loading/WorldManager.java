package io.egg.server.loading;



import io.egg.server.database.Database;

import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

public class WorldManager {
    static HashMap<String, World> cache = new HashMap<>();
    public static World getWorld(String name) {
        if (cache.containsKey(name)) return cache.get(name);
        World w = Database.getInstance().worlds.find(eq("_id", name)).first();
        if (w == null)  {
            w = new World();
            w.id = name;
            Database.getInstance().worlds.insertOne(w);
        }
        cache.put(name, w);
        return w;
    }
    public static World create(String name, String owner) {
        if (cache.containsKey(name)) return cache.get(name);
        World w = Database.getInstance().worlds.find(eq("_id", name)).first();
        if (w == null)  {
            w = new World();
            w.id = name;
            w.owner = owner;
            Database.getInstance().worlds.insertOne(w);
        }
        w.owner = owner;
        cache.put(name, w);
        return w;
    }


}
