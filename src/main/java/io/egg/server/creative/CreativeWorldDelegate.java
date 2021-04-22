package io.egg.server.creative;

import io.egg.server.instances.InstanceManager;
import io.egg.server.loading.WorldManager;
import io.egg.server.profiles.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import sun.misc.Unsafe;

public class CreativeWorldDelegate extends DefaultProfileDelegate {
    String wn;
    Player owner;
    public CreativeWorldDelegate(String worldName, Player ow) {
        wn = worldName;
        owner = ow;
    }
    @Override
    public ProfileData getData() {
        return new ProfileData(wn, true);
    }

    @Override
    public void setupInstance(Instance i) {
        i.setChunkGenerator(new FlatWorldGenerator());
        i.getWorldBorder().setDiameter(99);
        i.getWorldBorder().setCenter(0, 0);
    }

    public void saveAndDestroy(Player ignore) {
        getInstance().saveChunksToStorage(() -> {
            WorldManager.getWorld(wn).unloadAllChunks();
            InstanceManager.get().destroy(name, ignore);
            Runtime.getRuntime().gc();
        });
    }

    @EventHandler
    public void leave(PlayerLeaveProfileEvent e) {
        if (e.getPlayer() == owner) {
            //owner left instance, save and commit die

            getInstance().scheduleNextTick(instance -> {
                saveAndDestroy(e.getPlayer());
            });
        }
    }

    @EventHandler
    public void checkJoin(PlayerJoinProfileEvent e) {
        if (e.getP() != owner && !FriendManager.isFriend(owner, e.getP())) {
            e.setCancelled(true);
            e.setCancelReason("You are not friends with the owner of this world! Ask them to run /friend add " + e.getP().getUsername());
            return;
        }
        e.getP().setGameMode(GameMode.CREATIVE);
        e.getP().getInventory().clear();

    }

    @Override
    public void placeBlock(PlayerBlockPlaceEvent e) {

    }

    @Override
    public void removeBlock(PlayerBlockBreakEvent e) {

    }

    @Override
    public String getName() {
        return "Creative Editor";
    }
}
