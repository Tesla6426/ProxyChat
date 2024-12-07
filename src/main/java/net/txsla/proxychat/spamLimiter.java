package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class spamLimiter {
    static List<Player> index = new ArrayList<>();
    static List<Integer> counter = new ArrayList<>();
    static int maxCounter;
    static int decTimer;
    static boolean enabled;

    public static void addPlayer(Player player) {
        // do NOT add if player is already added to counter/index
        // currently this uses the player object to reset the counter after each rejoin -
        // switch to player name or UUID to keep it persistent (add a count reset command when command system is implemented)
        if (index.contains(player)) return;
        // set info
        index.add(player);
        counter.add(0);
    }
    public static void inc(Player player, int i) {
        counter.set(index.indexOf(player), counter.get(index.indexOf(player)) + i);
    }
    public static boolean canChat(Player player) {
        return counter.get(index.indexOf(player)) <= maxCounter;
    }
    public static void startDecTimer() {
        Thread dec = new Thread(()->
                {
                    int i =0;
                    // debug, remove later
                    System.out.println("dec timer started");

                    while (true) {
                        i = 0;
                        // wait
                        try { Thread.sleep(decTimer); } catch (InterruptedException e) {
                            throw new RuntimeException(e);}

                        // decrement one from everyone's chat timer
                        for (int count : counter) {
                            // decrement if counter is above 0
                            if (counter.get(i) > 0) counter.set(i, count-1);
                            i++;
                        }
                    }
                }
                );
        dec.start();
    }
}
