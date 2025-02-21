package net.txsla.proxychat.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.Collection;

public class mute {
    public static BrigadierCommand muteCommand (final ProxyServer proxy) {
        // still trying to understand this
        LiteralCommandNode<CommandSource> muteCommandNode = LiteralArgumentBuilder.<CommandSource>literal("mute")

                // check if player has permissions (buggy?)
                /*
                .requires(commandSource -> {
                    if (!commandSource.hasPermission("permission.node")) {
                        commandSource.sendMessage(Component.text("You do not have permission to run this command!"));
                        return false;
                    }
                    return true;
                })
                */

                .then(RequiredArgumentBuilder.<CommandSource, String>argument("arg", StringArgumentType.word())
                        // command autocomplete
                        .suggests((ctx, builder) -> {
                            // get all players on proxy
                            Collection<Player> players = proxy.getAllPlayers();

                            // cycle through every player on proxy and check what names to suggest
                            players.forEach(player -> {
                                try {
                                    // get argument
                                    String argument = ctx.getArgument("arg", String.class);
                                    // autocomplete player usernames
                                    if (player.getUsername().startsWith(argument)) {
                                        builder.suggest(player.getUsername());
                                    }
                                } catch (IllegalArgumentException e) {
                                    // if user has not typed, then return all usernames
                                    builder.suggest(player.getUsername());
                                }

                            });
                            // return autocomplete
                            return builder.buildFuture();

                        }).executes(context -> {
                            // check if sender is a player
                            if (!(context.getSource() instanceof Player)) {
                                context.getSource().sendMessage(Component.text("You must be a player to execute this command"));
                                return 0;
                            }

                            // get argument
                            String argumentProvided = context.getArgument("arg", String.class);

                            // tell sender it was successful
                            Player sender = (Player) context.getSource();
                            sender.sendMessage(Component.text("Player " + argumentProvided + "muted"));

                            return Command.SINGLE_SUCCESS;

                        }))
                // I have no clue what the fuck this second execute block does
                .executes(context -> {
                    // check if sender is a player
                    if (!(context.getSource() instanceof Player)) {
                        context.getSource().sendMessage(Component.text("You must be a player to execute this command"));
                        return 0;
                    }
                    return Command.SINGLE_SUCCESS;
                })
                // build (yay) :)
                .build();


        return new BrigadierCommand(muteCommandNode);
    }

}
