package net.txsla.proxychat.X_Proxy;

import net.txsla.proxychat.format;
import net.txsla.proxychat.log;
import net.txsla.proxychat.rank.ranks;
import net.txsla.proxychat.send;

import java.util.Base64;

import static java.lang.System.out;

public class xProxy {
    // this code is ugly as fuck - remind me to rewrite this sometime next year
    public static void receive(String in) {
        new Thread(() -> {
            // manage xProxy received messages
                if (in != null) {

                    // no one cares who sent the message :(
                    String sender = in.split("¦")[0];
                    String com = in.split("¦")[1];
                    String data = in.split("¦")[2];
                    String whoFor = data.split("-")[0];

                    switch (com) {
                        // check if message was for ProxyChat
                        case "bdc":
                            if (whoFor.equals("proxychat")) {
                                // decode, format and send message
                                String[] msg = decode( data.split("-")[1] ).split("¦");
                                send.messageChannel( msg[1], format.xProxyMessage(msg[0], msg[1], msg[2], msg[3], msg[4]) );
                                // relay to console
                                out.println("[" + msg[0] + "] [" + msg[1] + "] <" + msg[2] + "> " + msg[4]);
                                if (log.enabled) log.add( "[" + msg[0] + "] [" + msg[1] + "] <" + msg[2] + "> " + msg[4]);
                            }
                            if (whoFor.equals("xproxyrank"))  {
                                // load ranks from encoded string
                                ranks.load_xProxy_ranks( data.split("-")[1] );
                            }
                            break;
                        case "other":
                            break;
                    }
                }
        }).start();
    }
    public static String encode(String encode) { return new String(Base64.getEncoder().encode(encode.getBytes()));}
    public static String decode(String b64) { return new String(Base64.getDecoder().decode(b64));}
}
