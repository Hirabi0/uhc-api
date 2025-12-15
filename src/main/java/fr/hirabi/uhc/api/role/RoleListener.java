package fr.hirabi.uhc.api.role;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Listener générique qui relaie les morts de joueurs vers le RoleManager.
 */
public class RoleListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        RoleManager manager = RoleAPI.getRoleManager();
        if (manager == null) {
            return;
        }

        Player dead = event.getEntity();
        Player killer = dead.getKiller(); // peut être null

        manager.onPlayerDeath(dead, killer);
        if (killer != null) {
            manager.onPlayerKill(killer, dead);
        }
    }
}
