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
        super(plugin, "重力ブロック", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.RED + "周囲のブロックが落下し始めました！");
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
        
        Location center = player.getLocation();
        
        new BukkitRunnable() {
            int counter = 0;
            int totalBlocksSpawned = 0;
            final int maxBlocks = 30; // 最大ブロック数制限（1.5倍）
            
            @Override
            public void run() {
                if (counter >= 10 || !player.isOnline() || totalBlocksSpawned >= maxBlocks) {
                    this.cancel();
                    return;
                }
                
                // ランダムな位置から落下ブロックを生成（1.5倍）
                int blocksThisRound = Math.min(3, maxBlocks - totalBlocksSpawned); // 最大3個まで（1.5倍）
                for (int i = 0; i < blocksThisRound; i++) {
                    int x = center.getBlockX() + random.nextInt(8) - 4; // 範囲も削減
                    int z = center.getBlockZ() + random.nextInt(8) - 4;
                    int y = center.getBlockY() + 5 + random.nextInt(3); // 高さも削減
                    
                    Location spawnLoc = new Location(center.getWorld(), x, y, z);
                    
                    Material[] materials = {Material.STONE, Material.DIRT, Material.SAND, Material.GRAVEL};
                    Material material = materials[random.nextInt(materials.length)];
                    
                    FallingBlock fallingBlock = center.getWorld().spawnFallingBlock(spawnLoc, material.createBlockData());
                    fallingBlock.setDropItem(false);
                    fallingBlock.setHurtEntities(false); // エンティティダメージを無効化
                    totalBlocksSpawned++;
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 14L); // 頻度1.5倍（20L → 14L）
        
        return getDescription();
    }
}