package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ShameMarkEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public ShameMarkEffect(JavaPlugin plugin) {
        super(plugin, "恥辱の刻印", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        String[] shameMessages = {
            "は運が悪いようです",
            "は今日ついていません",
            "は呪われているかもしれません",
            "は不運の星の下に生まれました",
            "はラッキーボックスに嫌われています"
        };
        
        String shameMessage = shameMessages[random.nextInt(shameMessages.length)];
        
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "★ " + ChatColor.RED + player.getName() + 
                               ChatColor.YELLOW + shameMessage + ChatColor.DARK_PURPLE + " ★");
        
        player.sendMessage(ChatColor.RED + "恥辱の刻印が刻まれました...皆があなたを見ています。");
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 5 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "恥辱の刻印が消えました。");
                    this.cancel();
                    return;
                }
                
                if (counter % 2 == 0) {
                    Bukkit.broadcastMessage(ChatColor.GRAY + player.getName() + " はまだ不運の中にいます...");
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 100L);
        
        return getDescription();
    }
}