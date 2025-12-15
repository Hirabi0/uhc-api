# UHC-API – Documentation des fonctionnalités

Ce module fournit une API générique pour créer des modes de jeu UHC personnalisés, avec gestion des rôles, des modes de jeu, de la configuration et des GUIs.

---

## 1. Plugin principal et commande `/uhcapi`

### Classe principale
- `main.java.fr.hirabi.uhc.api.api` (extends `JavaPlugin`)
- Gère :
  - le cycle jour/nuit du monde principal,
  - les épisodes (durée configurable),
  - l’enregistrement des listeners (rôles, GUIs, SimpleGui),
  - l’enregistrement de la commande `/uhcapi`.

### Commande `/uhcapi`
- Déclarée dans `plugin.yml` (commande `uhcapi`, permission `uhcapi.config`).
- Classe : `main.java.fr.hirabi.uhc.api.command.ApiCommand`.
- Comportement :
  - `/uhcapi roles` : ouvre `RoleViewGUI` (vue des rôles des joueurs).
  - `/uhcapi` (sans argument) :
    - vérifie la permission `uhcapi.config`,
    - ouvre d’abord `GameModeSelectGUI` pour choisir le mode de jeu,
    - après sélection d’un mode, ouvre le menu principal **UHC-API Config** (`ApiConfigGUI`).

---

## 2. Système de rôles

### Interface `Role`
- Package : `fr.hirabi.uhc.api.role.Role`
- Principales méthodes :
  - `String getName()`
  - `String getDescription()`
  - `Camp getCamp()`
  - `List<ItemStack> getStartItems()`
  - `List<PotionEffect> getPermanentEffects()`
  - Hooks de partie : `onDayStart()`, `onNightStart()`, `onEpisodeStart(int)`, `onDeath(...)`, `onKill(...)`.

### Interface `RoleManager`
- Package : `fr.hirabi.uhc.api.role.RoleManager`
- Rôle : gérer l’attribution et le cycle de vie des rôles.
- Méthodes clés :
  - `void assignRoles(Collection<? extends Player> players)`
  - `Role getRole(Player player)` / `void setRole(Player player, Role role)`
  - `void clearRoles()`
  - `void onGameStart()`
  - `void onEpisodeStart(int episodeNumber)`
  - `void onDayStart()` / `void onNightStart()`
  - `void onPlayerDeath(Player dead, Player killer)`
  - `void onPlayerKill(Player killer, Player victim)`

### Classe `Camp`
- Package : `fr.hirabi.uhc.api.role.Camp`
- Représente un camp (faction) : id, nom affiché, couleur (`ChatColor`).

### `RoleAPI`
- Package : `fr.hirabi.uhc.api.role.RoleAPI`
- Point d’accès statique pour le `RoleManager` global :
  - `static void setRoleManager(RoleManager manager)`
  - `static RoleManager getRoleManager()`
- Le plugin de mode enregistre son `RoleManager` au démarrage via `RoleAPI.setRoleManager(...)`.

### GUI des rôles
- `RoleViewGUI` (package `main.java.fr.hirabi.uhc.api.gui`)
  - `open(Player viewer)` : ouvre un inventaire listant les joueurs en ligne avec leur rôle et camp.

---

## 3. Gestion des épisodes et jour/nuit

### Dans la classe `api`
- Champs :
  - `currentEpisode` (int, commence à 1)
  - `episodeTicks` (long)
  - `episodeLengthTicks` (long) – durée d’un épisode en ticks.
- Chargement de la durée d’épisode :
  - `api.reloadEpisodeLength()` lit `episodes.length-seconds` dans `config.yml`.
- Tâche répétitive (toutes les secondes) :
  - détecte le passage jour → nuit et nuit → jour,
  - appelle `RoleManager.onNightStart()` / `onDayStart()` si un `RoleManager` est enregistré,
  - incrémente `episodeTicks` et déclenche un nouvel épisode lorsque `episodeTicks >= episodeLengthTicks` :
    - `currentEpisode++`
    - `RoleManager.onEpisodeStart(currentEpisode)`
    - envoie l’événement `UHCEpisodeChangeEvent`.

