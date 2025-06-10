package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * ウェーブ型液化システム
 * フェーズ1: 3x3から開始
 * フェーズ2: 5秒間隔で範囲拡大（最大11x11）
 * フェーズ3: ランダムに泥・砂・砂利が混在
 * フェーズ4: 復旧時も段階的（外側から内側へ）
 * 深度制限: プレイヤー位置から下2ブロックまで
 */
public class GroundLiquefactionEffect extends UnluckyEffectBase {

    public GroundLiquefactionEffect(JavaPlugin plugin) {
        super(plugin, "地面液化", EffectRarity.UNCOMMON);
    }

    private final Random random = new Random();
    private final Map<Block, Material> originalBlocks = new HashMap<>();
    
    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        originalBlocks.clear();
        
        player.sendMessage(ChatColor.BLUE + "⚠ 地面液化が開始されました！");
        player.sendMessage(ChatColor.YELLOW + "🌊 ウェーブ状に広がっていきます...");
        
        // 初期エフェクト
        player.playSound(center, Sound.ITEM_BUCKET_FILL, 1.0f, 0.8f);
        center.getWorld().spawnParticle(
            Particle.SPLASH,
            center,
            20, 2, 1, 2, 0.1
        );
        
        // ウェーブ液化システム開始
        startWaveLiquefaction(player, center);
        
