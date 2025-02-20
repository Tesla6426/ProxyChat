package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class mute {
    public static String[] muted_name;
    public static UUID[] muted_uuid;

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
    public static void mutePlayer(Player muted_player, String mute_enforcer, String reason, LocalDateTime mute_until ) {
        // add player to mute list file and reload config
    }
    public static boolean unmutePlayer() {

        // false = unsuccessful (player was never muted)
        // true = successfully unmuted player
        return false;
    }
    public static String muteList() {
        // return a list of muted players
        String mute_list = "[";
        // add all muted players to a list.
        for (int i = 0; i < muted_name.length; i++) {
            mute_list += muted_name[i] + ", ";
        }
        // remove trailing ', '
        mute_list = mute_list.substring(0, mute_list.length()-2) + "]";
        return mute_list;
    }
    public static String muteInfo(String username) {
        // return info on a player's mute status
        return "";
    }

    public static void loadMuteList() {
        // reload the mute list from config file
    }
}