### Forcer l’épisode suivant
- Méthode `api.forceNextEpisode()` :
  - incrémente `currentEpisode`,
  - appelle `RoleManager.onEpisodeStart(currentEpisode)` si présent,
  - déclenche un `UHCEpisodeChangeEvent`.
- Accessible via le bouton "Forcer l'épisode suivant" dans le menu principal.

---

## 4. Système de modes de jeu

### Interface `GameMode`
- Package : `fr.hirabi.uhc.api.gamemode.GameMode`
- Méthodes :
  - `String getId()` – identifiant unique du mode (ex : "uhc").
  - `String getName()` – nom affiché.
  - `String getDescription()` – description courte.
  - `ItemStack getIcon()` – icône du mode (utilisée dans les GUIs).
  - `void onEnableMode()` / `void onDisableMode()` – hooks lors du changement de mode actif.
  - `void onGameStart()` / `void onGameEnd()` – hooks pour le début/fin de partie.

### `GameModeAPI`
- Package : `fr.hirabi.uhc.api.gamemode.GameModeAPI`
- Gestion statique des modes :
  - `static void registerMode(GameMode mode)` : enregistre un mode.
  - `static Collection<GameMode> getRegisteredModes()` : liste des modes connus.
  - `static GameMode getActiveMode()` : renvoie le mode actif.
  - `static void setActiveMode(String id)` : change le mode actif :
    - appelle `onDisableMode()` sur l’ancien mode (si différent),
    - met à jour `activeMode`,
    - appelle `onEnableMode()` sur le nouveau mode,
    - déclenche l’événement `UHCGameModeChangeEvent`.

### Mode par défaut `DefaultUhcMode`
- Package : `fr.hirabi.uhc.api.gamemode.DefaultUhcMode`
- Caractéristiques :
  - `getId()` → "uhc"
  - `getName()` → "UHC"
  - `getDescription()` → "Mode UHC classique sans règles spéciales."
  - `getIcon()` → `GOLDEN_APPLE`
  - Hooks (`onEnableMode`, `onDisableMode`, `onGameStart`, `onGameEnd`) vides par défaut.

### Enregistrement au démarrage
- Dans `api.onEnable()` :
  - `GameModeAPI.registerMode(new DefaultUhcMode());`
  - `GameModeAPI.setActiveMode("uhc");`

### GUI de sélection de mode
- Classe : `main.java.fr.hirabi.uhc.api.gui.GameModeSelectGUI`
- Méthodes :
  - `static String getTitle()` : titre de l’inventaire.
  - `static void open(Player viewer)` : ouvre un inventaire listant tous les `GameMode` enregistrés.
- Fonctionnement :
  - taille d’inventaire calculée (1 à 6 lignes),
  - pour chaque `GameMode` :
    - icône = `getIcon()` clonée,
    - nom = `getName()`,
    - lore = description + "(Actif)" si c’est le mode actif,
    - première ligne de lore contient `ID: <modeId>` pour identification.
- `ApiConfigListener` intercepte les clics sur ce GUI, récupère l’ID dans la lore et :
  - appelle `GameModeAPI.setActiveMode(id)`,
  - ouvre `ApiConfigGUI.openMainMenu(player, plugin)`.

---

## 5. Configuration par mode (`modes.yml`)

### `ModeConfigAPI`
- Package : `fr.hirabi.uhc.api.gamemode.ModeConfigAPI`
- Gère un fichier `modes.yml` dans le data folder du plugin `uhc-api`.
- Méthodes :
  - `static ConfigurationSection getModeConfig(String modeId)` :
    - charge `modes.yml` au besoin,
    - s’assure que la section racine `modes` existe,
    - s’assure que `modes.<modeId>` existe (créée si besoin),
    - renvoie la `ConfigurationSection` correspondante.
  - `static void save()` : sauvegarde `modes.yml` sur disque.
