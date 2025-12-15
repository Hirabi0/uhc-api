package fr.hirabi.uhc.api.command;

import main.java.fr.hirabi.uhc.api.gui.RoleViewGUI;
import main.java.fr.hirabi.uhc.api.gui.GameModeSelectGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ApiCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande doit être exécutée en jeu.");
            return true;
        }

        Player player = (Player) sender;
        if (args.length > 0 && args[0].equalsIgnoreCase("roles")) {
            if (!player.hasPermission("uhcapi.config")) {
                player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
                return true;
            }
            RoleViewGUI.open(player);
            return true;
        }

        if (!player.hasPermission("uhcapi.config")) {
            player.sendMessage("§cVous n'avez pas la permission d'ouvrir ce menu.");
            return true;
        }

        GameModeSelectGUI.open(player);
        return true;
    }
}
