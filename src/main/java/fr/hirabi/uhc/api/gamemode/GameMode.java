package fr.hirabi.uhc.api.gamemode;

import org.bukkit.inventory.ItemStack;

public interface GameMode {

    String getId();

    String getName();

    String getDescription();

    ItemStack getIcon();

    void onEnableMode();

    void onDisableMode();

    void onGameStart();

    void onGameEnd();
}
