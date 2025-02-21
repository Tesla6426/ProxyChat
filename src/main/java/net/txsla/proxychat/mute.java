package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class mute {
    public static boolean requireReason;
    public static YamlDocument muteConfig;
    public static List<String> muted_name = new ArrayList<>();
    public static List<UUID> muted_uuid = new ArrayList<>();

    public static boolean isMuted(UUID uuid) {
        for (UUID u : muted_uuid) if (u != null) if (u.equals(uuid)) return true;
        return false;
    }
    public static boolean isMuted(String username) {
        for (String s : muted_name) if (s.equals(username)) return true;
        return false;
    }
    public static boolean mutePlayer(String username, String mute_enforcer, String reason, String duration ) {
        // add player to mute list file and reload config
        Date date = new Date();
        UUID uuid = null;
        try {
            // requires player to be online to find UUID - maybe add null search later whenever a player joins and matched mute list
            uuid = ProxyChat.proxy.getPlayer(username).get().getUniqueId();
        } catch (Exception e) {
            System.out.println("Player " + username + " not found. Please populate UUID field manually. (Mute is still in effect unless player changes their name)");
        }

        // figure out muted_until unix timestamp from duration
        long muted_until = date.getTime();
        if (duration.matches("[0-9]+[mhdwy]")) {
            String let = duration.substring(duration.length()-1);
            int num = Integer.parseInt(duration.substring(0, duration.length()-1));

            // convert duration to a unix time code
            if (!(num > 0)) return false;
            switch (let) {
                case "m": muted_until += num*60000L; break;
                case "h": muted_until += num*3600000L; break;
                case "d": muted_until += num*86400000L; break;
                case "w": muted_until += num*604800000L; break;
                case "y": muted_until += num*31557600000L; break;
                // invalid input
                default: return false;
            }
        }else { return false; /*bail if duration does not match regex*/ }

        // add username to mute list
        List<String> usernames = muteConfig.getStringList("mute-list");
        usernames.add(username);
        muteConfig.set("mute-list", usernames);

        // populate punishment-info in config
        muteConfig.createSection("punishment-info." +username);
        muteConfig.set("punishment-info." +username+".uuid", uuid);
        muteConfig.set("punishment-info." +username+".time-muted", date.getTime() );
        muteConfig.set("punishment-info." +username+".muted-until", muted_until);
        muteConfig.set("punishment-info." +username+".muted-by", mute_enforcer);
        muteConfig.set("punishment-info." +username+".reason", reason);

        // log action
        muteLog( username + " (" + uuid + ") was muted by " + mute_enforcer + "for " + reason);

        // save
        try {
            muteConfig.save();
        } catch (Exception e) {
            System.out.println(e + "\n[ProxyChat] Error saving mute config!");
            return false;
        }

        // reload mute list in RAM
        loadMuteList();

        return true;
    }
    public static boolean unmutePlayer() {
        // false = unsuccessful (player was never muted?)
        // true = successfully unmuted player
        return false;
    }
    public static String getMuteNameList() {
        String mute_list = "[";
        for (String s : muted_name) mute_list += s + ", ";
        return mute_list.replaceAll(", $", "") + "]";
    }
    public static String getMuteUUIDList() {
        String mute_list = "[";
        for (UUID s : muted_uuid) mute_list += s + ", ";
        return mute_list.replaceAll(", $", "") + "]";
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
                    "date-issued: " + muteConfig.get("punishment-info."+username+".time-muted") + "\n" +
                    "muted_until: " + muteConfig.get("punishment-info."+username+".muted-until") + "\n" +
                    "muted_by: " + muteConfig.get("punishment-info."+username+".muted-by") + "\n" +
                    "reason: " + muteConfig.get("punishment-info."+username+".reason") + "\n";
        }
        return mute_info;
    }

    public static void loadMuteList() {
        // (re)load the mute list from config file
        muted_name = muteConfig.getStringList("mute-list");
        for (String s : muted_name) muted_uuid.add((UUID) muteConfig.get("punishment-info." + s + ".uuid"));
    }
    public static void muteLog(String log) {
        // log all mute actions to log file
    }
}
