package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class SafeTeleportEffect extends LuckyEffectBase {
    
    private final Random random = new Random();

    public SafeTeleportEffect(JavaPlugin plugin) {
        super(plugin, "安全地帯テレポート", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        Location currentLoc = player.getLocation();
        Location safeLoc = findSafeLocation(currentLoc);
        
        if (safeLoc != null) {
            currentLoc.getWorld().spawnParticle(
                Particle.PORTAL,
                currentLoc.add(0, 1, 0),
                100, 0.5, 1, 0.5, 0.3
            );
            
            player.teleport(safeLoc);
            player.sendMessage(ChatColor.AQUA + "安全な場所にテレポートしました！");
            player.playSound(safeLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
            
            safeLoc.getWorld().spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                safeLoc.add(0, 1, 0),
                50, 1, 1, 1, 0.1
            );
            
            return getDescription();
        } else {
            player.sendMessage(ChatColor.YELLOW + "安全な場所が見つからませんでした。代わりに体力を回復します。");
            player.setHealth(Math.min(player.getHealth() + 10, player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
            return "体力回復";
        }
    }
    
    private Location findSafeLocation(Location center) {
        for (int attempts = 0; attempts < 50; attempts++) {
            int x = center.getBlockX() + random.nextInt(200) - 100;
            int z = center.getBlockZ() + random.nextInt(200) - 100;
            int y = center.getWorld().getHighestBlockYAt(x, z);
            
            Location candidate = new Location(center.getWorld(), x + 0.5, y + 1, z + 0.5);
            
            if (isSafeLocation(candidate)) {
                return candidate;
            }
        }
        return null;
    }
    
    private boolean isSafeLocation(Location loc) {
        Material groundBlock = loc.clone().add(0, -1, 0).getBlock().getType();
        Material feetBlock = loc.getBlock().getType();
        Material headBlock = loc.clone().add(0, 1, 0).getBlock().getType();
        
        return groundBlock.isSolid() &&
               feetBlock == Material.AIR &&
               headBlock == Material.AIR &&
               groundBlock != Material.LAVA &&
               groundBlock != Material.MAGMA_BLOCK;
    }
}