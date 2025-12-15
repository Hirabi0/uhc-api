package fr.hirabi.uhc.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCEpisodeChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final int oldEpisode;
    private final int newEpisode;

    public UHCEpisodeChangeEvent(int oldEpisode, int newEpisode) {
        this.oldEpisode = oldEpisode;
        this.newEpisode = newEpisode;
    }

    public int getOldEpisode() {
        return oldEpisode;
    }

    public int getNewEpisode() {
        return newEpisode;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
