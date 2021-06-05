package io.egg.server.creative;


import io.egg.server.instances.InstanceManager;
import io.egg.server.instances.ProfiledInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;

import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;

import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.utils.entity.EntityFinder;


import java.util.HashSet;
import java.util.UUID;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "f");

        var friendTarget = ArgumentType.String("target");
        friendTarget.setSuggestionCallback((sender, context, suggestion) -> {
           var p = sender.asPlayer();
           HashSet<String> friends = FriendManager.getFriends(p);
           if (friends != null) {
               for (var f: friends) {
                   suggestion.addEntry(new SuggestionEntry((f)));
               }
           }
        });
        var litRemove = ArgumentType.Literal("remove");
        addSyntax(this::remove, litRemove, friendTarget);

        // adding friends
        var target = ArgumentType.Entity("player")
                .onlyPlayers(true)
                .singleEntity(true);
        var litAdd = ArgumentType.Literal("add");

        addSyntax(this::add, litAdd, target);
    }


    public void add(CommandSender sender, CommandContext context) {
        Player p = sender.asPlayer();
        Player target = ((EntityFinder) context.get("player")).findFirstPlayer(sender);
        if (FriendManager.addFriend(p, target)) {
            p.sendMessage(Component.text("Added " + target.getUsername() + " to your friends list", NameFormatter.PRIMARY));
            target.sendMessage(Component.text(p.getUsername() + " added you to their friends list", NameFormatter.PRIMARY));
        } else {
            p.sendMessage(Component.text("Could not add that person to your friends list (Already friends?)", TextColor.color(255, 0,0)));
        }
    }



    public void remove(CommandSender sender, CommandContext context) {
        Player p =sender.asPlayer();
        if (FriendManager.removeFriend(p, context.get("target"))) {
            sender.sendMessage(Component.text(context.get("target") + " has been removed from your friends list", NameFormatter.PRIMARY));
            if (MinecraftServer.getConnectionManager().getPlayer((String) context.get("target")) != null) {
                // player is online, kick him from world if thingy
                Player target = MinecraftServer.getConnectionManager().getPlayer((String) context.get("target"));
                ProfiledInstance pi = InstanceManager.get().getProfile((InstanceContainer) target.getInstance());
                if (pi == null) return;
                if (pi.getDelegate() instanceof CreativeWorldDelegate) {
                    // player is in a creative world
                    CreativeWorldDelegate dp = (CreativeWorldDelegate) pi.getDelegate();

                    if (dp.owner.getUuid().equals(p.getUuid())) {
                        // player was in the command sender's instance, kick
                        InstanceManager.get().transfer(target, InstanceManager.get().getInstance("lobby"));
                    };
                }
            }
        }
    }



}
