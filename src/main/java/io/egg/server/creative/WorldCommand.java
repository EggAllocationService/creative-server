package io.egg.server.creative;

import io.egg.server.instances.InstanceManager;
import io.egg.server.loading.World;
import io.egg.server.loading.WorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionCallback;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class WorldCommand extends Command {
    public WorldCommand() {
        super("world", "w");
        var worldArgument = ArgumentType.String("world");
        worldArgument.setSuggestionCallback((sender, context, suggestion) -> {
            Player p = sender.asPlayer();
            for (String s : OwnershipCache.getWorlds(p.getUuid().toString())) {
                suggestion.addEntry(new SuggestionEntry(s.split("/")[1]));
            }
        });
        var stringArg = ArgumentType.String("worldName");
        var create = ArgumentType.Literal("create");
        var edit = ArgumentType.Literal("edit");
        var list = ArgumentType.Literal("list");
        var delete = ArgumentType.Literal("delete");

        addSyntax(this::create, create, stringArg);
        addSyntax(this::load, edit, worldArgument);
        addSyntax(this::list, list);
        addSyntax(this::delete, delete, worldArgument);

    }

    public void list(CommandSender sender, CommandContext context) {
        ArrayList<String> worlds = OwnershipCache.getWorlds(sender.asPlayer().getUuid().toString());
        Component base = Component.text("Worlds (" + worlds.size() + "): ", NameFormatter.PRIMARY);
        for (String s : worlds) {
            base = base.append(
                    Component.text(s.split("/")[1], NameFormatter.SECONDARY)
                    )
                    .append(Component.text(", ", TextColor.color(0xffffff)));
        }
        sender.sendMessage(base);
    }

    public void delete(CommandSender sender, CommandContext context) {
        String name = NameFormatter.formattedWorldName(sender.asPlayer(), context.get("world"));
        if (!WorldManager.exists(name)) {
            sender.sendMessage(Component.text("You do not have a world by that name!", TextColor.color(0xaa2222)));
            return;
        }
        if (InstanceManager.get().getProfile(name) != null) {
            // this instance is loaded, fail
            sender.sendMessage(Component.text("That world is currently loaded! Please return to the lobby to delete the world.", TextColor.color(0xaa2222)));
            return;
        }
        sender.asPlayer().sendMessage(Component.text("Deleting world ", NameFormatter.PRIMARY)
                .append(Component.text(name, NameFormatter.SECONDARY))
                .append(Component.text(", this may take a second!", NameFormatter.PRIMARY))

        );
        WorldManager.delete(name);
        sender.asPlayer().sendMessage(Component.text("World deleted!", TextColor.color(0x34bc8a)));
        OwnershipCache.poke(sender.asPlayer().getUuid().toString());
    }


    public void create(CommandSender sender, CommandContext context) {
        if (WorldManager.exists(NameFormatter.formattedWorldName(sender.asPlayer(), context.get("worldName")))) {
            sender.sendMessage(Component.text("You already have a world by that name!", TextColor.color(0xaa2222)));
            return;
        }
        String name = NameFormatter.formattedWorldName(sender.asPlayer(), context.get("worldName"));
        WorldManager.create(name, sender.asPlayer().getUuid().toString());
        OwnershipCache.poke(sender.asPlayer().getUuid().toString());
        try {
            InstanceContainer target = InstanceManager.get().spawn(name, new CreativeWorldDelegate(name, sender.asPlayer()));
            InstanceManager.get().transfer(sender.asPlayer(), target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            sender.sendMessage(Component.text("Error loading world!", TextColor.color(0xaa2222)));
            return;
        }

    }

    public void load(CommandSender sender, CommandContext context) {
        if (!WorldManager.exists(NameFormatter.formattedWorldName(sender.asPlayer(), context.get("world")))) {
            sender.sendMessage(Component.text("You do not have a world by that name!", TextColor.color(0xaa2222)));
            return;
        }
        String name = NameFormatter.formattedWorldName(sender.asPlayer(), context.get("world"));
        if (InstanceManager.get().getInstance(name) != null) {
            sender.sendMessage(Component.text("Instance already exists!", TextColor.color(0xaa2222)));
            return;
        }
        // player has access to world now lets goooo
        new WorldDownloaderThread(sender.asPlayer(), name).start();

    }


}
class WorldDownloaderThread extends Thread {
    Player p;
    String world;
    public WorldDownloaderThread(Player pl, String w) {
        p = pl;
        world = w;
    };
    @Override
    public void run() {
        p.sendMessage(Component.text("Pulling world ", NameFormatter.PRIMARY)
            .append(Component.text(world, NameFormatter.SECONDARY))
                .append(Component.text(" from the database, this may take a second!", NameFormatter.PRIMARY))

        );
        World z = WorldManager.getWorld(world);
        z.downloadAllChunks();
        try {
            InstanceContainer target = InstanceManager.get().spawn(world, new CreativeWorldDelegate(world, p));
            InstanceManager.get().transfer(p, target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            p.sendMessage(Component.text("Error loading world!", TextColor.color(0xaa2222)));
            return;
        }

    }
}
