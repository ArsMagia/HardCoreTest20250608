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
        super(plugin, "モブ加速", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        Bukkit.broadcastMessage(ChatColor.RED + "サーバー全体のモブが狂暴化しました！30秒間、移動速度が2倍になります！");
        player.playSound(player.getLocation(), Sound.ENTITY_WOLF_GROWL, 1.0f, 0.8f);

        // サーバー全体のモブの速度を2倍に
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Mob) {
                    Mob mob = (Mob) entity;
                    AttributeInstance speedAttr = mob.getAttribute(Attribute.MOVEMENT_SPEED);
                    
                    if (speedAttr != null) {
                        double originalSpeed = speedAttr.getBaseValue();
                        originalSpeeds.put(mob.getUniqueId(), originalSpeed);
                        speedAttr.setBaseValue(originalSpeed * 2.0);
                    }
                }
            }
        }

        // 30秒後に速度を元に戻す
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Mob) {
                            Mob mob = (Mob) entity;
                            Double originalSpeed = originalSpeeds.remove(mob.getUniqueId());
                            
                            if (originalSpeed != null) {
                                AttributeInstance speedAttr = mob.getAttribute(Attribute.MOVEMENT_SPEED);
                                if (speedAttr != null) {
                                    speedAttr.setBaseValue(originalSpeed);
                                }
                            }
                        }
                    }
                }
                
                Bukkit.broadcastMessage(ChatColor.GRAY + "サーバー全体のモブが正常な速度に戻りました。");
            }
        }.runTaskLater(plugin, 600L); // 30秒後

        return getDescription();
    }
}