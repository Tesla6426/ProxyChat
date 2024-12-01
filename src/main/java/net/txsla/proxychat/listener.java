package net.txsla.proxychat;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreTransferEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class listener {
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

        // [TEMPORARY CODE - REMOVE LATER]
        send.messageProxy("[ProxyChat] [" + server.getServerInfo().getName() + "] <" + sender.getUsername() + "> " + message );
    }






    // it does not seem like there are a lot of options to control player leave/join events from velocity - I will prob just use a server-side plugin for that
    @Subscribe
    public void onPlayerConnect(PlayerChooseInitialServerEvent event) {
        // (in the case of NBTARCHIVES) this event might also be fired as a lobby server, even if player is banned and sent to the wasteland

        Player player = event.getPlayer();
        RegisteredServer server = event.getInitialServer().get();

        System.out.println("Player " + player.getUsername() + " has joined the proxy on server " + server.getServerInfo().getName());
    }
    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        RegisteredServer server = player.getCurrentServer().get().getServer();

        System.out.println("Player " + player.getUsername() + " has left the proxy from server " + server.getServerInfo().getName());
    }
    @Subscribe
    public void onPlayerTransfer(PreTransferEvent event) {
        Player player = event.player();
        RegisteredServer server = player.getCurrentServer().get().getServer();
        boolean allowed = event.getResult().isAllowed();
        System.out.println("Player " + player.getUsername() + " is switching servers from " + server.getServerInfo().getName() + "; allowed = " + allowed);
    }

}
