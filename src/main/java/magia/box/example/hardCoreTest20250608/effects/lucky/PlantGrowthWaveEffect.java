package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PlantGrowthWaveEffect extends LuckyEffectBase {

    public PlantGrowthWaveEffect(JavaPlugin plugin) {
        super(plugin, "植物成長波", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        int grownCount = 0;
        
        for (int x = -8; x <= 8; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -8; z <= 8; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                    
                    if (isCrop(block)) {
                        BlockData data = block.getBlockData();
                        if (data instanceof Ageable) {
                            Ageable ageable = (Ageable) data;
                            if (ageable.getAge() < ageable.getMaximumAge()) {
                                ageable.setAge(ageable.getMaximumAge());
                                block.setBlockData(ageable);
                                grownCount++;
                                
                                block.getLocation().getWorld().spawnParticle(
                                    Particle.HAPPY_VILLAGER,
                                    block.getLocation().add(0.5, 1, 0.5),
                                    3, 0.2, 0.2, 0.2, 0.02
                                );
                            }
                        }
                    }
                }
            }
        }
        
        if (grownCount > 0) {
            player.sendMessage(ChatColor.GREEN + "植物成長波により " + grownCount + " 個の作物が成長しました！");
            player.playSound(center, Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.2f);
            
            center.getWorld().spawnParticle(
                Particle.COMPOSTER,
                center.add(0, 1, 0),
                50, 5, 2, 5, 0.1
            );
        } else {
            player.sendMessage(ChatColor.YELLOW + "周囲に成長可能な作物がありませんでした。代わりに経験値を獲得します。");
            player.giveExp(50);
        }
        
        return getDescription();
    }
    
    private boolean isCrop(Block block) {
        Material type = block.getType();
        return type == Material.WHEAT || type == Material.CARROTS || 
               type == Material.POTATOES || type == Material.BEETROOTS ||
               type == Material.COCOA || type == Material.NETHER_WART ||
               type == Material.SWEET_BERRY_BUSH || type == Material.BAMBOO;
    }
}