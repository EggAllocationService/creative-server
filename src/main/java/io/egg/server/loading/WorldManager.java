package io.egg.server.loading;



import io.egg.server.database.Database;

import javax.xml.crypto.Data;
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
            w.owner = "";
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
    public static boolean cached(String name) {
        return cache.containsKey(name);
    }
    public static boolean exists(String name) {
        return Database.getInstance().worlds.find(eq("_id", name)).first() != null;
    }
    public static void delete(String name) {
        if (cache.get(name) != null) {
            cache.get(name).unloadAllChunks();
            cache.remove(name);
        }

        Database.getInstance().worlds.deleteOne(eq("_id", name));
        Database.getInstance().worldChunks.deleteMany(eq("world", name));
    }


}
