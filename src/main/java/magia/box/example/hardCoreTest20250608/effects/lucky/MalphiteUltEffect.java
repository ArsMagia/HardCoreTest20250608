package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class MalphiteUltEffect extends LuckyEffectBase {

    public MalphiteUltEffect(JavaPlugin plugin) {
        super(plugin, "地殻突進", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location startLoc = player.getLocation().clone();
        Vector direction = startLoc.getDirection().normalize();
        Location targetLoc = startLoc.clone().add(direction.multiply(12));
        
        // 地面に着地するよう調整
        targetLoc.setY(targetLoc.getWorld().getHighestBlockYAt(targetLoc) + 1);
        
        player.sendMessage(ChatColor.GOLD + "マルファイトのUlt発動！前方12ブロック先に突進します！");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.8f);
        
        // 突進エフェクト
        new BukkitRunnable() {
            int ticks = 0;
            Location currentLoc = startLoc.clone();
            Vector velocity = targetLoc.toVector().subtract(startLoc.toVector()).normalize().multiply(0.8);
            
            @Override
            public void run() {
                if (ticks >= 15 || currentLoc.distance(targetLoc) < 1.0) {
                    // 着地時の爆発エフェクトとノックバック
                    player.teleport(targetLoc);
                    
                    // 爆発エフェクト
                    targetLoc.getWorld().spawnParticle(
                        Particle.EXPLOSION,
                        targetLoc,
                        10, 2, 1, 2, 0.1
                    );
                    
                    targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 1.0f);
                    
                    // 周囲の敵をノックバック（ダメージなし）
                    List<Entity> nearbyEntities = (List<Entity>) targetLoc.getWorld().getNearbyEntities(targetLoc, 4, 4, 4);
                    for (Entity entity : nearbyEntities) {
                        if (entity != player && entity instanceof Player) {
                            Vector knockback = entity.getLocation().toVector().subtract(targetLoc.toVector()).normalize().multiply(2);
                            knockback.setY(0.8);
                            entity.setVelocity(knockback);
                        }
                    }
                    
                    this.cancel();
                    return;
                }
                
                // 突進中のパーティクル
                currentLoc.getWorld().spawnParticle(
                    Particle.CLOUD,
                    currentLoc,
                    5, 0.2, 0.2, 0.2, 0.05
                );
                
                currentLoc.add(velocity);
                player.teleport(currentLoc);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        return getDescription();
    }
}