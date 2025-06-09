package magia.box.example.hardCoreTest20250608.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
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

        if (command.getName().equalsIgnoreCase(COMMAND)) {
            // テレポート機能の処理
            if (args.length >= 4 && "tp".equalsIgnoreCase(args[0])) {
                return handleTeleportCommand(player, args);
            }
            
            // 既存の座標表示機能
            Location location = player.getLocation();
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
    
    /**
     * テレポートコマンドを処理
     * @param player 実行プレイヤー
     * @param args コマンド引数 [tp, x, y, z, world]
     * @return 成功したかどうか
     */
    private boolean handleTeleportCommand(Player player, String[] args) {
        try {
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            
            World targetWorld = player.getWorld(); // デフォルトは現在のワールド
            if (args.length >= 5) {
                targetWorld = Bukkit.getWorld(args[4]);
                if (targetWorld == null) {
                    player.sendMessage(ChatColor.RED + "指定されたワールドが見つかりません: " + args[4]);
                    return true;
                }
            }
            
            Location teleportLocation = new Location(targetWorld, x, y, z);
            player.teleport(teleportLocation);
            player.sendMessage(ChatColor.GREEN + "死亡地点にテレポートしました！");
            return true;
            
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "無効な座標です。");
            return true;
        }
    }
}
