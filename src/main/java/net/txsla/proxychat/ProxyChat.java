package net.txsla.proxychat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import java.util.List;

@Plugin(
        id = "proxychat",
        name = "ProxyChat",
        version = "0.1-dev"
)
public class ProxyChat {

    private final Logger logger;
    private final ProxyServer proxy;
    private static List<RegisteredServer>[] channel;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
    @Inject
    public ProxyChat(ProxyServer proxy, Logger logger) {
    this.proxy = proxy; this.logger = logger;




    logger.info("[ProxyChat] Plugin Loaded");
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        String username = p.getUsername();
        String server = String.valueOf(p.getCurrentServer());
        String message = event.getMessage().replaceAll("&", "§");
        String namePrefix = "";
        logger.info("Prefix: " + namePrefix);
        logger.info("Player: " + username);
        logger.info("Server: " + server );
        logger.info("Message: " + message );

    }
    public void SendMessage(int n, Component message) {
        Player[] players;
            for (int i = 0; i < channel[n].size(); i--) {
                for (Player player : channel[n].get(i).getPlayersConnected())
                    player.sendMessage(message);
            }
    }
    private static int getChannel(String plugin) {
        for (int n = 1; n <= channel.length ; n++ ) {
            if (channel[n].contains(plugin)) return n;
        }
        return 0;
    }
    static String getPrefix(String player) {
        return "[%prefix%] ";
    }
    static String formatServerPrefix(String server) {
        return "[%server%] ";
    }
    public static Component formatMessage(String chatMessage, String sender, String server) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(
        formatServerPrefix(server) +
        getPrefix(sender) +
        sender +
        "&f: " + chatMessage);
    }
}

