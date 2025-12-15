package fr.hirabi.uhc.api.gamemode;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import fr.hirabi.uhc.api.event.UHCGameModeChangeEvent;
import org.bukkit.Bukkit;

public final class GameModeAPI {

    private static final Map<String, GameMode> MODES = new LinkedHashMap<String, GameMode>();
    private static GameMode activeMode;

    private GameModeAPI() {
    }

    public static void registerMode(GameMode mode) {
        if (mode == null || mode.getId() == null) {
            return;
        }
        MODES.put(mode.getId(), mode);
    }

    public static Collection<GameMode> getRegisteredModes() {
        return Collections.unmodifiableCollection(MODES.values());
    }

    public static GameMode getActiveMode() {
        return activeMode;
    }

    public static void setActiveMode(String id) {
        if (id == null) {
            return;
        }
        GameMode newMode = MODES.get(id);
        if (newMode == null) {
            return;
        }
        if (activeMode == newMode) {
            return;
        }

        GameMode old = activeMode;
        if (old != null) {
            old.onDisableMode();
        }
        activeMode = newMode;
        activeMode.onEnableMode();

        Bukkit.getPluginManager().callEvent(new UHCGameModeChangeEvent(old, newMode));
    }
}
