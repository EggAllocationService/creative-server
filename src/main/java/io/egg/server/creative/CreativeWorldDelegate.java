package io.egg.server.creative;

import io.egg.server.instances.InstanceManager;
import io.egg.server.loading.WorldManager;
import io.egg.server.profiles.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;

public class CreativeWorldDelegate extends DefaultProfileDelegate {
    String wn;
    Player owner;
    public CreativeWorldDelegate(String worldName, Player owner) {
        wn = worldName;
    }
    @Override
    public ProfileData getData() {
        return new ProfileData(wn, true);
    }

    @Override
    public void setupInstance(Instance i) {

    }

    public void saveAndDestroy() {
        getInstance().saveChunksToStorage(() -> {
            WorldManager.getWorld(wn).unloadAllChunks();
            InstanceManager.get().destroy(name);
            Runtime.getRuntime().gc();
        });
    }

    @EventHandler
    public void leave(PlayerLeaveProfileEvent e) {
        if (e.getPlayer() == owner) {
            //owner left instance, save and commit die

            saveAndDestroy();
        }
    }

    @EventHandler
    public void checkJoin(PlayerJoinProfileEvent e) {
        if (e.getP() == owner) return;
        if (!FriendManager.isFriend(owner, e.getP())) {
            e.setCancelled(true);
            e.setCancelReason("You are not friends with the owner of this world! Ask them to run /friend add " + e.getP().getUsername());
        }
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
