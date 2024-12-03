package net.txsla.proxychat;

import com.velocitypowered.api.proxy.Player;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ranks {
    public static YamlDocument ranksConfig;
    public static Rank defaultRank;
    public static List<Rank> ranks;
    public static int rankSystem; // 0 = Disabled, 1 = ProxyChat, 2 = xProxy, 3 = hopefully coming soon (either vault or placeholderAPI)
    public static String getPrefix(Player p) {
        // return empty string if ranks are disabled
        if (rankSystem == 0 ) return "";
        if (rankSystem == 1) return getProxyChatRanksPrefix(p);
        return "[RANK]";
    }
    public static void loadRanks() {
        switch (rankSystem) {
            case 1:
                System.out.println("[ProxyChat] Loading ProxyChat Ranks...");
                loadProxyChatRanks();
                break;
            default:
                System.out.println("[ProxyChat] Ranks are Disabled");
                return;
        }
    }
    public static Rank getRank(Player player) {
        for (Rank rank : ranks) {
            if ( rank.getPlayers().contains(player.getUsername()) ) {
                return rank;
            }
        }
        // return default rank if no rank was found
        return defaultRank;
    }
    public static void loadProxyChatRanks() {
        // reset ranks to not mem leak
        ranks = new ArrayList<>();

        // load default rank
        defaultRank = new Rank("default", ranksConfig.getString("ProxyChatRanks.ranks-config.default.prefix"));

        // load ranks into array
        for (String rank : ranksConfig.getStringList("ProxyChatRanks.ranks-list")) {
            ranks.add( new Rank(
                    rank,
                    ranksConfig.getString("ProxyChatRanks.ranks-config." + rank + ".prefix"),
                    ranksConfig.getStringList("ProxyChatRanks.ranks-config." + rank + ".players")
            ) );
        }
    }
    public static String getProxyChatRanksPrefix(Player p) { return getRank(p).getPrefix();}
}
