package fr.hirabi.uhc.api.config;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ApiConfigEntry {

    ItemStack getIcon();

    String getId();

    void onClick(Player player);
}
