package net.txsla.proxychat.rank;

import net.txsla.proxychat.rank.Rank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static net.txsla.proxychat.ProxyChat.config;

public class RankProcessor {
    public static String encodeRankConfig() {
        // add rank version
        String out = Rank.version + "¦"; // +ranks go into the array in a b64 list

        // encode default rank (players list should be 'null')
        out += encode( "default¦" + encode(ranks.defaultRank.getPrefix()) + "¦" + encode("null") + "¦" + encode(ranks.defaultRank.getPermissions().toString()) );

        // encode each rank and add it to the string
        for (Rank rank : ranks.ranks) {
            out += "," + encode( rank.getName() + "¦" + encode(rank.getPrefix()) + "¦" + encode( String.join( "¦", rank.getPlayers())) + "¦" + encode(String.join("¦",rank.getPermissions() )));
        }
        return out;
    }
    public static void decodeRankConfig(String encoded_config) {
        // decode the encoded config directly to live vars
        encoded_config = decode(encoded_config);
        // get rank config version, for future compatibility
        String rank_config_version = encoded_config.split("¦")[0];

        // get a list of encoded ranks
        String[] encoded_ranks = encoded_config.split("¦")[1].split(",");

        ranks.loading = true;

        // temp var to prevent declaring in a loop
        String[] encoded_rank;

        // erase current ranks
        ranks.ranks = null;
        ranks.ranks = new ArrayList<>();

        // default rank
        System.out.println("Decoding Default Rank");
        encoded_rank = decode(encoded_ranks[0]).split("¦");
        ranks.defaultRank = new Rank(
                encoded_rank[0],
                decode(encoded_rank[1]),
                decodeList(encoded_rank[2]),
                decodeList(encoded_rank[3])
        );

        for (int i = 1; i < encoded_ranks.length; i++) {
            encoded_rank = decode(encoded_ranks[i]).split("¦");
            ranks.ranks.add(new Rank(
                    encoded_rank[0],
                    decode(encoded_rank[1]),
                    decodeList(encoded_rank[2]),
                    decodeList(encoded_rank[3])
            ));

            System.out.println("\nLoading Rank " + encoded_rank[0] +
                    "\nPrefix: " + decode(encoded_rank[1]) +
                    "\nPlayers: " + decodeList(encoded_rank[2]) +
                    "\nPermissions: " + decodeList(encoded_rank[3]) + "\n\n"
            );

        }

        // hopefully this works

        ranks.loading = false;
    }
    public static List<String> decodeList(String encodedList) {
        try {
            return Arrays.stream(decode(encodedList).split("¦")).toList();
        } catch (Exception e) {
            return null;
        }
    }
    public static String encode(String encode) { return new String(Base64.getEncoder().encode(encode.getBytes()));}
    public static String decode(String b64) { return new String(Base64.getDecoder().decode(b64));}
}

