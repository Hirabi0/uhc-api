package fr.hirabi.uhc.api.gamemode;

import main.java.fr.hirabi.uhc.api.api;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class DefaultUhcMode implements GameMode {

    public String getId() {
        return "uhc";
    }

    public String getName() {
        return "UHC";
    }

    public String getDescription() {
        return "Mode UHC classique sans règles spéciales.";
    }

    public ItemStack getIcon() {
        return new ItemStack(Material.GOLDEN_APPLE);
    }

    public void onEnableMode() {
        // Charger / créer la config du mode dans modes.yml
        ConfigurationSection cfg = ModeConfigAPI.getModeConfig(getId());

        // Valeur par défaut : 1200s ou valeur actuelle de la config principale si définie
        api apiPlugin = JavaPlugin.getPlugin(api.class);
        int defaultSeconds = apiPlugin.getConfig().getInt("episodes.length-seconds", 1200);

        int lengthSeconds = cfg.getInt("episodes.length-seconds", defaultSeconds);
        if (!cfg.isSet("episodes.length-seconds")) {
            cfg.set("episodes.length-seconds", lengthSeconds);
        }

        // Appliquer la valeur au plugin principal et recharger la durée des épisodes
        apiPlugin.getConfig().set("episodes.length-seconds", lengthSeconds);
        apiPlugin.saveConfig();
        apiPlugin.reloadEpisodeLength();
    }

    public void onDisableMode() {
    }

    public void onGameStart() {
    }

    public void onGameEnd() {
    }
}
