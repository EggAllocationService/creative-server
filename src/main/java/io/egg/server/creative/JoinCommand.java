package io.egg.server.creative;

import io.egg.server.instances.InstanceManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JoinCommand extends Command {
    public JoinCommand() {
        super("join");
        var playerArg = ArgumentType.Entity("target")
                .onlyPlayers(true)
                .singleEntity(true);
        addSyntax(this::run, playerArg);
    }
    public void run(CommandSender sender, CommandContext context) {
        Player target = ((EntityFinder) context.get("target")).findFirstPlayer(sender);
       //sender.asPlayer().setInstance(target.getInstance(), target.getPosition());
        InstanceManager.get().transfer(sender.asPlayer(), (InstanceContainer) target.getInstance());
    }
}
