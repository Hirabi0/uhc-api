package fr.hirabi.uhc.api.gui;

import main.java.fr.hirabi.uhc.api.api;
import fr.hirabi.uhc.api.gamemode.GameModeAPI;
import main.java.fr.hirabi.uhc.api.gui.ApiConfigGUI;
import main.java.fr.hirabi.uhc.api.gui.GameModeSelectGUI;
import fr.hirabi.uhc.api.config.ApiConfigEntry;
import fr.hirabi.uhc.api.config.ApiConfigRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiConfigListener implements Listener {

    private final JavaPlugin plugin;

    public ApiConfigListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        String title = inv.getTitle();

        if (title == null) {
            return;
        }

        String strippedTitle = ChatColor.stripColor(title);

        if (strippedTitle.equalsIgnoreCase("UHC-API Config")) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getItemMeta() == null || current.getItemMeta().getDisplayName() == null) {
                return;
            }

            String name = ChatColor.stripColor(current.getItemMeta().getDisplayName());
            if (name.equalsIgnoreCase("Durée des épisodes")) {
                ApiConfigGUI.openEpisodeMenu(player, plugin);
            } else if (name.equalsIgnoreCase("Forcer l'épisode suivant")) {
                api apiPlugin = JavaPlugin.getPlugin(api.class);
                apiPlugin.forceNextEpisode();
                player.sendMessage(ChatColor.RED + "Vous avez forcé l'épisode suivant.");
            } else {
                // Essayer de retrouver une ApiConfigEntry via l'ID stocké dans la lore
                if (current.getItemMeta().getLore() != null) {
                    for (String line : current.getItemMeta().getLore()) {
                        String stripped = ChatColor.stripColor(line);
                        if (stripped.startsWith("ID: ")) {
                            String id = stripped.substring("ID: ".length()).trim();
                            if (!id.isEmpty()) {
                                for (ApiConfigEntry entry : ApiConfigRegistry.getEntries()) {
                                    if (entry.getId().equalsIgnoreCase(id)) {
                                        entry.onClick(player);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (strippedTitle.equalsIgnoreCase("Durée des épisodes")) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getItemMeta() == null || current.getItemMeta().getDisplayName() == null) {
                return;
            }

            String name = ChatColor.stripColor(current.getItemMeta().getDisplayName());
            int delta = 0;
            if (name.equalsIgnoreCase("-10 min")) delta = -10;
            else if (name.equalsIgnoreCase("-5 min")) delta = -5;
            else if (name.equalsIgnoreCase("-1 min")) delta = -1;
            else if (name.equalsIgnoreCase("+1 min")) delta = 1;
            else if (name.equalsIgnoreCase("+5 min")) delta = 5;
            else if (name.equalsIgnoreCase("+10 min")) delta = 10;

            if (delta != 0) {
                ApiConfigGUI.applyEpisodeDelta(player, plugin, delta);
            }
        } else if (strippedTitle.equalsIgnoreCase("Sélection du mode de jeu")) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getItemMeta() == null || current.getItemMeta().getDisplayName() == null) {
                return;
            }

            if (current.getItemMeta().getLore() == null || current.getItemMeta().getLore().isEmpty()) {
                return;
            }

            String firstLine = ChatColor.stripColor(current.getItemMeta().getLore().get(0));
            if (!firstLine.startsWith("ID: ")) {
                return;
            }

            String modeId = firstLine.substring("ID: ".length()).trim();
            if (!modeId.isEmpty()) {
                GameModeAPI.setActiveMode(modeId);
                ApiConfigGUI.openMainMenu(player, plugin);
            }
        }
    }
}
