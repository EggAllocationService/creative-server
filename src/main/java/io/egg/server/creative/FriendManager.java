package io.egg.server.creative;

import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class FriendManager {
    public static HashMap<UUID, HashSet<String>> friends = new HashMap<>();

    public static boolean addFriend(Player p, Player o) {
        if (!friends.containsKey(p.getUuid())) {
            friends.put(p.getUuid(), new HashSet<>());
        }
        if (friends.get(p.getUuid()).contains(o.getUsername())) {
            return false; // already added as a friend
        }
        friends.get(p.getUuid()).add(o.getUsername());
        return true;
    }

    public static boolean isFriend(Player p, Player o) {
        if (!friends.containsKey(p.getUuid())) {
            return false;
        }
        if (!friends.get(p.getUuid()).contains(o.getUsername())) return false;
        return true;
    }
}
