package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class send {
    public static boolean reportFailedMessages;
    public static List<List<String>> channel = new ArrayList<>();
    /*
    To add in the future:
        - move 'LegacyComponentSerializer.legacyAmpersand().deserialize(' to its own method in proxychat.format
            and allow users to choose the specific formatting that they want
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
    public static void messageChannel(int toChannel, String message) {
        // does not discriminate in channel 0
        List<String> servers = channel.get(toChannel);
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
        if ( channel.get(0).contains(name) ) {
            messageServer(server, message); return;
        }

        messageChannel( getChannel(name), message);
    }
    public static void messageChannel(String serverName, String message) {
        // finds what channel a server is in and messages the channel
        // do NOT use this method in a case where channel 0 is needed - channel 0 messages are ignored

        // this method is used by xProxy, no need for channel 0
        // if server is in channel 0, then ignore message
        if ( channel.get(0).contains(serverName) ) {
            return;
        }

        messageChannel(getChannel(serverName), message);
    }
    public static void messageXProxy(String server, String sender, String uuid, String chatMessage  ) {
        // sends message to other Proxies via XProxy
        String encodedMessage = ProxyChat.proxyName + "¦" + server + "¦" + sender + "¦" + uuid + "¦" +
                chatMessage; // leave message raw, it is to be processed and formatted at the other proxy
        // convert to b64 before sending
        encodedMessage = new String(Base64.getEncoder().encode(encodedMessage.getBytes()));
        // send to other proxies
        xProxyClient.out = "bdc¦proxychat-" + encodedMessage;
    }
    public static int getChannel(String serverName) {
        for (int i = 0; i < channel.size(); i++) {
            if (channel.get(i).contains(serverName)) {
                return i;
            }
        }
        if (reportFailedMessages) System.out.println("Server " + serverName + " is not in a channel - messages will not be sent");
        return -1;
    }
}
