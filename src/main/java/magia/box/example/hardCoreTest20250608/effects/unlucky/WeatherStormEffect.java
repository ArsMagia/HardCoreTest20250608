package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class WeatherStormEffect extends UnluckyEffectBase {

    public WeatherStormEffect(JavaPlugin plugin) {
        super(plugin, "天候暴走", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_BLUE + "あなたの頭上だけに雷雨が発生しました！30秒間続きます。");
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 30 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "個人的な雷雨が止みました。");
                    this.cancel();
                    return;
                }
                
                if (counter % 3 == 0) {
                    Location strikeLoc = player.getLocation().add(
                        (Math.random() - 0.5) * 4,
                        0,
                        (Math.random() - 0.5) * 4
                    );
                    player.getWorld().strikeLightningEffect(strikeLoc);
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}