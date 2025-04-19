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

import java.util.ArrayList;
import java.util.Collection;

public class mute {
    public static BrigadierCommand muteCommandSwap (final ProxyServer proxy) {
        //no?
        return null;
    }
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
                // 1st arg processor
                .then(RequiredArgumentBuilder.<CommandSource, String>argument("mute_player", StringArgumentType.word())
                        // command autocomplete
                        .suggests((ctx, builder) -> {
                            // get all players on proxy
                            Collection<Player> players = proxy.getAllPlayers();

                            // cycle through every player on proxy and check what names to suggest
                            players.forEach(player -> {
                                try {
                                    // get argument
                                    String argument = ctx.getArgument("mute_player", String.class);
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

                        })
                        .executes(context -> {
                            context.getSource().sendMessage(Component.text("/mute <player> <duration> <reason>"));
                            return 0;
                        })
                        // duration layer
                        .then(RequiredArgumentBuilder.<CommandSource, String>argument("duration", StringArgumentType.word())
                            .suggests(((context, builder) -> {
                                // list of durations available
                                ArrayList<String> durations = new ArrayList<>();
                                durations.add("m"); // minutes
                                durations.add("h"); // hours
                                durations.add("d"); // days
                                durations.add("w"); // weeks
                                durations.add("y"); // years

                                durations.forEach(duration -> {
                                        // append suggested duration if player is typing it
                                    try {
                                        String arg = context.getArgument("duration", String.class).toLowerCase();
                                        if (arg.matches("[0-9]+")) {
                                            builder.suggest(arg + duration);
                                        }
                                    } catch (IllegalArgumentException e) {
                                        builder.suggest("<int>" + duration);
                                    }
                                });
                                        return builder.buildFuture();
                        }))
                                .executes(context -> {
                                    if (net.txsla.proxychat.mute.requireReason) {
                                        context.getSource().sendMessage(Component.text("Incorrect usage: A reason is needed to mute a player"));
                                        context.getSource().sendMessage(Component.text("/mute <player> <duration> <reason>"));
                                        return 0;
                                    }
                                    context.getSource().sendMessage(Component.text("Player Muted!"));
                                    return Command.SINGLE_SUCCESS;
                                })
                        // reason layer (final layer)
                            .then(RequiredArgumentBuilder.<CommandSource, String>argument("reason", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String sender = "[@]";
                                        String muted_player = context.getArgument("mute_player", String.class);
                                        String duration = context.getArgument("duration", String.class);
                                        String reason = context.getArgument("reason", String.class);
                                        if (context.getSource() instanceof Player) {
                                            sender = ((Player) context.getSource()).getUsername();
                                        }

                                        // mute player
                                        if ( net.txsla.proxychat.mute.mutePlayer(muted_player, sender, reason, duration) ) {
                                            // if successful, tell player
                                            context.getSource().sendMessage(Component.text("Player " + muted_player + " muted for " + duration));
                                            return Command.SINGLE_SUCCESS;
                                        }
                                        // tell sender it was successful
                                        context.getSource().sendMessage(Component.text("Failed to mute player: check command syntax"));
                                        return 0;

                        }))))
                // executes when NO args
                .executes(context -> {
                        context.getSource().sendMessage(Component.text("/mute <player> <duration> <reason>"));
                        return 0;
                })
                // build (yay) :)
                .build();


        return new BrigadierCommand(muteCommandNode);
    }

}
