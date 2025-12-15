package main.java.fr.hirabi.uhc.api.gui;

import fr.hirabi.uhc.api.config.ApiConfigEntry;
import fr.hirabi.uhc.api.config.ApiConfigRegistry;
import main.java.fr.hirabi.uhc.api.api;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class ApiConfigGUI {

    private static final String MAIN_TITLE = ChatColor.DARK_GREEN + "UHC-API Config";
    private static final String EPISODE_TITLE = ChatColor.DARK_GREEN + "Durée des épisodes";

    public static void openMainMenu(Player player, JavaPlugin plugin) {
        // Inventaire 3 lignes pour un rendu plus aéré
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);

        // Fond décoratif en vitres teintées
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        int episodeLengthSeconds = plugin.getConfig().getInt("episodes.length-seconds", 1200);
        int minutes = episodeLengthSeconds / 60;

        // Bouton durée des épisodes au centre de la ligne du milieu
        ItemStack episodeItem = new ItemStack(Material.WATCH);
        ItemMeta meta = episodeItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Durée des épisodes");
        meta.setLore(Arrays.asList(ChatColor.YELLOW + "Actuellement: " + minutes + " min", ChatColor.GRAY + "Clique pour modifier"));
        episodeItem.setItemMeta(meta);

        inv.setItem(13, episodeItem);

        // Bouton pour forcer l'épisode suivant, à droite
        ItemStack forceEpisode = new ItemStack(Material.NETHER_STAR);
        ItemMeta forceMeta = forceEpisode.getItemMeta();
        forceMeta.setDisplayName(ChatColor.RED + "Forcer l'épisode suivant");
        forceMeta.setLore(Arrays.asList(ChatColor.GRAY + "Passe immédiatement à l'épisode suivant"));
        forceEpisode.setItemMeta(forceMeta);

        inv.setItem(15, forceEpisode);

        // Emplacements réservés pour les entrées de config des plugins (ligne du haut)
        int[] extraSlots = new int[] {10, 11, 12, 14, 16};
        int index = 0;
        for (ApiConfigEntry entry : ApiConfigRegistry.getEntries()) {
            if (index >= extraSlots.length) {
                break;
            }
            int slot = extraSlots[index++];
            ItemStack icon = entry.getIcon();
            if (icon == null) {
                continue;
            }
            icon = icon.clone();
            ItemMeta entryMeta = icon.getItemMeta();
            if (entryMeta != null) {
                java.util.List<String> lore = entryMeta.getLore();
                if (lore == null) {
                    lore = new java.util.ArrayList<String>();
                }
                lore.add(ChatColor.DARK_GRAY + "ID: " + entry.getId());
                entryMeta.setLore(lore);
                icon.setItemMeta(entryMeta);
            }
            inv.setItem(slot, icon);
        }
        player.openInventory(inv);
    }

    public static void openEpisodeMenu(Player player, JavaPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 9, EPISODE_TITLE);

        // Durée actuelle pour affichage au centre
        int episodeLengthSeconds = plugin.getConfig().getInt("episodes.length-seconds", 1200);
        int minutes = episodeLengthSeconds / 60;

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.AQUA + "Durée actuelle");
        infoMeta.setLore(Arrays.asList(ChatColor.YELLOW + String.valueOf(minutes) + " minute(s)", ChatColor.GRAY + "Utilise les boutons autour"));
        info.setItemMeta(infoMeta);

        // Boutons de modification
        inv.setItem(1, createButton(Material.REDSTONE_TORCH_ON, ChatColor.RED + "-10 min", -10));
        inv.setItem(2, createButton(Material.REDSTONE_TORCH_ON, ChatColor.RED + "-5 min", -5));
        inv.setItem(3, createButton(Material.REDSTONE_TORCH_ON, ChatColor.RED + "-1 min", -1));
        inv.setItem(4, info);
        inv.setItem(5, createButton(Material.LEVER, ChatColor.GREEN + "+1 min", 1));
        inv.setItem(6, createButton(Material.LEVER, ChatColor.GREEN + "+5 min", 5));
        inv.setItem(7, createButton(Material.LEVER, ChatColor.GREEN + "+10 min", 10));

        player.openInventory(inv);
    }

    private static ItemStack createButton(Material material, String name, int deltaMinutes) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Clique pour ajuster de " + deltaMinutes + " min"));
        item.setItemMeta(meta);
        return item;
    }

    public static void applyEpisodeDelta(Player player, JavaPlugin plugin, int deltaMinutes) {
        int currentSeconds = plugin.getConfig().getInt("episodes.length-seconds", 1200);
        int newSeconds = currentSeconds + deltaMinutes * 60;
        if (newSeconds < 60) {
            newSeconds = 60; // minimum 1 minute
        }
        plugin.getConfig().set("episodes.length-seconds", newSeconds);
        plugin.saveConfig();

        // Mettre à jour la durée des épisodes en mémoire
        api apiPlugin = JavaPlugin.getPlugin(api.class);
        apiPlugin.reloadEpisodeLength();

        int minutes = newSeconds / 60;
        player.sendMessage(ChatColor.GREEN + "Durée des épisodes réglée sur " + minutes + " minute(s). ");
        openEpisodeMenu(player, plugin);
    }
}
