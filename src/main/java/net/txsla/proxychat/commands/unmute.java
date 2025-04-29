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
import net.txsla.proxychat.mute;

import java.util.Collection;

public class unmute {
    public static BrigadierCommand unmuteCommand (final ProxyServer proxy) {
        // still trying to understand this
        LiteralCommandNode<CommandSource> unmuteCommandNode = LiteralArgumentBuilder.<CommandSource>literal("unmute")
                // check if player has permissions (buggy?)

                .requires(commandSource -> {
                    if (!commandSource.hasPermission("proxychat.mute")) {
                        // do nothing if player does not have permission
                        return false;
                    }
                    return true;
                })

                .then(RequiredArgumentBuilder.<CommandSource, String>argument("player", StringArgumentType.word())
                        // command autocomplete
                        .suggests((context, builder) -> {
                            // get all players on proxy
                            Collection<Player> players = proxy.getAllPlayers();

                            // cycle through every player on proxy and check what names to suggest
                            mute.muted_name.forEach(player -> {
                                try {
                                    // get argument
                                    String argument = context.getArgument("player", String.class);
                                    // autocomplete player usernames
                                    if (player.startsWith(argument)) builder.suggest(player);
                                } catch (IllegalArgumentException e) {
                                    // if user has not typed, then return all usernames
                                    builder.suggest(player);
                                }
                            });
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            if ( net.txsla.proxychat.mute.unmutePlayer( context.getArgument("player", String.class) ) ) {
                                context.getSource().sendMessage(Component.text("§a" + context.getArgument("player", String.class) + " unmuted!"));
                                return Command.SINGLE_SUCCESS;
                            }
                            context.getSource().sendMessage(Component.text("§cFailed to unmute " + context.getArgument("player", String.class) + "! Is player already unmuted?"));
                            return 0;
                        }))
                .executes(context -> {
                    // no args
                    context.getSource().sendMessage(Component.text("/unmute <player>"));
                    return 0;
                })
                .build();

        return new BrigadierCommand(unmuteCommandNode);
    }

}

