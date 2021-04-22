package io.egg.server.creative;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;

public class NameFormatter {
    public static TextColor PRIMARY = TextColor.color(0x49c98d);
    public static TextColor SECONDARY = TextColor.color(0x9449c9);
    public static String formattedWorldName(Player p, String name) {
        return "@" + p.getUuid().toString().replaceAll("-", "") + "/" + name;
    }
}
