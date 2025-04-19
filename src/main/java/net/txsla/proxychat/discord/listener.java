package net.txsla.proxychat.discord;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.txsla.proxychat.ProxyChat;
import net.txsla.proxychat.send;
import org.jetbrains.annotations.NotNull;

public class listener extends ListenerAdapter{
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String channel_id = ProxyChat.config.getString("discord.channel-id");
        String message = event.getMessage().getContentRaw();
        User user = event.getAuthor();
        Member member = event.getMember();
        String name = user.getName();
        Role rank = member.getRoles().get(1);

        // ignore bots
        if (event.getAuthor().isBot()) return;

        // ignore if not from the proper channel
        if (!event.getMessage().getChannelId().equals(channel_id)) return;

        // send message to Minecraft
        send.messageChannel(send.getChannel("discord"), "[ProxyChat] [Discord] [" + rank + "] " + name + " : " + message);

    }
}
