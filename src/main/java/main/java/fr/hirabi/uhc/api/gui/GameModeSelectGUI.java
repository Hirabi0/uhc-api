package main.java.fr.hirabi.uhc.api.gui;

import fr.hirabi.uhc.api.gamemode.GameMode;
import fr.hirabi.uhc.api.gamemode.GameModeAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameModeSelectGUI {

    private static final String TITLE = ChatColor.DARK_BLUE + "Sélection du mode de jeu";

    public static void open(Player viewer) {
        Collection<GameMode> modes = GameModeAPI.getRegisteredModes();
        if (modes.isEmpty()) {
            viewer.sendMessage("§cAucun mode de jeu n'est enregistré.");
            return;
        }

        int size = 9;
        int count = modes.size();
        while (size < count && size < 54) {
            size += 9;
        }

        Inventory inv = Bukkit.createInventory(null, size, TITLE);

        int slot = 0;
        GameMode active = GameModeAPI.getActiveMode();
        for (GameMode mode : modes) {
            if (slot >= inv.getSize()) {
                break;
            }
            ItemStack icon = mode.getIcon();
            if (icon == null) {
                continue;
            }
            icon = icon.clone();
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + mode.getName());

            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "ID: " + mode.getId());
            lore.add(ChatColor.DARK_GRAY + mode.getDescription());
            if (active != null && active == mode) {
                lore.add(ChatColor.GREEN + "(Actif)");
            }
            meta.setLore(lore);
            icon.setItemMeta(meta);

            inv.setItem(slot++, icon);
        }

        viewer.openInventory(inv);
    }
}
