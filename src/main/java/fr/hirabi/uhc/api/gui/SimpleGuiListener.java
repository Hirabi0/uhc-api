package fr.hirabi.uhc.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SimpleGuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Inventory inv = event.getInventory();
        SimpleGui gui = SimpleGui.getByInventory(inv);
        if (gui == null) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= inv.getSize()) {
            return;
        }

        SimpleGui.GuiClickHandler handler = gui.getHandler(slot);
        if (handler != null) {
            handler.onClick((Player) event.getWhoClicked(), event.getClick());
        }
    }
}
