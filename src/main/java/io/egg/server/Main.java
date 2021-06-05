package io.egg.server;

import io.egg.server.commands.*;
import io.egg.server.creative.*;
import io.egg.server.database.Database;
import io.egg.server.generators.VoidWorldGenerator;
import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import io.egg.server.profiles.delegates.LobbyProfileDelegate;
import io.egg.server.skins.SkinManager;
import io.egg.server.tasks.InstanceNameTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSkinInitEvent;
import net.minestom.server.extras.PlacementRules;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.utils.Position;
import net.minestom.server.utils.time.TimeUnit;
import net.minestom.server.utils.time.UpdateOption;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        MinecraftServer m = MinecraftServer.init();
        MinecraftServer.getBenchmarkManager().enable(new UpdateOption(5, TimeUnit.TICK));
        MinecraftServer.setBrandName("EggServer");

        InstanceManager.init();
        Database.init("creative");
        MinecraftServer.getCommandManager().register(new SaveCommand());
        InstanceNameTask.init();
        MinecraftServer.getSchedulerManager().buildTask(new InstanceNameTask()).repeat(100, TimeUnit.MILLISECOND).schedule();

        MinecraftServer.getSchedulerManager().buildTask(() -> InstanceManager.get().tick()).repeat(1, TimeUnit.TICK).schedule();
        MinecraftServer.getBiomeManager().addBiome(VoidWorldGenerator.LOBBY);
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new GamemodeCommand());

        MinecraftServer.getCommandManager().register(new ExportWorldCommand());

        MinecraftServer.getCommandManager().register(new WorldCommand());
        MinecraftServer.getCommandManager().register(new LobbyCommand());
        MinecraftServer.getCommandManager().register(new FriendCommand());
        MinecraftServer.getCommandManager().register(new JoinCommand());

        MinecraftServer.setChunkViewDistance(6);


        OptifineSupport.enable();

        // placement rules
        PlacementRules.init();

        try {
            InstanceManager.get().spawn("lobby", new LobbyProfileDelegate());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            return;
        }

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(InstanceManager.get().getInstance("lobby"));
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlying(true);
            player.setRespawnPoint(new Position(0.5, 65, 0.5));
            if (!player.getUsername().startsWith("test")) {
                player.showBossBar(InstanceNameTask.memoryBossBar);

            }
        });
        globalEventHandler.addEventCallback(PlayerSkinInitEvent.class, event -> {
            event.setSkin(SkinManager.getName(event.getPlayer().getUsername()));
        });
        globalEventHandler.addEventCallback(PlayerDisconnectEvent.class, event -> {
            InstanceManager.get().playerLeave(event);
        });


        // TODO: This should NOT be shipped
       // MinecraftServer.getConnectionManager().setUuidProvider(MojangUUIDProvider::calculate);

        int port = 25565;
        if (System.getProperties().containsKey("EnableBungee")) {
            BungeeCordProxy.enable();
            System.out.println("Enabling BungeeCord support.");
        }
        if (System.getProperties().containsKey("port")) {
            port = Integer.parseInt(System.getProperty("port"));
        }


        System.out.println("Server listening on port: " + port);
        m.start("0.0.0.0", port, (playerConnection, responseData) -> {
            responseData.setOnline(69);
            responseData.setMaxPlayer(420);
            for (Player p : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                responseData.addPlayer(p);
            }
            responseData.setDescription(Component.text("Standard Testing Server Instance", TextColor.color(0xc13f6f)));
        });

    }
}
