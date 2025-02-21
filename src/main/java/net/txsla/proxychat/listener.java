package net.txsla.proxychat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

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

        send.messageChannel(server, format.playerMessage(sender, message));
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
    }
}
