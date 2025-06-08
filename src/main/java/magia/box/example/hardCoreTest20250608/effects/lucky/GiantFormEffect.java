package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GiantFormEffect extends LuckyEffectBase {
    
    private static final Map<UUID, Double> originalHealth = new HashMap<>();

    public GiantFormEffect(JavaPlugin plugin) {
        super(plugin, "巨人化", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            originalHealth.put(player.getUniqueId(), healthAttr.getBaseValue());
            healthAttr.setBaseValue(Math.min(healthAttr.getBaseValue() * 2, 32));
            player.setHealth(healthAttr.getValue());
        }
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 1200, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 1200, 0));
        
        player.sendMessage(ChatColor.RED + "巨人化発動！1分間、体力2倍・力UP・速度DOWN！");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.7f);
        
        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            player.getLocation().add(0, 1, 0),
            5, 1, 1, 1, 0.1
        );
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 60 || !player.isOnline()) {
                    Double originalHp = originalHealth.remove(player.getUniqueId());
                    if (originalHp != null && player.isOnline()) {
                        AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
                        if (attr != null) {
                            attr.setBaseValue(originalHp);
                            player.setHealth(Math.min(player.getHealth(), originalHp));
                        }
                        player.sendMessage(ChatColor.GRAY + "巨人化が解除されました。");
                    }
                    this.cancel();
                    return;
                }
                
                if (counter % 10 == 0) {
                    player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(0, 3, 0),
                        10, 1, 0.5, 1, 0.05
                    );
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}