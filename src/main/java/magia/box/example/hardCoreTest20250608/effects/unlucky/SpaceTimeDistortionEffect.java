package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class SpaceTimeDistortionEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public SpaceTimeDistortionEffect(JavaPlugin plugin) {
        super(plugin, "時空の歪み", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "時空の歪みに巻き込まれました！15秒間ランダムテレポートします。");
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.5f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 10 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "時空の歪みが収束しました。");
                    this.cancel();
                    return;
                }
                
                Location safeLoc = findNearbyLocation(player.getLocation());
                if (safeLoc != null) {
                    player.getLocation().getWorld().spawnParticle(
                        Particle.PORTAL,
                        player.getLocation().add(0, 1, 0),
                        20, 0.5, 1, 0.5, 0.1
                    );
                    
                    player.teleport(safeLoc);
                    player.playSound(safeLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 30L);
        
        return getDescription();
    }
    
    private Location findNearbyLocation(Location center) {
        for (int attempts = 0; attempts < 20; attempts++) {
            int x = center.getBlockX() + random.nextInt(20) - 10;
            int z = center.getBlockZ() + random.nextInt(20) - 10;
            int y = center.getWorld().getHighestBlockYAt(x, z);
            
            Location candidate = new Location(center.getWorld(), x + 0.5, y + 1, z + 0.5);
            
            if (isSafeLocation(candidate)) {
                return candidate;
            }
        }
        return center;
    }
    
    private boolean isSafeLocation(Location loc) {
        Material groundBlock = loc.clone().add(0, -1, 0).getBlock().getType();
        Material feetBlock = loc.getBlock().getType();
        Material headBlock = loc.clone().add(0, 1, 0).getBlock().getType();
        
        return groundBlock.isSolid() &&
               feetBlock == Material.AIR &&
               headBlock == Material.AIR;
    }
}