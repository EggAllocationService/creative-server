package io.egg.server.tasks;


import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.HSVLike;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;


public class InstanceNameTask implements Runnable{
    public static BossBar memoryBossBar;
    public static final TextColor PRIMARY_COLOR = TextColor.color(0xa36bd2);
    public static final TextColor SECOND_COLOR = TextColor.color(0xbacd5f);
    public static void init() {
        memoryBossBar = BossBar.bossBar(Component.text("Memory: 0/0 MB"), 0.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }
    int h = 0;
    @Override
    public void run() {
        Component s = MinecraftServer.getBenchmarkManager().getCpuMonitoringMessage();
        if (h == 360) {
            h = -1;

        }
        h = h + 1;
        Component help = Component.text("Send Help Please", TextColor.color(HSVLike.of(h,1.0f, 1.0f)));
        for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
          /*  ProfiledInstance s = InstanceManager.get().getProfile((InstanceContainer) p.getInstance());
            if (s == null) continue;
            Component t = Component.text("You are playing ", TextColor.color(0x23bbf3))
                    .append(
                            Component.text(s.getDelegate().getName(), TextColor.color(0x8a329b))
                    )
                    .append(
                            Component.text(" on instance ", TextColor.color(0x23bbf3))
                    )
                    .append(
                            Component.text(s.getName(),TextColor.color(0x8a329b))

                    );*/

            if (!p.getUsername().startsWith("test")) {

                p.sendPlayerListHeaderAndFooter(help, s);

            }

        }

        long maxMemoryMB = Runtime.getRuntime().maxMemory() / (1024 * 1024);
        long usedMemoryMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024);
        memoryBossBar.name(Component.text("Memory usage: ", PRIMARY_COLOR)

                .append(Component.text(usedMemoryMB, SECOND_COLOR))
                .append(Component.text("/", PRIMARY_COLOR))
                .append(Component.text(maxMemoryMB, SECOND_COLOR))
                .append(Component.text(" MB", PRIMARY_COLOR))
        );
        memoryBossBar.progress((float) usedMemoryMB / (float) maxMemoryMB);


    }

}
