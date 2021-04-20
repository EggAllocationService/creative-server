package io.egg.server.creative;

import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.CommandExecutor;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.Suggestion;
import net.minestom.server.command.builder.suggestion.SuggestionCallback;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldCommand extends Command {
    public WorldCommand() {
        super("world", "w");
        var worldArgument = ArgumentType.String("world");
        worldArgument.setSuggestionCallback((sender, context, suggestion) -> {
            Player p = sender.asPlayer();
            for (String s : OwnershipCache.getWorlds(p.getUsername())) {
                suggestion.addEntry(new SuggestionEntry(s));
            }
        });
        var stringArg = ArgumentType.String("worldName");
        var create = ArgumentType.Literal("create");
        var edit = ArgumentType.Literal("edit");
        addSyntax(this::create, create, stringArg);
        addSyntax(this::edit, edit, worldArgument);

    }

    public void create(CommandSender sender, CommandContext context) {

    }

    public void edit(CommandSender sender, CommandContext context) {

    }


}
