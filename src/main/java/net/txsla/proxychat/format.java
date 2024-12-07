package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

import java.util.regex.Matcher;

public class format {
    public static String format;
    public static String playerMessage(Player player, String message) {
        String formattedMessage = format;

        // replace placeholders
        return formattedMessage
                .replaceAll("%server%", player.getCurrentServer().get().getServerInfo().getName())
                .replaceAll("%player%", player.getUsername())
                .replaceAll("%proxy%", ProxyChat.proxyName)
                .replaceAll("%prefix%", ranks.getPrefix(player))
                .replaceAll("%message%", Matcher.quoteReplacement(message));
    }
    public static String xProxyMessage(String proxy, String server, String sender, String UUID, String chatMessage) {
        String formattedMessage = format;

        // replace placeholders
        return formattedMessage
                .replaceAll("%server%", server)
                .replaceAll("%player%", sender)
                .replaceAll("%proxy%", proxy)
                .replaceAll("%prefix%", ranks.getRank(sender).getPrefix() )
                .replaceAll("%message%", chatMessage);
        // idk why the UUID is requested, but it is in the old protocol I wrote, so here it stays
    }
}
