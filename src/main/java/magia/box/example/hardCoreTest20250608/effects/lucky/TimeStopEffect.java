package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TimeStopEffect extends LuckyEffectBase {

    public TimeStopEffect(JavaPlugin plugin) {
        super(plugin, "時間停止", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        List<LivingEntity> frozenEntities = new ArrayList<>();
        
        for (Entity entity : center.getWorld().getNearbyEntities(center, 10, 10, 10)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 255, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 255, false, false));
                living.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 200, -10, false, false));
                frozenEntities.add(living);
            }
        }
        
        player.sendMessage(ChatColor.LIGHT_PURPLE + "時間が停止しました！10秒間、周囲の生物が動けません。");
        player.playSound(center, Sound.BLOCK_END_PORTAL_SPAWN, 2.0f, 0.5f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 10) {
                    player.sendMessage(ChatColor.GRAY + "時間の流れが元に戻りました。");
                    player.playSound(center, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                    this.cancel();
                    return;
                }
                
                center.getWorld().spawnParticle(
                    Particle.WITCH,
                    center.clone().add(0, 2, 0),
                    50, 8, 3, 8, 0.1
                );
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}