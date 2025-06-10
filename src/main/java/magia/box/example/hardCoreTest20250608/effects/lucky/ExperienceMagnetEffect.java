package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ExperienceMagnetEffect extends LuckyEffectBase {

    public ExperienceMagnetEffect(JavaPlugin plugin) {
        super(plugin, "経験値磁石", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.GREEN + "経験値磁石が発動！5分間、周囲の経験値を自動回収します。");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.3f);
        
        new BukkitRunnable() {
            int duration = 0;
            
            @Override
            public void run() {
                if (duration >= 300 || !player.isOnline()) { // 5分間
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GRAY + "経験値磁石の効果が終了しました。");
                    }
                    this.cancel();
                    return;
                }
                
                Location playerLoc = player.getLocation();
                
                // 周囲50ブロックの経験値オーブを引き寄せ
                for (Entity entity : playerLoc.getWorld().getNearbyEntities(playerLoc, 50, 50, 50)) {
                    if (entity instanceof ExperienceOrb) {
                        Vector direction = playerLoc.toVector().subtract(entity.getLocation().toVector());
                        double distance = direction.length();
                        
                        if (distance > 2.0) {
                            direction.normalize();
                            double pullStrength = Math.min(0.4, 8.0 / distance);
                            entity.setVelocity(direction.multiply(pullStrength));
                        }
                    }
                }
                
                // パーティクル効果（20秒ごと）
                if (duration % 20 == 0) {
                    playerLoc.getWorld().spawnParticle(
                        Particle.HAPPY_VILLAGER,
                        playerLoc.add(0, 1, 0),
                        15, 2, 2, 2, 0.05
                    );
                }
                
                duration++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}