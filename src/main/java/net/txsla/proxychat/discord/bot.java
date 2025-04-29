package net.txsla.proxychat.discord;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.txsla.proxychat.ProxyChat;
import net.txsla.proxychat.send;
import org.jetbrains.annotations.NotNull;

public class bot{
    TextChannel textChannel;
    static JDA bot;
    public static void start() {
        String token = ProxyChat.config.getString("discord.token");
        String playing = "developing plugins";


        bot = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing(playing))
                    .setStatus(OnlineStatus.ONLINE)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(new listener())
                    .build();

    }
    public static void send(String message) {
        bot.getTextChannelById(ProxyChat.config.getString("discord.channel-id")).sendMessage(message).queue();

    }
}
