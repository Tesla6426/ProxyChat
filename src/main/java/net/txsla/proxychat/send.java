package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.List;

public class send {
    public static boolean reportFailedMessages;
    public static List<String>[] channel;
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
    public static void messageXProxy(String channel, String message) {
        // this one is not meant to be copy/paste-able to other projects
        // sends message to other Proxies via XProxy

        // add xProxy send command here
    }
    public static void messageChannel(int toChannel, String message) {
        // does not discriminate in channel 0, this code is for broadcasts only
        List<String> servers = channel[toChannel];
        for (String serverName : servers)  {
            if (ProxyChat.proxy.getServer(serverName).isPresent()) {
                messageServer(ProxyChat.proxy.getServer(serverName).get(), message);
            }
        }
    }
    public static void messageChannel(RegisteredServer server, String message) {
        // finds what channel a server is in and messages the channel
        String name = server.getServerInfo().getName();
        // if server is in channel 0, then send the message back to the server
        if ( channel[0].contains(name) ) {
            messageServer(server, message);
            return;
        }

        // finds the channel that the server is on
        for (int i = 0; i < channel.length; i++) {
            if (channel[i].contains(name)) {
                // message channel and return
                messageChannel(i , message);
                return;
            }
        }

        // the code should never reach here unless the server is either not listed or is improperly listed in the channel config
        if (!reportFailedMessages)  ProxyChat.logger.info("MESSAGE '" + message + "' from server '" + name + "' not sent! Did you forget to add the server to a channel?");

    }
// scitzo ass comments - no one is ever going to actually read this except me lol
}
