package net.txsla.proxychat;

import com.google.inject.Inject;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.text.Component;
import net.txsla.proxychat.xProxy.xProxyClient;
import org.slf4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;

@Plugin(
        id = "proxychat",
        name = "ProxyChat",
        version = "0.1-dev"
)
public class ProxyChat {

    public static Logger logger;
    public final ProxyServer proxy;
    public static YamlDocument config;
    public static List<RegisteredServer>[] channel;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws IOException {
        // for xProxy
        String username = config.getString("xProxy.name");
        String password = config.getString("password");
        String ip = config.getString("xProxy-server-ip");
        int port = 25599;


        if ( config.getBoolean("xProxy.enable") ) {
            // start xProxy Client
            msgMngr.xProxyEnabled = true;
            System.out.println("Starting xProxy Client");
            new Thread(new Runnable() { // xProxy Thread
                @Override
                public void run() {
                    Socket socket = null;
                    try {
                        socket = new Socket(ip, port);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    xProxyClient client = new xProxyClient(socket, username, password);
                    client.listener();
                    client.send();
                    xProxyClient.out = ("con¦" + password);
                }
            }).start();
        }

    }
    @Inject
    public ProxyChat(ProxyServer proxy, Logger logger, @DataDirectory Path configDirectory) {
    this.proxy = proxy; this.logger = logger;

    // Initialise config
    try {
        config = YamlDocument.create(new File(configDirectory.toFile(), "config.yml"),
                //getClass().getResourceAsStream(File.separator + "config.yml"),
                getClass().getResourceAsStream("/config.yml"),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
        );
        config.update();
        config.save();
    } catch (IOException ex) {
        logger.error("[Startup] Error loading config!");
    }


    logger.info("Plugin Loaded");
    }

    @Subscribe
    public void onChat(PlayerChatEvent event) {

        // get message & sender data
        Player p = event.getPlayer();
        String username = p.getUsername();
        String UUID = "" + p.getUniqueId();
        String server = String.valueOf(p.getCurrentServer());
        String message = event.getMessage();

        // get channel
        int channel = msgMngr.getChannel(server);

        // format message
        Component formattedMessage = msgMngr.formatMessage(server, username, UUID, message);

        // send message to other servers/proxies
        msgMngr.sendMessage(channel, formattedMessage);
        if (msgMngr.xProxyEnabled) msgMngr.xProxySendMessage(channel, server, username, UUID, message);



    }
}

