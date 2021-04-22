package io.egg.server.creative;

import io.egg.server.instances.InstanceManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;


public class LobbyCommand extends Command {
    public LobbyCommand() {
        super("lobby");
        setDefaultExecutor((sender, context) -> {
            Player p = sender.asPlayer();
            if (p.getInstance() == InstanceManager.get().getInstance("lobby")) return;
            InstanceManager.get().transfer(p, InstanceManager.get().getInstance("lobby"));
        });
    }
}
