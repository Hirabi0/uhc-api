package main.java.fr.hirabi.uhc.api.role;

import fr.hirabi.uhc.api.event.UHCRoleAssignedEvent;
import fr.hirabi.uhc.api.role.Role;
import fr.hirabi.uhc.api.role.RoleManager;
import fr.hirabi.uhc.api.role.RoleAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Utilitaire pour changer le rôle d'un joueur en déclenchant UHCRoleAssignedEvent.
 * Les plugins peuvent utiliser cette classe au lieu d'appeler directement RoleManager.setRole.
 */
public final class RoleEventUtil {

    private RoleEventUtil() {
    }

    /**
     * Change le rôle d'un joueur via le RoleManager global et déclenche UHCRoleAssignedEvent.
     * Si aucun RoleManager n'est enregistré, ne fait rien.
     */
    public static void setRoleWithEvent(Player player, Role newRole) {
        if (player == null) {
            return;
        }

        RoleManager manager = RoleAPI.getRoleManager();
        if (manager == null) {
            return;
        }

        Role oldRole = manager.getRole(player);
        if (oldRole == newRole) {
            return;
        }

        manager.setRole(player, newRole);
        Bukkit.getPluginManager().callEvent(new UHCRoleAssignedEvent(player, oldRole, newRole));
    }
}
