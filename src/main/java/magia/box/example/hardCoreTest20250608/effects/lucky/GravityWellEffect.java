package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GravityWellEffect extends LuckyEffectBase {

    public GravityWellEffect(JavaPlugin plugin) {
        super(plugin, "重力井戸", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        
        player.sendMessage(ChatColor.DARK_PURPLE + "重力井戸が発生！周囲のアイテムと経験値を引き寄せます。");
        player.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.5f);
        
        new BukkitRunnable() {
            int duration = 0;
            
            @Override
            public void run() {
                if (duration >= 200 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "重力井戸が消失しました。");
                    this.cancel();
                    return;
                }
                
                Location playerLoc = player.getLocation();
                
                for (Entity entity : playerLoc.getWorld().getNearbyEntities(playerLoc, 15, 15, 15)) {
                    if (entity instanceof Item || entity instanceof ExperienceOrb) {
                        Vector direction = playerLoc.toVector().subtract(entity.getLocation().toVector());
                        double distance = direction.length();
                        
                        if (distance > 1.0) {
                            direction.normalize();
                            double pullStrength = Math.min(0.3, 5.0 / distance);
                            entity.setVelocity(direction.multiply(pullStrength));
                        }
                    }
                }
                
                if (duration % 20 == 0) {
                    playerLoc.getWorld().spawnParticle(
                        Particle.WITCH,
                        playerLoc.add(0, 1, 0),
                        30, 3, 3, 3, 0.1
                    );
                    player.playSound(playerLoc, Sound.BLOCK_CONDUIT_AMBIENT, 0.5f, 1.2f);
                }
                
                duration += 5;
            }
        }.runTaskTimer(plugin, 0L, 5L);
        
        return getDescription();
    }
}