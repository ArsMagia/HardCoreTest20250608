package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GoldenPathEffect extends LuckyEffectBase {

    public GoldenPathEffect(JavaPlugin plugin) {
        super(plugin, "黄金の道", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location start = player.getLocation();
        List<Block> goldenBlocks = new ArrayList<>();
        
        org.bukkit.util.Vector direction = start.getDirection().normalize();
        direction.setY(0);
        
        for (int i = 1; i <= 20; i++) {
            Location pathLoc = start.clone().add(direction.clone().multiply(i));
            pathLoc.setY(pathLoc.getWorld().getHighestBlockYAt(pathLoc));
            
            Block block = pathLoc.getBlock();
            if (block.getType() == Material.AIR || !block.getType().isSolid()) {
                block = pathLoc.clone().add(0, -1, 0).getBlock();
            }
            
            if (block.getType() != Material.GOLD_BLOCK) {
                block.setType(Material.GOLD_BLOCK);
                goldenBlocks.add(block);
                
                pathLoc.getWorld().spawnParticle(
                    Particle.FALLING_DUST,
                    pathLoc.add(0, 1, 0),
                    10, 0.5, 0.1, 0.5, 0,
                    Material.GOLD_BLOCK.createBlockData()
                );
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "黄金の道が現れました！1分間持続します。");
        player.playSound(start, Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.2f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : goldenBlocks) {
                    if (block.getType() == Material.GOLD_BLOCK) {
                        block.setType(Material.AIR);
                        block.getLocation().getWorld().spawnParticle(
                            Particle.CLOUD,
                            block.getLocation().add(0.5, 1, 0.5),
                            5, 0.3, 0.3, 0.3, 0.05
                        );
                    }
                }
                player.sendMessage(ChatColor.GRAY + "黄金の道が消えました。");
            }
        }.runTaskLater(plugin, 1200L);
        
        return getDescription();
    }
}