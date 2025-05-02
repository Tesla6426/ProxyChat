package net.txsla.proxychat.rank;

import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.txsla.proxychat.X_Proxy.xProxyClient;

import java.util.ArrayList;
import java.util.List;

public class ranks {
    public static YamlDocument ranksConfig;
    public static Rank defaultRank;
    public static boolean loading;
    public static List<Rank> ranks;
    public static int rankSystem; // 0 = Disabled, 1 = ProxyChat, 2 = xProxy, 3 = hopefully coming soon (either vault or placeholderAPI)
    public static String getPrefix(Player p) {
        // return empty string if ranks are disabled
        if (rankSystem == 0 ) return "";
        if (rankSystem == 1) return getProxyChatRanksPrefix(p);
        if (rankSystem == 2) return getProxyChatRanksPrefix(p);
        return "[RANK]";
    }
    public static void loadRanks() {
        switch (rankSystem) {
            case 1:
                System.out.println("[ProxyChat] Loading Ranks...");
                loadProxyChatRanks();
                break;
            case 2:
                System.out.println("[ProxyChat] Loading xProxy Ranks...");
                loading = true;
                // request a packet from xProxy
                xProxyClient.out = "req¦xproxyrank-send";
                break;
            default:
                System.out.println("[ProxyChat] Ranks are Disabled");
        }
    }
    public static Rank getRank(Player player) {
        for (Rank rank : ranks) {
            if ( rank.getPlayers().contains(player.getUsername())) return rank;
        }
        // return default rank if no rank was found
        return defaultRank;
    }
    public static Rank getRank(String username) {
        for (Rank rank : ranks) if ( rank.getPlayers().contains(username)) return rank;
        // return default rank if no rank was found
        return defaultRank;
    }
    public static void loadProxyChatRanks() {

        System.out.println("[ProxyChat] ProxyChat Ranks Loaded.");
        // reset ranks to not mem leak
        ranks = new ArrayList<>();

        // load default rank
        defaultRank = new Rank("default", ranksConfig.getString("ProxyChatRanks.ranks-config.default.prefix"),
        null,ranksConfig.getStringList("ProxyChatRanks.ranks-config.default.permissions"));

        // load ranks into array
        for (String rank : ranksConfig.getStringList("ProxyChatRanks.ranks-list")) {
            ranks.add( new Rank(
                    rank,
                    ranksConfig.getString("ProxyChatRanks.ranks-config." + rank + ".prefix"),
                    ranksConfig.getStringList("ProxyChatRanks.ranks-config." + rank + ".players"),
                    ranksConfig.getStringList("ProxyChatRanks.ranks-config." + rank + ".permissions")
            ) );
        }
    }
    public static void load_xProxy_ranks(String encoded) {
        RankProcessor.decodeRankConfig(encoded);
        loading = false;
        System.out.println("[ProxyChat] xProxy Ranks Loaded.");
    }
    public static String getProxyChatRanksPrefix(Player p) { return getRank(p).getPrefix();}
}
