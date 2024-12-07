package net.txsla.proxychat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.IOException;

public class listener {
    @Subscribe
    public void onPlayerMessage(PlayerChatEvent event) throws IOException {
        // get sender info
        Player sender = event.getPlayer();
        String message = event.getMessage();
        RegisteredServer server = sender.getCurrentServer().get().getServer();

        // repeat messages to console (toggled in configs)
        System.out.println("[" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> " + message );

        //cancel event as to not double-send the message to players
        event.setResult(PlayerChatEvent.ChatResult.denied());

        if (spamLimiter.enabled) {
            // increment chat counter
            spamLimiter.inc(sender, 1);

            // check if player's messages should be ignored
            if (!spamLimiter.canChat(sender)) {
                // shows the spammer their own messages, so they do not know they are being ignored
                // also skips logging the message
                send.messagePlayer(sender, format.playerMessage(sender, message) );
                return;
            }
        }

        // send message to server channel
        send.messageChannel(server, format.playerMessage(sender, message));

        if (log.enabled) {
            //log the message
            log.add( "[" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> : " + message);
        }
    }
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        // add player to spam list, no need for bypass if configured correctly
        if (spamLimiter.enabled) spamLimiter.addPlayer(event.getPlayer());
    }
}
