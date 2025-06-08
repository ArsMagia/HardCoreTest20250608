package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FallingBlocksEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public FallingBlocksEffect(JavaPlugin plugin) {
        super(plugin, "重力ブロック", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.RED + "周囲のブロックが落下し始めました！");
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        
        Location center = player.getLocation();
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 10 || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                // ランダムな位置から落下ブロックを生成
                for (int i = 0; i < 3; i++) {
                    int x = center.getBlockX() + random.nextInt(10) - 5;
                    int z = center.getBlockZ() + random.nextInt(10) - 5;
                    int y = center.getBlockY() + 5 + random.nextInt(5);
                    
                    Location spawnLoc = new Location(center.getWorld(), x, y, z);
                    
                    Material[] materials = {Material.STONE, Material.DIRT, Material.SAND, Material.GRAVEL};
                    Material material = materials[random.nextInt(materials.length)];
                    
                    FallingBlock fallingBlock = center.getWorld().spawnFallingBlock(spawnLoc, material.createBlockData());
                    fallingBlock.setDropItem(false);
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}