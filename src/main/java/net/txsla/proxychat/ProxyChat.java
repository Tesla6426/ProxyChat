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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = "proxychat",
        name = "ProxyChat",
        version = "dev"
)
public class ProxyChat {
    @Inject
    private Logger logger;
    public static ProxyServer proxy;
    private static YamlDocument config;
    @Inject
    public ProxyChat(ProxyServer thisProxy, Logger logger, @DataDirectory Path dataDir) {
        proxy = thisProxy; // I promise this is best practice
        try {
            // config file magic (I followed a tutorial for this one as I would rather eat glass than read more documentation)
            config = YamlDocument.create(new File(dataDir.toFile(), "config.yml"),
                    getClass().getResourceAsStream("/config.yml"),
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

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // Set Configs

        // Start xProxyClient IF enabled in configs

        // Scan servers into channels

        // Start relaying messages :)
    }
    public void GlobalConfigs() {
        // note to self:
        // MAKE THE PLUGIN ACTUALLY WORK BEFORE ADDING THE CONFIGS!!!!
        // DON'T BE RESTARTED THIS TME
    }
}
