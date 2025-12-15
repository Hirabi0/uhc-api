package fr.hirabi.uhc.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleGui {

    private static final Map<Inventory, SimpleGui> GUI_BY_INVENTORY = new HashMap<Inventory, SimpleGui>();

    private final UUID id;
    private final int rows;
    private final String title;
    private final Map<Integer, GuiClickHandler> handlers = new HashMap<Integer, GuiClickHandler>();

    private Inventory inventory;

    public SimpleGui(int rows, String title) {
        if (rows < 1) rows = 1;
        if (rows > 6) rows = 6;
        this.rows = rows;
        this.title = title;
        this.id = UUID.randomUUID();
    }

    private void ensureInventory() {
        if (inventory == null) {
            inventory = Bukkit.createInventory(null, rows * 9, title);
            GUI_BY_INVENTORY.put(inventory, this);
        }
    }

    public void setItem(int slot, ItemStack item, GuiClickHandler handler) {
        ensureInventory();
        if (slot < 0 || slot >= inventory.getSize()) {
            return;
        }
        inventory.setItem(slot, item);
        if (handler != null) {
            handlers.put(slot, handler);
        } else {
            handlers.remove(slot);
        }
    }

    public void open(Player player) {
        ensureInventory();
        player.openInventory(inventory);
    }

    GuiClickHandler getHandler(int slot) {
        return handlers.get(slot);
    }

    static SimpleGui getByInventory(Inventory inv) {
        return GUI_BY_INVENTORY.get(inv);
    }

    public UUID getId() {
        return id;
    }

    public interface GuiClickHandler {
        void onClick(Player player, ClickType clickType);
    }

    /*
    Exemple d'utilisation :

    SimpleGui gui = new SimpleGui(1, "Exemple");
    ItemStack item = new ItemStack(Material.STONE);
    gui.setItem(0, item, new SimpleGui.GuiClickHandler() {
        @Override
        public void onClick(Player player, ClickType clickType) {
            player.sendMessage("Vous avez cliqué sur la pierre !");
        }
    });
    gui.open(player);
    */
}
