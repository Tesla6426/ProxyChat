package net.txsla.proxychat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.txsla.proxychat.discord.bot;

import java.util.regex.Matcher;

public class listener {
    @Subscribe
    public void onPlayerMessage(PlayerChatEvent event) {
        // get sender info
        Player sender = event.getPlayer();
        String message = event.getMessage();
        RegisteredServer server = sender.getCurrentServer().get().getServer();

        // repeat messages to console (make toggle in configs)
        System.out.println("[" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> " + message );

        if (mute.isMuted(sender.getUniqueId()) || mute.isMuted(sender.getUsername())) return;


        if (spamLimiter.enabled) {
            spamLimiter.inc(sender, 1);
            if (!spamLimiter.canChat(sender)) {
                // shows the spammer their own messages, so they do not know they are being ignored
                send.messagePlayer(sender, format.playerMessage(sender, message) );
                return;
            }
        }

        // message channel
        send.messageChannel(sender, server,  message);

        // log message to file
        if (log.enabled) log.add( "[" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> : " + message);

        // send to xProxy
        send.messageXProxy(
                sender.getCurrentServer().get().getServerInfo().getName(),
                sender.getUsername(),
                sender.getUniqueId().toString(),
                message);
    }
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        // add player to spam list, no need for bypass if configured correctly
        if (spamLimiter.enabled) spamLimiter.addPlayer(event.getPlayer());

        // join messages
        send.messageChannel("join-leave", format.playerJoinMessage(event.getPlayer()));
        bot.send("_" +
                format.playerJoinMessage(event.getPlayer())
                        // remove minimessage and legacy tags as they do not work on discord
                        .replaceAll("[&§][0-9a-fk-or]", "")
                        .replaceAll("</?[a-z0-9:#_]+>", "")
                        .replaceAll("_", Matcher.quoteReplacement("\\_")) + "_"
        );
    }
    @Subscribe
    public void onDisconnectEvent(DisconnectEvent event) {
        // leave messages
        send.messageChannel("join-leave", format.playerLeaveMessage(event.getPlayer()));
        bot.send("_" +
                format.playerLeaveMessage(event.getPlayer())
                        // remove minimessage and legacy tags as they do not work on discord
                        .replaceAll("[&§][0-9a-fk-or]", "")
                        .replaceAll("</?[a-z0-9:#_]+>", "")
                        .replaceAll("_", Matcher.quoteReplacement("\\_")) + "_"
        );

    }
}