- Appelée dans `api.onDisable()` pour persister les changements :
  - `ModeConfigAPI.save();`

### Exemple d’utilisation dans un `GameMode`

Le mode par défaut `DefaultUhcMode` utilise `ModeConfigAPI` dans `onEnableMode()` pour synchroniser la durée des épisodes :

```java
public void onEnableMode() {
    // Charger / créer la config du mode dans modes.yml
    ConfigurationSection cfg = ModeConfigAPI.getModeConfig(getId());

    // Valeur par défaut : 1200s ou valeur actuelle de la config principale si définie
    api apiPlugin = JavaPlugin.getPlugin(api.class);
    int defaultSeconds = apiPlugin.getConfig().getInt("episodes.length-seconds", 1200);

    int lengthSeconds = cfg.getInt("episodes.length-seconds", defaultSeconds);
    if (!cfg.isSet("episodes.length-seconds")) {
        cfg.set("episodes.length-seconds", lengthSeconds);
    }

    // Appliquer la valeur au plugin principal et recharger la durée des épisodes
    apiPlugin.getConfig().set("episodes.length-seconds", lengthSeconds);
    apiPlugin.saveConfig();
    apiPlugin.reloadEpisodeLength();
}
```

Structure typique de `modes.yml` :

```yaml
modes:
  uhc:
    episodes:
      length-seconds: 1200
  mon_mode:
    some-option: true
```

---

## 6. Events custom UHC

### `UHCEpisodeChangeEvent`
- Package : `main.java.fr.hirabi.uhc.api.event.UHCEpisodeChangeEvent`
- Champs :
  - `int oldEpisode`
  - `int newEpisode`
- Déclenché :
  - à chaque changement d’épisode dans la tâche principale,
  - dans `api.forceNextEpisode()`.

### `UHCGameModeChangeEvent`
- Package : `main.java.fr.hirabi.uhc.api.event.UHCGameModeChangeEvent`
- Champs :
  - `GameMode oldMode`
  - `GameMode newMode`
- Déclenché dans `GameModeAPI.setActiveMode(...)` lorsque le mode actif change réellement.

### Esquisse `UHCRoleAssignedEvent`
- Package : `main.java.fr.hirabi.uhc.api.event.UHCRoleAssignedEvent`
- Champs :
  - `Player player`
  - `Role oldRole`
  - `Role newRole`
- Prévu pour signaler l’attribution/changement de rôle ; non encore utilisé tant que les `RoleManager` externes ne sont pas adaptés.

### Exemple de listener

```java
@EventHandler
public void onEpisodeChange(UHCEpisodeChangeEvent event) {
    int oldEp = event.getOldEpisode();
    int newEp = event.getNewEpisode();
    // Logique custom...
}

@EventHandler
public void onGameModeChange(UHCGameModeChangeEvent event) {
    GameMode oldMode = event.getOldMode();
    GameMode newMode = event.getNewMode();
    // Logique custom...
}
```

---

## 7. Builder de GUI générique : `SimpleGui`

### Classe `SimpleGui`
- Package : `main.java.fr.hirabi.uhc.api.gui.SimpleGui`
- Constructeur :
  - `SimpleGui(int rows, String title)` (1 ≤ rows ≤ 6).
- Méthodes principales :
  - `void setItem(int slot, ItemStack item, SimpleGui.GuiClickHandler handler)`
  - `void open(Player player)`
- Comportement interne :
  - crée un `Inventory` Bukkit de taille `rows * 9` et le mappe vers l’instance de `SimpleGui` via une map statique :
    - `Map<Inventory, SimpleGui> GUI_BY_INVENTORY`
  - stocke pour chaque slot un `GuiClickHandler` dans une map `slot -> handler`.

### Interface interne `GuiClickHandler`

```java
public interface GuiClickHandler {
    void onClick(Player player, ClickType clickType);
}
```

