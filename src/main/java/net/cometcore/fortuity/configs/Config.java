package net.cometcore.fortuity.configs;

import net.cometcore.fortuity.FortuitySpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Config {

    private final Plugin plugin;
    private final String name;

    private File database = null;
    private FileConfiguration databaseConfig = null;
    private boolean firstLoad = false;

    /**
     *
     * @param plugin Plugin's main file
     * @param name Name of the configuration
     */
    public Config(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.setup();
    }

    /**
     * Set up the configuration
     */
    public void setup() {
        if (this.database == null) database = new File(plugin.getDataFolder(), name + ".yml");
        if (!database.exists()) {
            if (database.getParentFile().exists()) database.getParentFile().mkdir();
            plugin.saveResource(name + ".yml", false);
            firstLoad = true;
        }
    }

    /**
     * Get the configuration
     * @return The configuration
     */
    public FileConfiguration get() {
        if (databaseConfig == null) reload();
        return databaseConfig;
    }

    /**
     * Save the configuration
     */
    public void save() {
        if (database == null || databaseConfig == null) return;
        try {
            get().save(database);
        } catch (IOException e) {
            Logger logger = FortuitySpigot.getLOGGER();
            logger.severe("Error when saving the file " + name + ".yml");
            logger.severe("This is a bug, please report it to the developer " + e);
            logger.warning(e.getMessage());
        }
        firstLoad = false;
    }

    /**
     * Reload the configuration
     */
    public void reload() {
        if (this.database == null) database = new File(plugin.getDataFolder(), name + ".yml");
        this.databaseConfig = YamlConfiguration.loadConfiguration(database);

        InputStream defaultStream = plugin.getResource(name + ".yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.databaseConfig.setDefaults(defaultConfig);
        }
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }
}
