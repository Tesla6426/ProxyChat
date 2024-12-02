package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

public class format {
    /*
    * TODO:
    *  basically all that needs to be done here is add in placeholders and let the config file do the rest
    *  per-channel and global formatting need to work
    * */
    public static String format;
    public static String playerMessage(Player player, String message, String fromProxy) {
        // replace placeholders
        String formattedMessage = format;
        return formattedMessage
                .replaceAll("%server%", player.getCurrentServer().get().getServerInfo().getName())
                .replaceAll("%player%", player.getUsername())
                .replaceAll("%proxy%", fromProxy)
                .replaceAll("%prefix%", "[RANK]")
                .replaceAll("%message%", message);
    }
}
