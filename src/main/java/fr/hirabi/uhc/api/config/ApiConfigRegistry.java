package fr.hirabi.uhc.api.config;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiConfigRegistry {

    private static final Map<String, ApiConfigEntry> ENTRIES = new LinkedHashMap<String, ApiConfigEntry>();

    private ApiConfigRegistry() {
    }

    public static void registerEntry(ApiConfigEntry entry) {
        if (entry == null || entry.getId() == null) {
            return;
        }
        ENTRIES.put(entry.getId(), entry);
    }

    public static Collection<ApiConfigEntry> getEntries() {
        return Collections.unmodifiableCollection(ENTRIES.values());
    }

    /*
    Exemple d'enregistrement depuis un plugin externe :

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
                    // Ouvrir ici un GUI de configuration propre au mode / plugin
                }
            });
        }
    }
    */
}
