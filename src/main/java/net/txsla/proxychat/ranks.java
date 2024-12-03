package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;

public class ranks {
    public static int rankSystem; // 0 = Disabled, 1 = ProxyChat, 2 = xProxy, 3 = hopefully coming soon (either vault or placeholderAPI)
    public static String getPrefix(Player p) {
        // return empty string if ranks are disabled
        if (rankSystem == 0 ) return "";
        return "[RANK]";
    }



}
