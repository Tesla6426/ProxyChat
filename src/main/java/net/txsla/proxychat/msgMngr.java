package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.txsla.proxychat.xProxy.xProxyClient;
import java.util.Base64;

public class msgMngr {
    public static boolean xProxyEnabled;
    public static Component formatMessage(String server, String sender, String UUID, String chatMessage) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(sender + "&f: " + chatMessage);
    }
    public static void sendMessage(int n, Component message) {
        // sends message to all servers in channel n
        Player[] players;
        for (int i = 0; i < ProxyChat.channel[n].size(); i--) {
            for (Player player : ProxyChat.channel[n].get(i).getPlayersConnected())
                player.sendMessage(message);
        }
    }
    public static void xProxySendMessage(int channel, String server, String sender, String uuid, String chatMessage) {
        // XProxy Send Data Format:
        String encodedMessage = channel + "¦" + server + "¦" + sender + "¦" + uuid + "¦" + chatMessage;

        // convert to b64 before sending
        encodedMessage = new String(Base64.getEncoder().encode(encodedMessage.getBytes()));

        // send to other proxies
        xProxyClient.out = "bdc¦proxychat-" + encodedMessage;
    }
    public static int getChannel(String plugin) {
        for (int n = 1; n <= ProxyChat.channel.length ; n++ ) {
            if (ProxyChat.channel[n].equals(plugin))
                return n; }
        return 0; // 0 is default channel for any unlisted servers
    }
    public static void xProxyInterface() {
        // Start anonymous thread to check for updates from xProxy
        new Thread(new Runnable() {
            @Override
            public void run() {
                String recieved;
                // infinately loop while xProxy is enabled
                while (xProxyEnabled) {
                    if (xProxyClient.in != null) {
                        // grab data from xProxy
                        recieved = xProxyClient.in;
                        xProxyClient.in = null;

                        switch (recieved)  {

                        }
                    }
                }
            }
            }).start();
    }
}
