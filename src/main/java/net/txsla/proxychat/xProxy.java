package net.txsla.proxychat;

import java.util.Base64;

import static java.lang.System.out;

public class xProxy {
    // this code is ugly as fuck - remind me to rewrite this sometime next year
    public static void receive(String in) {
        new Thread(() -> {
            // manage xProxy received messages
                if (in != null) {
                    // no one cares who sent the message :(
                    String sender = in.substring(0, in.indexOf("¦")); // sender is the POXY who sent
                    String com = in.substring(in.indexOf("¦")+1, in.lastIndexOf("¦"));
                    String whoFor = in.substring(in.lastIndexOf("¦")+1, in.lastIndexOf("-")); // damn this code sucks
                    // decodes the data after 'proxychat-' in the incoming packet
                    String b64data = new String(Base64.getDecoder().decode(in.substring(in.lastIndexOf("-")+1, in.length())));
                    switch (com) {
                        // check if message was for ProxyChat
                        case "bdc":
                            if (whoFor.equals("proxychat")) {
                                // format and send message
                                String[] msg = b64data.split("¦");
                                send.messageChannel( msg[1], format.xProxyMessage(msg[0], msg[1], msg[2], msg[3], msg[4]) );
                                // relay to console
                                out.println("[" + msg[0] + "] [" + msg[1] + "] <" + msg[2] + "> " + msg[4]);
                                if (log.enabled) log.add( "[" + msg[0] + "] [" + msg[1] + "] <" + msg[2] + "> " + msg[4]);
                            }
                            break;
                        case "other":
                            break;
                    }
                }
        }).start();
    }
}
