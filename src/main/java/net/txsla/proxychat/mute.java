package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class mute {
    public static YamlDocument muteConfig;
    public static List<String> muted_name = new ArrayList<>();
    public static List<UUID> muted_uuid = new ArrayList<>();

    public static boolean isMuted(UUID uuid) {
        // check if a player's UUID is in the mute list;
        for (UUID u : muted_uuid) {
            if (u.equals(uuid)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isMuted(String username) {
        // check if a username is in the mute list;
        for (String s : muted_name) {
            if (s.equals(username)) {
                return true;
            }
        }
        return false;
    }
    public static void mutePlayer(Player muted_player, String mute_enforcer, String reason, LocalDateTime muted_until ) {
        // add player to mute list file and reload config
        String username = muted_player.getUsername();
        UUID uuid = muted_player.getUniqueId();
        muteConfig.set("mute-list", username);

        // populate punishment-info in config
        muteConfig.createSection("punishment-info." +username);
        muteConfig.set("punishment-info." +username+".uuid", uuid);
        muteConfig.set("punishment-info." +username+".datetime-muted", LocalDateTime.now());
        muteConfig.set("punishment-info." +username+".muted-until", muted_until);
        muteConfig.set("punishment-info." +username+".muted-by", mute_enforcer);
        muteConfig.set("punishment-info." +username+".reason", reason);

        // log action
        muteLog( username + " (" + uuid + ") was muted by " + mute_enforcer + "for " + reason);
    }
    public static boolean unmutePlayer() {

        // false = unsuccessful (player was never muted)
        // true = successfully unmuted player
        return false;
    }
    public static String muteList() {
        // return a String list of muted players
        StringBuilder mute_list = new StringBuilder("[");
        // add all muted players to a list.
        for (String s : muted_name) {
            mute_list.append(s).append(", ");
        }
        // remove trailing ', '
        mute_list = new StringBuilder(mute_list.substring(0, mute_list.length() - 2) + "]");
        return mute_list.toString();
    }
    public static String muteInfo(String username) {
        // return info on a player's mute status
        boolean is_muted = isMuted(username);
        String mute_info =
                "player: " + username + "\n" +
                "is_muted: " + is_muted + "\n";
        if (is_muted) {
            // return further info if player is, in fact, muted
            mute_info +=
                    "date-issued: " + muteConfig.get("punishment-info."+username+".datetime-muted") + "\n" +
                    "muted_until: " + muteConfig.get("punishment-info."+username+".muted-until") + "\n" +
                    "muted_by: " + muteConfig.get("punishment-info."+username+".muted-by") + "\n" +
                    "reason: " + muteConfig.get("punishment-info."+username+".reason") + "\n";
        }
        return mute_info;
    }

    public static void loadMuteList() {
        // reload the mute list from config file
        muted_name = muteConfig.getStringList("mute-list");

        for (String s : muted_name) {
            muted_uuid.add((UUID) muteConfig.get("punishment-info." + s + ".uuid"));
        }
    }
    public static void muteLog(String log) {
        // log all mute actions to log file


    }
}
