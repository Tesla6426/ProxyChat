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

public class mute_list {
    public static BrigadierCommand muteListCommand (final ProxyServer proxy) {
        // still trying to understand this
        LiteralCommandNode<CommandSource> muteListCommandNode = LiteralArgumentBuilder.<CommandSource>literal("mute-list")

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

                 .executes(context -> {
                     context.getSource().sendMessage(Component.text(
                             "§dMute Lists: \n" +
                             "name: " + net.txsla.proxychat.mute.getMuteNameList() + "\n" +
                             "uuid: " + net.txsla.proxychat.mute.getMuteUUIDList() ));
                       return Command.SINGLE_SUCCESS;
                 })
                .build();

        return new BrigadierCommand(muteListCommandNode);
    }

}
