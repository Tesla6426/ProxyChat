package net.txsla.proxychat;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.slf4j.Logger;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Plugin(
        id = "proxychat",
        name = "ProxyChat",
        version = "dev"
)
public class ProxyChat {
    public static String proxyName;
    @Inject
    public static Path dir;
    public static ProxyServer proxy;
    public static YamlDocument config;
    public static boolean xProxyEnabled;
    @Inject
    public ProxyChat(ProxyServer thisProxy, Logger logger, @DataDirectory Path dataDir) {
        proxy = thisProxy; // I promise this is best practice
        dir = dataDir;
        try {
            // config file magic (I followed a tutorial for this one and I would rather eat glass than read more documentation)
            config = YamlDocument.create(new File(dataDir.toFile(), "config.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/config.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
                    );
            config.update();
            config.save();
        }
        catch (Exception e) {
            // shut down plugin if config fails to load
            logger.error("Failed to load config - ProxyChat is shutting down");
            // thank god for the tab button
            Optional<PluginContainer> container = proxy.getPluginManager().getPlugin("proxychat");
            container.ifPresent(pluginContainer -> pluginContainer.getExecutorService().shutdown());
        }

        // load config vars
        loadConfigs();

        // load ProxyChat Ranks Config File (if enabled)
        if (ranks.rankSystem == 1) {
            System.out.println("[ProxyChat] Loading ProxyChat Ranks...");

            try {
                // load ranks config file
                ranks.ranksConfig = YamlDocument.create(new File(ProxyChat.dir.toFile(), "ranks.yml"),
                        Objects.requireNonNull(getClass().getResourceAsStream("/ranks.yml")),
                        GeneralSettings.DEFAULT,
                        LoaderSettings.builder().setAutoUpdate(true).build(),
                        DumperSettings.DEFAULT,
                        UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
                );
                ranks.ranksConfig.update();
                ranks.ranksConfig.save();
                System.out.println("[ProxyChat] Ranks Config Loaded!");
            }
            catch (Exception e) {
                System.out.println("\n\n" + e);
                System.out.println("[ProxyChat] [ERROR] Failed to load ProxyChat Ranks Config!\n\n");
                // disable ranks
                ranks.rankSystem = 0;
            }
            ranks.loadRanks();
        }

        // Mute Config
        System.out.println("[ProxyChat] Loading Mute Config...");
        try {
            // load mute config file
            mute.muteConfig = YamlDocument.create(new File(ProxyChat.dir.toFile(), "muted.yml"),
                    Objects.requireNonNull(getClass().getResourceAsStream("/muted.yml")),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).setOptionSorting(UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS).build()
            );
            mute.muteConfig.update();
            mute.muteConfig.save();
            System.out.println("[ProxyChat] Mute Config Loaded!");
        }
        catch (Exception e) {
            System.out.println("\n==========\n" + e);
            System.out.println("[ProxyChat] [ERROR] Failed to load Mute Config!\n==========\n");
        }

        mute.requireReason = mute.muteConfig.getBoolean("require-reason");
        mute.loadMuteList();

        // start discord bot
        net.txsla.proxychat.discord.bot.start();

        // start xProxy client if applicable
        if (xProxyEnabled) initialiseXProxy();
    }

    @Subscribe // < --- sub to my YouTube also :) {I do not ever post though so don't expect much}
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getEventManager().register(this, new listener());
        loadChannels();

        // load commands
        CommandManager commandManager = proxy.getCommandManager();
        commandManager.register(commandManager.metaBuilder("mute").plugin(this).build(), net.txsla.proxychat.commands.mute.muteCommand(proxy) );
        commandManager.register(commandManager.metaBuilder("mute-list").plugin(this).build(), net.txsla.proxychat.commands.mute_list.muteListCommand(proxy) );
        commandManager.register(commandManager.metaBuilder("mute-status").plugin(this).build(), net.txsla.proxychat.commands.mute_status.muteStatusCommand(proxy) );
        commandManager.register(commandManager.metaBuilder("unmute").plugin(this).build(), net.txsla.proxychat.commands.unmute.unmuteCommand(proxy) );
    }

    // load misc config vars - I will most likely clean this up in a later update
    public void loadConfigs() {
        // load global config vars
        xProxyEnabled = config.getBoolean("xProxy.enabled");

        format.message_format = config.getString("message-format");
        format.leave_format = config.getString("join-messages.leave-format");
        format.join_format = config.getString("join-messages.join-format");
        format.dc2mc_format = config.getString("");
        format.mc2dc_format = config.getString("");

        proxyName = config.getString("proxy-name");
        send.reportFailedMessages = config.getBoolean("reportFailedMessages");
        log.enabled = config.getBoolean("log-messages");
        try {log.loadLogFile();} catch (Exception e) {e.printStackTrace();}
        switch (config.getString("rank-system").toLowerCase()) {
            case "proxychat":
            case "proxy-chat":
            case "proxy_chat":
                ranks.rankSystem = 1;
                break;
            case "xproxy":
            case "x-proxy":
            case "x_proxy":
                ranks.rankSystem = 2;
                break;
            default:
                ranks.rankSystem = 0;
                break;
        }
        // spam filter
        spamLimiter.enabled = config.getBoolean("spam-limiter.enabled");
        if (spamLimiter.enabled)
        {
            spamLimiter.decTimer = config.getInt("spam-limiter.decrement-timer");
            spamLimiter.maxCounter = config.getInt("spam-limiter.counter-ceiling");
            spamLimiter.startDecTimer();
        }
    }

    // Load channels from config into ram
    public void loadChannels() {
        // accounts for channel 0 (skips null check)
        send.channel.add( config.getStringList("channels.0") );

        // add servers listed in config to a String List Array until it reaches a null channel
        int x = 1;
        while (!config.getStringList("channels." + x).isEmpty()) {
            send.channel.add(config.getStringList("channels." + x)); x++;
        }
    }

    // Connect to an XProxy server if enabled
    public static void initialiseXProxy() {
        String password = config.getString("xProxy.password"); // set to a SECURE PASSWORD, as of night now, there is NO BRUTE FORCE PROTECTION
        String ip = config.getString("xProxy.xProxy-server-ip");
        int port = config.getInt("xProxy.xProxy-server-port"); // DO NOT PORT FORWARD UNLESS YOU KNOW *EXACTLY* WHAT YOU ARE DOING

        // xProxy Thread (I pinky promise this is best practice and not some old recycled code)
        System.out.println("[ProxyChat] starting xProxy client");
        new Thread(() -> {
            Socket socket;
            try {
                socket = new Socket(ip, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            xProxyClient client = new xProxyClient(socket, proxyName, password);
            client.listener();
            client.send();
            xProxyClient.out = ("con¦" + password);
        }).start();
    }
}
