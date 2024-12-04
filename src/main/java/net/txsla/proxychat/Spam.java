package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

public class Spam {
    // my lazy ass is just making objects instead of writing actual good code - ig this is peak java programming
    // I might switch from storing the whole player object to just using a string for the name, but NBT Archives has 120+GB or ram, so it shouldn't be a problem
    // maybe if the active player amounts hits 200+ ill change it (I doubt that will happen, when is there ever more than 10 people in a library)
    private Player player;
    private int counter;
    public Spam(Player player) {
        this.player = player;
    }
    public Spam(Player player, int counter) {
        this.player = player;
        this.counter = counter;
    }
    public void add(int add) {
        // just add a negative number to decrement
        this.counter = this.counter + add;
    }
    public int getCounter() {
        // OoP looks so dumb
        return this.counter;
    }
}
