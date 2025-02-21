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

public class mute_status {
    public static BrigadierCommand muteStatusCommand (final ProxyServer proxy) {
        // still trying to understand this
        LiteralCommandNode<CommandSource> muteStatusCommandNode = LiteralArgumentBuilder.<CommandSource>literal("mute-status")
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
                            context.getSource().sendMessage(Component.text(
                                    "§dMute Status:\n" + net.txsla.proxychat.mute.muteInfo( context.getArgument("player", String.class) )
                                    ));
                         return Command.SINGLE_SUCCESS;
                        }))
                .executes(context -> {
                    // MO args
                    context.getSource().sendMessage(Component.text("/mute-status <player>"));
                    return 0;
                })
                .build();

        return new BrigadierCommand(muteStatusCommandNode);
    }

}

