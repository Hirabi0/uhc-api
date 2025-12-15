package main.java.fr.hirabi.uhc.api.gui;

import fr.hirabi.uhc.api.role.Role;
import fr.hirabi.uhc.api.role.RoleAPI;
import fr.hirabi.uhc.api.role.RoleManager;
import fr.hirabi.uhc.api.role.Camp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RoleViewGUI {

    private static final String TITLE = ChatColor.DARK_PURPLE + "Rôles des joueurs";

    public static void open(Player viewer) {
        RoleManager manager = RoleAPI.getRoleManager();
        if (manager == null) {
            viewer.sendMessage("§cAucun RoleManager n'est enregistré.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 54, TITLE);
        int slot = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (slot >= inv.getSize()) break;

            ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            ItemMeta meta = head.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + target.getName());

            List<String> lore = new ArrayList<>();
            Role role = manager.getRole(target);
            if (role == null) {
                lore.add(ChatColor.GRAY + "Rôle: " + ChatColor.RED + "Aucun");
            } else {
                lore.add(ChatColor.GRAY + "Rôle: " + ChatColor.AQUA + role.getName());
                Camp camp = role.getCamp();
                if (camp != null) {
                    lore.add(ChatColor.GRAY + "Camp: " + camp.getColor() + camp.getDisplayName());
                }
            }

            meta.setLore(lore);
            head.setItemMeta(meta);

            inv.setItem(slot++, head);
        }

        viewer.openInventory(inv);
    }
}
