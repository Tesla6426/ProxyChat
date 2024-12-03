package net.txsla.proxychat;

import java.util.List;

public class Rank {
    // class for storing rank data (hopefully this does not get too messy)
    private String name;
    private String prefix;
    private List<String> players;
    public Rank (String name) {
        this.name = name;
    }
    public Rank (String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }
    public Rank (String name, String prefix, List<String> players) {
        this.name = name;
        this.prefix = prefix;
        this.players = players;
    }
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void addPlayer(String username) {
        if (!this.players.contains(username)) this.players.add(username);
    }
    public void addPlayers(List<String> usernames) {
        for (String username : usernames) {
            if (!this.players.contains(username)) this.players.add(username);
        }
    }
    public String getPrefix() {
        return this.prefix;
    }
    public String getName() {
        return this.name;
    }
    public List<String> getPlayers() {
        return this.players;
    }
}
