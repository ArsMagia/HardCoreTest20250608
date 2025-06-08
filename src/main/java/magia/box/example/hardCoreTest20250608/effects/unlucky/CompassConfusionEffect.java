package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CompassConfusionEffect extends UnluckyEffectBase {

    public CompassConfusionEffect(JavaPlugin plugin) {
        super(plugin, "方向感覚喪失", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.YELLOW + "方向感覚を失いました...30秒間、位置情報が混乱します。");
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 30 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "方向感覚が戻りました。");
                    this.cancel();
                    return;
                }
                
                if (counter % 5 == 0) {
                    player.sendMessage(ChatColor.DARK_GRAY + "どちらが北だったでしょうか...？");
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}