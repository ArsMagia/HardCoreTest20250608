package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeAccelerationEffect extends UnluckyEffectBase {

    public TimeAccelerationEffect(JavaPlugin plugin) {
        super(plugin, "時間加速", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.YELLOW + "時間が加速しました！15秒間、空腹度と体力の減少が速くなります。");
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 15 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "時間の流れが正常に戻りました。");
                    this.cancel();
                    return;
                }
                
                // 空腹度を追加で減少
                int currentFood = player.getFoodLevel();
                player.setFoodLevel(Math.max(0, currentFood - 1));
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}