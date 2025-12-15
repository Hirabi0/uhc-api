package fr.hirabi.uhc.api.event;

import fr.hirabi.uhc.api.role.Role;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Esquisse d'event pour signaler l'attribution d'un rôle à un joueur.
 * (Pas encore utilisé tant que les RoleManager ne sont pas adaptés.)
 */
public class UHCRoleAssignedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final Role oldRole;
    private final Role newRole;

    public UHCRoleAssignedEvent(Player player, Role oldRole, Role newRole) {
        this.player = player;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }

    public Player getPlayer() {
        return player;
    }

    public Role getOldRole() {
        return oldRole;
    }

    public Role getNewRole() {
        return newRole;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
