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
        if (index.contains(player)) return;
        // set info
        index.add(player);
        counter.add(0);
    }
    public static void inc(Player player, int i) {
        // set to a negative number to subtract
        // simplify down to 1 line later (no need to store an int)
        int x = index.indexOf(player);
        counter.set(x, counter.get(x) + i);

        // for debug - remove later
        System.out.println("incrementing " + index.get(x) + "'s counter by " + i + " : " + counter.get(x));
    }
    public static boolean canChat(Player player) {
        // simplify down to 1 line later (no need to store an int)
        int x = index.indexOf(player);
        return counter.get(x) <= maxCounter;
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
                            // debug, remove later
                            System.out.println("decramenting " + index.get(i).getUsername() + "'s counter : " + counter.get(i));
                            i++;
                        }
                    }
                }
                );
        dec.start();
    }
}
