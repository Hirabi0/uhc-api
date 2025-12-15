package fr.hirabi.uhc.api.role;

/**
 * Point d'accès statique pour enregistrer et récupérer le RoleManager utilisé
 * par les plugins de modes.
 */
public final class RoleAPI {

    private static RoleManager roleManager;

    private RoleAPI() {
    }

    /**
     * Enregistre le RoleManager global.
     */
    public static void setRoleManager(RoleManager manager) {
        roleManager = manager;
    }

    /**
     * Récupère le RoleManager global (peut être null si non défini).
     */
    public static RoleManager getRoleManager() {
        return roleManager;
    }
}
