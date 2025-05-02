package net.txsla.proxychat.discord;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.txsla.proxychat.ProxyChat;
import net.txsla.proxychat.format;
import net.txsla.proxychat.send;
import org.jetbrains.annotations.NotNull;

public class listener extends ListenerAdapter{
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String channel_id = ProxyChat.config.getString("discord.channel-id");
        String message = event.getMessage().getContentRaw();

        // ignore bots
        if (event.getAuthor().isBot()) return;

        // ignore if not from the proper channel
        if (!event.getMessage().getChannelId().equals(channel_id)) return;

        // send message to Minecraft
        send.messageChannel("discord", format.discord2MinecraftMessage(event));

    }
}
