package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
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
import java.util.Random;

public class DimensionRiftEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public DimensionRiftEffect(JavaPlugin plugin) {
        super(plugin, "次元の裂け目", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "次元の裂け目が開きました！足元に危険なポータルが出現します。");
        
        Location center = player.getLocation();
        List<Block> portalBlocks = new ArrayList<>();
        
        // 足元に小さなポータルを作成
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z);
                if (block.getType() != Material.BEDROCK) {
                    portalBlocks.add(block);
                    block.setType(Material.NETHER_PORTAL);
                }
            }
        }
        
        player.playSound(center, Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.7f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 20 || !player.isOnline()) {
                    // ポータルを削除
                    for (Block block : portalBlocks) {
                        if (block.getType() == Material.NETHER_PORTAL) {
                            block.setType(Material.AIR);
                        }
                    }
                    player.sendMessage(ChatColor.GRAY + "次元の裂け目が閉じました。");
                    this.cancel();
                    return;
                }
                
                // パーティクル効果
                center.getWorld().spawnParticle(
                    Particle.PORTAL,
                    center,
                    10, 2, 1, 2, 0.1
                );
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}