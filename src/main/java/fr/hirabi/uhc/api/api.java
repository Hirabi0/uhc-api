package main.java.fr.hirabi.uhc.api;

import fr.hirabi.uhc.api.command.ApiCommand;
import fr.hirabi.uhc.api.gui.ApiConfigListener;
import fr.hirabi.uhc.api.gui.SimpleGuiListener;
import fr.hirabi.uhc.api.role.RoleAPI;
import fr.hirabi.uhc.api.role.RoleListener;
import fr.hirabi.uhc.api.role.RoleManager;
import fr.hirabi.uhc.api.gamemode.DefaultUhcMode;
import fr.hirabi.uhc.api.gamemode.GameModeAPI;
import fr.hirabi.uhc.api.gamemode.ModeConfigAPI;
import fr.hirabi.uhc.api.event.UHCEpisodeChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class api extends JavaPlugin {

    private int currentEpisode = 1;
    private long episodeTicks = 0L;
    // Durée d'un épisode en ticks (configurable via config.yml)
    private long episodeLengthTicks = 24000L;

    private boolean lastWasNight = false;

    @Override
    public void onEnable() {
        // Charger la configuration de l'API
        this.saveDefaultConfig();
        reloadEpisodeLength();

        // Enregistrer le mode de jeu par défaut
        GameModeAPI.registerMode(new DefaultUhcMode());
        GameModeAPI.setActiveMode("uhc");

        // Enregistrer le listener générique des rôles
        Bukkit.getPluginManager().registerEvents(new RoleListener(), this);

        // Enregistrer le listener de GUI et la commande de config
        Bukkit.getPluginManager().registerEvents(new ApiConfigListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SimpleGuiListener(), this);
        if (this.getCommand("uhcapi") != null) {
            this.getCommand("uhcapi").setExecutor(new ApiCommand(this));
        }

        // Tâche qui tourne chaque seconde pour gérer jour/nuit + épisodes
        new BukkitRunnable() {
            @Override
            public void run() {
                RoleManager manager = RoleAPI.getRoleManager();
                if (manager == null) {
                    return; // Aucun gestionnaire de rôles enregistré pour l'instant
                }

                World world = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
                if (world == null) {
                    return;
                }

                long time = world.getTime(); // 0..23999
                boolean isNight = time >= 12300 && time < 23850;

                // Détection des transitions jour/nuit
                if (isNight && !lastWasNight) {
                    // Passage jour -> nuit
                    manager.onNightStart();
                } else if (!isNight && lastWasNight) {
                    // Passage nuit -> jour
                    manager.onDayStart();
                }
                lastWasNight = isNight;

                // Gestion des épisodes (basé sur le temps reel du serveur)
                episodeTicks += 20L; // cette tâche tourne toutes les 20 ticks (1 seconde)
                if (episodeTicks >= episodeLengthTicks) {
                    episodeTicks = 0L;
                    int oldEpisode = currentEpisode;
                    currentEpisode++;
                    manager.onEpisodeStart(currentEpisode);
                    Bukkit.getPluginManager().callEvent(new UHCEpisodeChangeEvent(oldEpisode, currentEpisode));
                }
            }
        }.runTaskTimer(this, 20L, 20L); // délai 1s, répétition toutes les 1s
    }

    @Override
    public void onDisable() {
        ModeConfigAPI.save();
    }

    public void reloadEpisodeLength() {
        int episodeLengthSeconds = this.getConfig().getInt("episodes.length-seconds", 1200);
        if (episodeLengthSeconds <= 0) {
            episodeLengthSeconds = 1200;
        }
        this.episodeLengthTicks = episodeLengthSeconds * 20L;
    }

    public void forceNextEpisode() {
        int oldEpisode = currentEpisode;
        currentEpisode++;
        RoleManager manager = RoleAPI.getRoleManager();
        if (manager != null) {
            manager.onEpisodeStart(currentEpisode);
        }
        Bukkit.getPluginManager().callEvent(new UHCEpisodeChangeEvent(oldEpisode, currentEpisode));
    }
}
