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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlantGrowthWaveEffect extends LuckyEffectBase {

    private static final Random random = new Random();
    private static final List<Material> CROP_SEEDS = List.of(
        Material.CARROTS,
        Material.POTATOES, 
        Material.BEETROOTS
    );
    private static final int PLANT_RADIUS = 3; // 2-3ブロック範囲

    public PlantGrowthWaveEffect(JavaPlugin plugin) {
        super(plugin, "農作物生成", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        int plantedCount = 0;
        
        // 2-3ブロック範囲でランダムに作物を植える
        for (int x = -PLANT_RADIUS; x <= PLANT_RADIUS; x++) {
            for (int z = -PLANT_RADIUS; z <= PLANT_RADIUS; z++) {
                // 円形に近い範囲で植える（距離チェック）
                if (Math.sqrt(x*x + z*z) > PLANT_RADIUS) continue;
                
                Block groundBlock = center.getWorld().getBlockAt(
                    center.getBlockX() + x, 
                    center.getBlockY() - 1, 
                    center.getBlockZ() + z
                );
                Block plantBlock = groundBlock.getRelative(0, 1, 0);
                
                // 植える条件をチェック
                if (canPlantCrop(groundBlock, plantBlock)) {
                    plantCrop(plantBlock);
                    plantedCount++;
                } else if (plantBlock.getType() == Material.AIR) {
                    // 空中に土ブロックと作物を生成
                    groundBlock.setType(Material.DIRT);
                    plantCrop(plantBlock);
                    plantedCount++;
                    
                    // 特別エフェクト（空中生成）
                    groundBlock.getLocation().getWorld().spawnParticle(
                        Particle.BLOCK,
                        groundBlock.getLocation().add(0.5, 0.5, 0.5),
                        10, 0.3, 0.3, 0.3, 0.1,
                        Material.DIRT.createBlockData()
                    );
                }
            }
        }
        
        // 最低保障：作物が1つも植えられなかった場合はプレイヤーの直前に強制生成
        if (plantedCount == 0) {
            Block emergencyGround = center.getWorld().getBlockAt(
                center.getBlockX(), 
                center.getBlockY() - 1, 
                center.getBlockZ()
            );
            Block emergencyPlant = emergencyGround.getRelative(0, 1, 0);
            
            emergencyGround.setType(Material.FARMLAND);
            plantCrop(emergencyPlant);
            plantedCount = 1;
            
            // 緊急生成エフェクト
            emergencyGround.getLocation().getWorld().spawnParticle(
                Particle.ENCHANT,
                emergencyPlant.getLocation().add(0.5, 0.5, 0.5),
                15, 0.5, 0.5, 0.5, 0.1
            );
            
            player.sendMessage(ChatColor.YELLOW + "特別な力でプレイヤーの直前に作物を生成しました！");
        }
        
        if (plantedCount > 0) {
            player.sendMessage(ChatColor.GREEN + "農作物生成により " + plantedCount + " 個の作物を生成しました！");
            player.playSound(center, Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.2f);
            
            center.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                center.add(0, 1, 0),
                30, 3, 2, 3, 0.1
            );
        }
        
        return getDescription();
    }
    
    /**
     * 作物を植えることができるかチェック
     * @param groundBlock 地面ブロック
     * @param plantBlock 植える場所のブロック
     * @return 植えられるかどうか
     */
    private boolean canPlantCrop(Block groundBlock, Block plantBlock) {
        // 地面が耕地または土・草ブロックである
        Material groundType = groundBlock.getType();
        boolean validGround = groundType == Material.FARMLAND || 
                             groundType == Material.DIRT || 
                             groundType == Material.GRASS_BLOCK;
        
        // 植える場所が空気ブロックである
        boolean validPlantSpace = plantBlock.getType() == Material.AIR;
        
        return validGround && validPlantSpace;
    }
    
    /**
     * 作物を植える
     * @param plantBlock 植えるブロック
     */
    private void plantCrop(Block plantBlock) {
        // ランダムな作物を選択
        Material cropType = CROP_SEEDS.get(random.nextInt(CROP_SEEDS.size()));
        plantBlock.setType(cropType);
        
        // パーティクルエフェクト
        plantBlock.getLocation().getWorld().spawnParticle(
            Particle.COMPOSTER,
            plantBlock.getLocation().add(0.5, 0.5, 0.5),
            5, 0.3, 0.3, 0.3, 0.05
        );
    }
}