package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MobSpeedBoostEffect extends UnluckyEffectBase {

    private static final Map<UUID, Double> originalSpeeds = new HashMap<>();

    public MobSpeedBoostEffect(JavaPlugin plugin) {
        super(plugin, "モブ加速", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.RED + "周囲のモブが狂暴化しました！1分間、移動速度が2倍になります！");
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1.0f, 0.8f);

        // 周囲のモブを取得して速度を2倍に
        List<Entity> nearbyEntities = (List<Entity>) player.getWorld().getNearbyEntities(player.getLocation(), 50, 50, 50);
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                AttributeInstance speedAttr = mob.getAttribute(Attribute.MOVEMENT_SPEED);
                
                if (speedAttr != null) {
                    double originalSpeed = speedAttr.getBaseValue();
                    originalSpeeds.put(mob.getUniqueId(), originalSpeed);
                    speedAttr.setBaseValue(originalSpeed * 2.0);
                    
                    // 視覚効果
                    mob.getWorld().spawnParticle(
                        Particle.ANGRY_VILLAGER,
                        mob.getLocation().add(0, 1, 0),
                        5, 0.5, 0.5, 0.5, 0.1
                    );
                }
            }
        }

        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            player.getLocation().add(0, 1, 0),
            10, 3, 1, 3, 0.1
        );

        // 1分後に速度を元に戻す
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Entity> entities = (List<Entity>) player.getWorld().getNearbyEntities(player.getLocation(), 100, 100, 100);
                
                for (Entity entity : entities) {
                    if (entity instanceof Mob) {
                        Mob mob = (Mob) entity;
                        Double originalSpeed = originalSpeeds.remove(mob.getUniqueId());
                        
                        if (originalSpeed != null) {
                            AttributeInstance speedAttr = mob.getAttribute(Attribute.MOVEMENT_SPEED);
                            if (speedAttr != null) {
                                speedAttr.setBaseValue(originalSpeed);
                                
                                // 復旧効果
                                mob.getWorld().spawnParticle(
                                    Particle.HAPPY_VILLAGER,
                                    mob.getLocation().add(0, 1, 0),
                                    3, 0.3, 0.3, 0.3, 0.1
                                );
                            }
                        }
                    }
                }
                
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "周囲のモブが正常な速度に戻りました。");
                }
            }
        }.runTaskLater(plugin, 1200L); // 1分後

        return getDescription();
    }
}