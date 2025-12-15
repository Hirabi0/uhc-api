package fr.hirabi.uhc.api.role;

import org.bukkit.ChatColor;

/**
 * Représente un camp de jeu (faction) configurable.
 * Exemple : "Village", "Loups", "Neutre", etc.
 */
public class Camp {

    private final String id;          // identifiant technique unique (ex: "village", "loups")
    private final String displayName; // nom affiché au joueur
    private final ChatColor color;    // couleur principale du camp

    public Camp(String id, String displayName, ChatColor color) {
        this.id = id;
        this.displayName = displayName;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }
}

