package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;

public class message {
    /*
    When you are reading this code you might ask yourself
     "why are there so many methods to do basically the same thing (message<destination>)"
    The answer is so you and I can copy/paste whichever one works for your specific use case right into another project
    */
    public static void messagePlayer(Player player, String message) {
        // send a message to a specific player connected to the proxy
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
    public static void messageServer(RegisteredServer server, String message) {
        // send a message to all players in a server
        for ( Player player : server.getPlayersConnected() ) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
        }
    }
    public static void messageProxy(String message) {
        // send message to all players in registered servers connected to the current proxy
        for (RegisteredServer server : ProxyChat.proxy.getAllServers() ) {
            for ( Player player : server.getPlayersConnected() ) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
            }
        }
    }
    public static void messageXProxy(String message) {
        // this one is not meant to be copy/paste-able to other projects
        // broadcasts message to all registered servers and connected xProxy proxies
        messageProxy(message);
        // add xProxy send command here
    }

}