        return getDescription();
    }
    
    /**
     * ウェーブ型液化システムを開始
     */
    private void startWaveLiquefaction(Player player, Location center) {
        new BukkitRunnable() {
            int wavePhase = 0;
            final int maxPhases = 3; // 3x3 → 7x7 → 11x11
            
            @Override
            public void run() {
                if (!player.isOnline() || wavePhase >= maxPhases) {
                    // 最終フェーズ: ランダム材質混合
                    if (wavePhase >= maxPhases) {
                        addRandomMaterials(player, center);
                        // 15秒後に復旧開始
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                startRestoration(player, center);
                            }
                        }.runTaskLater(plugin, 300L); // 15秒後
                    }
                    this.cancel();
                    return;
                }
                
                int radius = 1 + (wavePhase * 2); // 1, 3, 5 (範囲は3x3, 7x7, 11x11)
                liquefyWave(player, center, radius, wavePhase + 1);
                wavePhase++;
            }
        }.runTaskTimer(plugin, 0L, 100L); // 5秒間隔
    }
    
    /**
     * 指定範囲で液化ウェーブを実行
     */
    private void liquefyWave(Player player, Location center, int radius, int phase) {
        List<Block> newLiquidBlocks = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // 円形の範囲で液化
                if (x * x + z * z <= radius * radius) {
                    // 下2ブロックまでを液化
                    for (int y = -1; y >= -2; y--) {
                        Block block = center.getWorld().getBlockAt(
                            center.getBlockX() + x, 
                            center.getBlockY() + y, 
                            center.getBlockZ() + z
                        );
                        
                        if (canLiquefy(block)) {
                            if (!originalBlocks.containsKey(block)) {
                                originalBlocks.put(block, block.getType());
                            }
                            block.setType(Material.WATER);
                            newLiquidBlocks.add(block);
                        }
                    }
                }
            }
        }
        
        // ウェーブエフェクト
        if (!newLiquidBlocks.isEmpty()) {
            player.sendMessage(ChatColor.AQUA + "🌊 フェーズ" + phase + ": 液化範囲が拡大しました！");
            
            // 液化音とエフェクト
            center.getWorld().playSound(center, Sound.BLOCK_WATER_AMBIENT, 1.0f, 1.0f);
            
            // 波紋パーティクルエフェクト
            for (int i = 0; i < 16; i++) {
                double angle = i * Math.PI * 2 / 16;
                Location wavePos = center.clone().add(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
                );
                
                center.getWorld().spawnParticle(
                    Particle.DRIPPING_DRIPSTONE_WATER,
                    wavePos,
                    2, 0.2, 0.1, 0.2, 0.05
                );
            }
        }
    }
    
    /**
     * ランダム材質を混合させる
     */
    private void addRandomMaterials(Player player, Location center) {
        player.sendMessage(ChatColor.GOLD + "🌋 特殊材質が混合し始めました...");
        
        Material[] mixMaterials = {Material.MUD, Material.SAND, Material.GRAVEL, Material.CLAY};
        
        List<Block> waterBlocks = new ArrayList<>();
        for (Block block : originalBlocks.keySet()) {
            if (block.getType() == Material.WATER) {
                waterBlocks.add(block);
            }
        }
        
        // 30%のブロックをランダム材質に変更
        int changeCount = Math.max(1, waterBlocks.size() / 3);
        Collections.shuffle(waterBlocks);
        
        for (int i = 0; i < Math.min(changeCount, waterBlocks.size()); i++) {
            Block block = waterBlocks.get(i);
            Material newMaterial = mixMaterials[random.nextInt(mixMaterials.length)];
            block.setType(newMaterial);
        }
        
        // ミックスエフェクト
        center.getWorld().playSound(center, Sound.BLOCK_GRAVEL_BREAK, 1.0f, 0.8f);
        center.getWorld().spawnParticle(
            Particle.BLOCK,
            center.clone().add(0, 1, 0),
            30, 3, 1, 3, 0.1,
            Material.MUD.createBlockData()
        );
    }
    
    /**
     * 段階的復旧システム（外側から内側へ）
     */
    private void startRestoration(Player player, Location center) {
        if (!player.isOnline()) return;
        
        player.sendMessage(ChatColor.GREEN + "✨ 地面の復旧が開始されました...");
        
        // 中心からの距離でブロックをグループ化
        Map<Integer, List<Block>> distanceGroups = new HashMap<>();
        
        for (Block block : originalBlocks.keySet()) {
            int distance = (int) Math.sqrt(
                Math.pow(block.getX() - center.getBlockX(), 2) +
                Math.pow(block.getZ() - center.getBlockZ(), 2)
            );
            
            distanceGroups.computeIfAbsent(distance, k -> new ArrayList<>()).add(block);
        }
        
        // 外側から復旧（2秒間隔）
        List<Integer> distances = new ArrayList<>(distanceGroups.keySet());
        distances.sort(Collections.reverseOrder()); // 遠い順にソート
        
        new BukkitRunnable() {
            int groupIndex = 0;
            
            @Override
            public void run() {
                if (groupIndex >= distances.size()) {
                    finishRestoration(player);
                    this.cancel();
                    return;
                }
                
                int distance = distances.get(groupIndex);
                List<Block> blocks = distanceGroups.get(distance);
                
                // この距離のブロックを復旧
                for (Block block : blocks) {
                    Material original = originalBlocks.get(block);
                    if (original != null) {
                        block.setType(original);
                    }
                }
                
                // 復旧エフェクト
                if (!blocks.isEmpty()) {
                    Block sampleBlock = blocks.get(0);
                    center.getWorld().playSound(sampleBlock.getLocation(), Sound.BLOCK_STONE_PLACE, 0.5f, 1.0f);
                    
                    center.getWorld().spawnParticle(
                        Particle.BLOCK,
                        sampleBlock.getLocation().add(0.5, 1, 0.5),
                        5, 0.3, 0.3, 0.3, 0.1,
                        originalBlocks.get(sampleBlock).createBlockData()
                    );
                }
                
                groupIndex++;
            }
        }.runTaskTimer(plugin, 0L, 40L); // 2秒間隔
    }
    
    /**
     * 復旧完了処理
     */
    private void finishRestoration(Player player) {
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "✅ 地面の復旧が完了しました！");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        }
        originalBlocks.clear();
    }
    
    /**
     * ブロックが液化可能かどうかチェック
     */
    private boolean canLiquefy(Block block) {
        Material type = block.getType();
        return type.isSolid() && 
               type != Material.BEDROCK && 
               type != Material.OBSIDIAN &&
               type != Material.END_PORTAL_FRAME &&
               !type.name().contains("SHULKER_BOX") &&
               type != Material.WATER &&
               type != Material.LAVA;
    }
}