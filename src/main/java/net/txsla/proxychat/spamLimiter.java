package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;

public class spamLimiter {
    // limit spam
    public static List<Spam> players = new ArrayList<>() {
    };
    public static void addPlayer(Player player) {
        // no dupe protection
        players.add(new Spam(player));
    }
}
