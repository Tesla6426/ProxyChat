package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;

public class format {
    public static String
            message_format,
            dc2mc_format,
            mc2dc_format,
            join_format,
            leave_format;

    public static String playerJoinEvent(Player p) {
     return "";
    }
    public static String playerLeaveMessage() {
        return "";
    }
    public static String minecraft2DiscordMessage() {

        return "";
    }
    public static String discord2MinecraftMessage() {
        String formattedMessage =dc2mc_format;

        return "";
    }
    public static String playerMessage(Player player, String message) {
        String formattedMessage = message_format;

        // replace placeholders
        return message
                .replaceAll("%server%", player.getCurrentServer().get().getServerInfo().getName())
                .replaceAll("%player%", player.getUsername())
                .replaceAll("%proxy%", ProxyChat.proxyName)
                .replaceAll("%prefix%", ranks.getPrefix(player))
                .replaceAll("%message%", Matcher.quoteReplacement(message));
    }
    public static String xProxyMessage(String proxy, String server, String sender, String UUID, String chatMessage) {
        String formattedMessage = message_format;

        // replace placeholders
        return formattedMessage
                .replaceAll("%server%", server)
                .replaceAll("%player%", sender)
                .replaceAll("%proxy%", proxy)
                .replaceAll("%prefix%", ranks.getRank(sender).getPrefix() )
                .replaceAll("%message%", chatMessage);
        // idk why the UUID is requested, but it is in the old protocol I wrote, so here it stays
    }
    public static Component message(String message) {
        if (!ProxyChat.config.getBoolean("legacy-formatting")) return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return MiniMessage.miniMessage().deserialize(message);
    }
 }
