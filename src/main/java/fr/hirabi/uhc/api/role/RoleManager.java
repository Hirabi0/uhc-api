package fr.hirabi.uhc.api.role;

import java.util.Collection;
import org.bukkit.entity.Player;

public interface RoleManager {

    // Attribution initiale des rôles aux joueurs
    void assignRoles(Collection<? extends Player> players);

    // Récupérer le rôle actuel d'un joueur (peut être null si aucun rôle)
    Role getRole(Player player);

    // Forcer le rôle d'un joueur (changement de camp, swap de rôle, etc.)
    void setRole(Player player, Role role);

    // Nettoyer toutes les associations joueur/rôle (fin de partie / reset)
    void clearRoles();

    // Appelé au lancement de la game : donner les items/effets de départ, etc.
    void onGameStart();

    // Appelé à chaque début de nouvel épisode (1, 2, 3, ...)
    void onEpisodeStart(int episodeNumber);

    // Appelé au début du jour
    void onDayStart();

    // Appelé au début de la nuit
    void onNightStart();

    // Appelé quand un joueur meurt (à toi de retrouver son rôle et d'appeler role.onDeath)
    void onPlayerDeath(Player dead, Player killer);

    // Appelé quand un joueur tue un autre joueur (pour déléguer aux rôles)
    void onPlayerKill(Player killer, Player victim);
}
