package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.txsla.proxychat.xProxy.xProxyClient;

import java.util.ArrayList;
import java.util.Base64;

public class msgMngr {
    public static ArrayList<String[]> ranks;
    public static boolean xProxyEnabled;
    public static Component formatMessage(String proxy, String server, String sender, String UUID, String chatMessage) {
        String format = ProxyChat.config.getString("message-format");
        String role = "&aPlayer-Role";
        String msg = format
                .replaceAll("%proxy%", proxy)
                .replaceAll("%server%", server)
                .replaceAll("%player%", sender)
                .replaceAll("%message%", chatMessage)
                .replaceAll("%prefix%", role);

        return LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
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
    public static void xProxyReceive() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (xProxyEnabled) {
                if (xProxyClient.in != null) {
                    String received = xProxyClient.in;
                    xProxyClient.in = null;

                    // no one cares who sent the message :(
                    String sender = received.substring(0, received.indexOf("¦"));
                    String com = received.substring(received.indexOf("¦"), received.lastIndexOf("¦"));
                    String data = new String(Base64.getDecoder().decode( received.substring(received.lastIndexOf("¦"), received.length()) ));
                    switch (com) {
                        case "btd":
                            // check if message was for ProxyChat
                            if (data.substring(0, data.indexOf("-")).equals("proxychat")) {
                                data = data.substring(data.indexOf("-")+1, data.length());
                                String[] msg = data.split("¦");
                                // format and send message
                                sendMessage( Integer.parseInt( msg[0] ), formatMessage(sender, msg[1], msg[2], msg[3], msg[4]) );
                            }
                            break;
                        case "dbr":
                            break;
                        case "dbu":
                            break;
                    }

                }
                }
            }
        }).start();
    }
    public static int getChannel(String server) {
        for (int n = 1; n <= ProxyChat.channel.length ; n++ ) {
            if (ProxyChat.channel[n].equals(server))
                return n; }
        return 0; // 0 is default channel for any unlisted servers
    }
}