### Listener `SimpleGuiListener`
- Package : `main.java.fr.hirabi.uhc.api.gui.SimpleGuiListener`
- Écoute `InventoryClickEvent` :
  - vérifie si l’inventaire cliqué appartient à un `SimpleGui` via `SimpleGui.getByInventory(inv)`,
  - annule l’event,
  - récupère le handler du slot et appelle `onClick(player, clickType)`.
- Enregistré dans `api.onEnable()` :
  - `Bukkit.getPluginManager().registerEvents(new SimpleGuiListener(), this);`

### Exemple d’utilisation

```java
SimpleGui gui = new SimpleGui(1, "Config MonMode");
ItemStack item = new ItemStack(Material.STONE);

gui.setItem(0, item, (player, clickType) -> {
    player.sendMessage("Vous avez cliqué sur la pierre !");
});

gui.open(player);
```

---

## 8. Extension du menu principal via `ApiConfigEntry`

### Interface `ApiConfigEntry`
- Package : `fr.hirabi.uhc.api.config.ApiConfigEntry`
- Méthodes :
  - `ItemStack getIcon()` – icône du bouton dans le menu principal.
  - `String getId()` – identifiant unique de l’entrée.
  - `void onClick(Player player)` – appelé quand le joueur clique sur l’icône.

### Registre `ApiConfigRegistry`
- Package : `fr.hirabi.uhc.api.config.ApiConfigRegistry`
- Méthodes :
  - `static void registerEntry(ApiConfigEntry entry)`
  - `static Collection<ApiConfigEntry> getEntries()`

### Intégration dans `ApiConfigGUI`
- Après placement des boutons de base (épisodes, forcer épisode), le menu principal "UHC-API Config" remplit certains slots libres (0,1,2,7,8) avec les entrées d’`ApiConfigRegistry` :
  - utilise `entry.getIcon()` comme base,
  - ajoute dans la **lore** une ligne `ID: <entryId>` pour l’identifier.

### Intégration dans `ApiConfigListener`
- Lors d’un clic dans "UHC-API Config" :
  - si l’item n’est ni "Durée des épisodes" ni "Forcer l'épisode suivant",
  - lit la lore, cherche une ligne commençant par `ID: `,
  - retrouve l’`ApiConfigEntry` ayant cet `id` dans `ApiConfigRegistry`,
  - appelle `entry.onClick(player)`.

### Exemple pour un plugin externe

```java
public class MonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        ApiConfigRegistry.registerEntry(new ApiConfigEntry() {
            @Override
            public ItemStack getIcon() {
                return new ItemStack(Material.BOOK);
            }

            @Override
            public String getId() {
                return "monplugin-config";
            }

            @Override
            public void onClick(Player player) {
                // Ouvre un GUI de config spécifique au mode MonPlugin
                SimpleGui gui = new SimpleGui(1, "Config MonPlugin");
                ItemStack rs = new ItemStack(Material.REDSTONE);
                gui.setItem(0, rs, (p, click) -> p.sendMessage("Réglage MonPlugin"));
                gui.open(player);
            }
        });
    }
}
```

---

## 9. Résumé pour les développeurs de modes

Pour créer un mode de jeu basé sur **UHC-API** :

1. **Déclarer un `GameMode`** et l’enregistrer via `GameModeAPI.registerMode(...)`.
2. **Fournir un `RoleManager`** et le déclarer via `RoleAPI.setRoleManager(...)`.
3. **Utiliser `ModeConfigAPI`** pour stocker la configuration spécifique au mode dans `modes.yml`.
4. **Écouter les events** `UHCEpisodeChangeEvent` et `UHCGameModeChangeEvent` si besoin.
5. **Utiliser `SimpleGui`** pour créer des menus de configuration interactifs.
6. **Enregistrer des `ApiConfigEntry`** dans `ApiConfigRegistry` pour ajouter des boutons de config dans le menu principal `/uhcapi`.

Ainsi, `uhc-api` sert de fondation commune pour plusieurs plugins/modes UHC tout en offrant une UX cohérente pour la configuration côté staff.

