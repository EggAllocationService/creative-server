package io.egg.server.creative;

import net.minestom.server.network.player.PlayerConnection;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MojangUUIDProvider {
    public static UUID calculate(PlayerConnection conn, String username) {
        return UUID.nameUUIDFromBytes(username.getBytes(StandardCharsets.UTF_8));
    }
}
