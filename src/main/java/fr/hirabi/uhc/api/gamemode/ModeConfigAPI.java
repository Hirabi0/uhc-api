package fr.hirabi.uhc.api.gamemode;

import main.java.fr.hirabi.uhc.api.api;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ModeConfigAPI {

    private static File configFile;
    private static FileConfiguration config;

    private ModeConfigAPI() {
    }

    private static void ensureLoaded() {
        if (config != null) {
            return;
        }

        JavaPlugin plugin = JavaPlugin.getPlugin(api.class);
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        configFile = new File(plugin.getDataFolder(), "modes.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        if (config.getConfigurationSection("modes") == null) {
            config.createSection("modes");
        }
    }

    public static ConfigurationSection getModeConfig(String modeId) {
        ensureLoaded();
        if (modeId == null || modeId.isEmpty()) {
            return config.getConfigurationSection("modes");
        }

        String path = "modes." + modeId;
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }
        return section;
    }

    public static void save() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
