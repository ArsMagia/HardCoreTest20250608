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

public class CloudBridgeEffect extends LuckyEffectBase {

    public CloudBridgeEffect(JavaPlugin plugin) {
        super(plugin, "雲の橋", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location start = player.getLocation().add(0, 10, 0);
        List<Block> cloudBlocks = new ArrayList<>();
        
        org.bukkit.util.Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(0);
        
        for (int i = 0; i <= 30; i++) {
            Location bridgeLoc = start.clone().add(direction.clone().multiply(i));
            
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = bridgeLoc.clone().add(x, 0, z).getBlock();
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.WHITE_WOOL);
                        cloudBlocks.add(block);
                        
                        block.getLocation().getWorld().spawnParticle(
                            Particle.CLOUD,
                            block.getLocation().add(0.5, 1, 0.5),
                            5, 0.2, 0.2, 0.2, 0.02
                        );
                    }
                }
            }
        }
        
        player.sendMessage(ChatColor.WHITE + "雲の橋が空に現れました！2分間持続します。");
        player.playSound(start, Sound.ENTITY_GHAST_AMBIENT, 1.0f, 1.5f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : cloudBlocks) {
                    if (block.getType() == Material.WHITE_WOOL) {
                        block.setType(Material.AIR);
                        block.getLocation().getWorld().spawnParticle(
                            Particle.CLOUD,
                            block.getLocation().add(0.5, 0.5, 0.5),
                            8, 0.3, 0.3, 0.3, 0.05
                        );
                    }
                }
                player.sendMessage(ChatColor.GRAY + "雲の橋が消散しました。");
            }
        }.runTaskLater(plugin, 2400L);
        
        return getDescription();
    }
}