package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.txsla.proxychat.rank.ranks;

import java.awt.*;
import java.util.regex.Matcher;

public class format {
    public static String
            message_format,
            dc2mc_format,
            mc2dc_format,
            join_format,
            leave_format;

    public static String playerJoinMessage(Player p) {
        String formatted = join_format;
        return formatted.replaceAll("%player%",p.getUsername());
    }
    public static String playerLeaveMessage(Player p) {
        String formatted = leave_format;
        return formatted.replaceAll("%player%",p.getUsername());
    }
    public static String minecraft2DiscordMessage(Player player, String message) {
        // get message format
        String formattedMessage = mc2dc_format;

        // replace placeholders
        return mc2dc_format.replaceAll("%server%", player.getCurrentServer().get().getServerInfo().getName())
                .replaceAll("%proxy%", ProxyChat.proxyName)
                .replaceAll("%prefix%", ranks.getPrefix(player))

                // MiniMessage Placeholders do not work on discord, so we will remove them before sending
                // replace legacy placeholders
                .replaceAll("[&§][0-9a-fk-or]", "")
                // replace MiniMessage tags
                .replaceAll("</?[a-z0-9:#_]+>", "")

                // these should never contain formatting placeholders (player-typed are exempt)
                .replaceAll("%player%", player.getUsername())

                // replace all '_' with '\_' to account for underscores effecting format in names
                .replaceAll("_", Matcher.quoteReplacement("\\_"))
                .replaceAll("%message%", Matcher.quoteReplacement(message));
    }
    public static String discord2MinecraftMessage(MessageReceivedEvent event) {
        String formattedMessage = dc2mc_format;
        Color color = event.getMember().getRoles().get(0).getColor();
        return formattedMessage.replaceAll("%rank%", "" + event.getMember().getRoles().get(0).getName())
                .replaceAll("%player%", event.getAuthor().getName())
                .replaceAll("%rank_color%", "" + toHex(color.getRed(), color.getBlue(), color.getGreen()))
                .replaceAll("%message%", event.getMessage().getContentRaw())
                ;
    }
    public static String playerMessage(Player player, String message) {
        // get message format
        String formattedMessage = message_format;

        // replace placeholders
        return formattedMessage
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
        if (ProxyChat.config.getBoolean("legacy-formatting")) return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return MiniMessage.miniMessage().deserialize(message);
    }
    public static String toHex(int red, int blue, int green) {
        return String.format("#%02x%02x%02x", red, green, blue);
    }
 }
