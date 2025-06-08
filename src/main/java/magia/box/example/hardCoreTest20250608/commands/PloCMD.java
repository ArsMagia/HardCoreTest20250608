package magia.box.example.hardCoreTest20250608.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PloCMD implements CommandExecutor {
    private final String COMMAND = "plo";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Location location = player.getLocation();

        if (command.getName().equalsIgnoreCase(COMMAND)) {
            int locX = location.getBlockX();
            int locY = location.getBlockY();
            int locZ = location.getBlockZ();

            String message;
            String name = "<" + player.getDisplayName() + "> ";
            String optional = "";
            String plo =
                    ChatColor.AQUA + "World: " + player.getWorld().getName() + " " +
                            ChatColor.BLUE + "X: " + locX + "," +
                            ChatColor.BLUE + "Y: " + locY + "," +
                            ChatColor.BLUE + "Z: " + locZ;

            for (String str : args) optional = optional.concat(str + " ");
            message = name + optional + plo;

            Bukkit.broadcastMessage(message);
        }
        return true;
    }
}
