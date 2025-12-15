package fr.hirabi.uhc.api.role;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public interface Role {

    // Nom du rôle (ex: "Loup-Garou", "Voyante"...)
    String getName();

    // Description courte pour les menus / livres
    String getDescription();

    // Camp principal du rôle
    Camp getCamp();

    // Objets donnés au joueur au début de la partie
    List<ItemStack> getStartItems();

    // Effets permanents appliqués au joueur pendant toute la game
    List<PotionEffect> getPermanentEffects();

    // Appelé au début du jour (par ton plugin de mode)
    void onDayStart();

    // Appelé au début de la nuit (par ton plugin de mode)
    void onNightStart();

    // Appelé à chaque début de nouvel épisode (1, 2, 3, ...)
    void onEpisodeStart(int episodeNumber);

    // Appelé quand le joueur qui possède ce rôle meurt
    // killer peut être null (mort environnementale)
    void onDeath(Player dead, Player killer);

    // Appelé quand le joueur qui possède ce rôle tue un autre joueur
    void onKill(Player killer, Player victim);
}
