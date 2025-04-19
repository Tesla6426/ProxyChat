package net.txsla.proxychat.discord;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.txsla.proxychat.ProxyChat;
import net.txsla.proxychat.send;
import org.jetbrains.annotations.NotNull;

public class bot{
    public static void start() {
        String token = ProxyChat.config.getString("discord.token");
        String playing = "developing plugins";

        try {
            JDA bot = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(playing))
                    .setStatus(OnlineStatus.ONLINE)
                    .addEventListeners(new listener())
                    .build();
        }catch (Exception e) {
            System.out.println("Unable to connect to discord bot. Did you provide a valid token?");
        }


    }
}
