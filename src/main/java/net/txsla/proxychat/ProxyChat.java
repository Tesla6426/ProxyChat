package net.txsla.proxychat;

import com.google.inject.Inject;
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
    private static YamlDocument config;
    @Inject
    public ProxyChat(ProxyServer thisProxy, Logger logger, @DataDirectory Path dataDir) {
        proxy = thisProxy; // I promise this is best practice
        dir = dataDir;
        try {
            // config file magic (I followed a tutorial for this one as I would rather eat glass than read more documentation)
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
            }
            catch (Exception e) {
                System.out.println("[ProxyChat] Failed to load ProxyChat Ranks Config!");
                // disable ranks
                ranks.rankSystem = 0;
            }
            ranks.loadRanks();
        }
    }

    @Subscribe // < --- sub to my YouTube also :) {I do not ever post though so don't expect much}
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // register listener
        proxy.getEventManager().register(this, new listener());


        loadChannels();

        // Start xProxyClient IF enabled in configs

        // Scan servers into channels

        // Start relaying messages :)
    }
    public void loadConfigs() {
        // load global config vars
        format.format = config.getString("message-format");
        proxyName = config.getString("proxy-name");
        listener.logMessages = config.getBoolean("log-messages");
        send.reportFailedMessages = config.getBoolean("reportFailedMessages");
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
    }
    public void loadChannels() {
        // accounts for channel 0 (skips null check)
        send.channel.add( config.getStringList("channels.0") );

        // add servers listed in config to a String List Array until it reaches a null channel
        int x = 1;
        while (!config.getStringList("channels." + x).isEmpty()) {
            send.channel.add(config.getStringList("channels." + x)); x++;

        }
    }
}
