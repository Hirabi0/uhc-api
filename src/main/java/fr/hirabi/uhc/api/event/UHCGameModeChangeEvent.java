package fr.hirabi.uhc.api.event;

import fr.hirabi.uhc.api.gamemode.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCGameModeChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final GameMode oldMode;
    private final GameMode newMode;

    public UHCGameModeChangeEvent(GameMode oldMode, GameMode newMode) {
        this.oldMode = oldMode;
        this.newMode = newMode;
    }

    public GameMode getOldMode() {
        return oldMode;
    }

    public GameMode getNewMode() {
        return newMode;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
