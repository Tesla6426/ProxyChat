package net.txsla.proxychat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

public class listener {
    public static boolean logMessages;
    @Subscribe
    public void onPlayerMessage(PlayerChatEvent event) {
        // get sender info
        Player sender = event.getPlayer();
        String message = event.getMessage();
        RegisteredServer server = sender.getCurrentServer().get().getServer();

        // repeat messages to console (toggled in configs)
        System.out.println("[" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> " + message );

        //cancel event as to not double-send the message to players
        event.setResult(PlayerChatEvent.ChatResult.denied());


        // send message to server channel
        send.messageChannel(server, format.playerMessage(sender, message));

        if (logMessages) {
            //log the message

        }
    }
}
