package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ChickenTransformEffect extends UnluckyEffectBase {

    public ChickenTransformEffect(JavaPlugin plugin) {
        super(plugin, "チキン化", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 1200, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1200, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1));
        
        player.sendMessage(ChatColor.YELLOW + "チキンに変身してしまいました！1分間、移動以外何もできません。");
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 1.0f, 1.0f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 60 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "人間の姿に戻りました。");
                    this.cancel();
                    return;
                }
                
                if (counter % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 0.5f, 1.0f);
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}