---

## 10. Créer un nouveau mode pas à pas

Cette section donne un exemple minimal pour créer un nouveau mode basé sur `uhc-api`.

### 10.1. Déclarer le `GameMode`

```java
public class MonMode implements GameMode {

    @Override
    public String getId() {
        return "mon_mode";
    }

    @Override
    public String getName() {
        return "Mon Mode";
    }

    @Override
    public String getDescription() {
        return "Un exemple de mode basé sur UHC-API.";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.DIAMOND_SWORD);
    }

    @Override
    public void onEnableMode() {
        // Charger la config de ce mode dans modes.yml
        ConfigurationSection cfg = ModeConfigAPI.getModeConfig(getId());

        api apiPlugin = JavaPlugin.getPlugin(api.class);
        int defaultSeconds = apiPlugin.getConfig().getInt("episodes.length-seconds", 1200);

        int lengthSeconds = cfg.getInt("episodes.length-seconds", defaultSeconds);
        if (!cfg.isSet("episodes.length-seconds")) {
            cfg.set("episodes.length-seconds", lengthSeconds);
        }

        // Appliquer au plugin principal
        apiPlugin.getConfig().set("episodes.length-seconds", lengthSeconds);
        apiPlugin.saveConfig();
        apiPlugin.reloadEpisodeLength();
    }

    @Override
    public void onDisableMode() { }

    @Override
    public void onGameStart() { }

    @Override
    public void onGameEnd() { }
}
```

### 10.2. Enregistrer le `GameMode`

Dans le `onEnable()` de ton plugin de mode (qui dépend de `UHC-API`) :

```java
@Override
public void onEnable() {
    GameModeAPI.registerMode(new MonMode());
}
```

Le mode apparaîtra automatiquement dans le `GameModeSelectGUI` (commande `/uhcapi`).

### 10.3. Fournir un `RoleManager`

Exemple simplifié :

```java
public class MonRoleManager implements RoleManager {

    private final Map<UUID, Role> roles = new HashMap<>();

    @Override
    public void assignRoles(Collection<? extends Player> players) {
        for (Player p : players) {
            // Assigner un rôle par défaut (à adapter)
            roles.put(p.getUniqueId(), new MonRole());
        }
    }

    @Override
    public Role getRole(Player player) {
        return roles.get(player.getUniqueId());
    }

    @Override
    public void setRole(Player player, Role role) {
        roles.put(player.getUniqueId(), role);
    }

    @Override
    public void clearRoles() {
        roles.clear();
    }

    // Implémenter les autres méthodes onGameStart, onEpisodeStart, etc.
}
```

Enregistrement dans ton plugin :

```java
@Override
public void onEnable() {
    RoleAPI.setRoleManager(new MonRoleManager());
}
```

### 10.4. Bouton de config dans le menu `/uhcapi`

Dans le `onEnable()` de ton plugin, en plus de l’enregistrement du mode/RoleManager :

```java
ApiConfigRegistry.registerEntry(new ApiConfigEntry() {
    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.BOOK);
    }

    @Override
    public String getId() {
        return "mon_mode-config";
    }

    @Override
    public void onClick(Player player) {
        // Ouvrir un SimpleGui de configuration propre à ton mode
        SimpleGui gui = new SimpleGui(1, "Config Mon Mode");
        ItemStack item = new ItemStack(Material.REDSTONE);
        gui.setItem(0, item, (p, click) -> p.sendMessage("Réglage d'un paramètre de Mon Mode"));
        gui.open(player);
    }
});
```

### 10.5. Changement de rôles avec event

Pour bénéficier de `UHCRoleAssignedEvent`, utilise `RoleEventUtil` :

```java
public void changerRole(Player player, Role nouveauRole) {
    RoleEventUtil.setRoleWithEvent(player, nouveauRole);
}
```

Tu peux ensuite écouter `UHCRoleAssignedEvent` pour réagir aux changements de rôles.
