package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemporaryGlassPlatformEffect extends LuckyEffectBase {

    /** 効果持続時間（8秒間） */
    private static final int EFFECT_DURATION_SECONDS = 8;
    
    /** ガラス生成間隔（ティック） */
    private static final int GENERATION_INTERVAL_TICKS = 10; // 0.5秒間隔
    
    /** プラットフォームの高度 */
    private static final int PLATFORM_HEIGHT = 20;
    
    /** プラットフォームの半径 */
    private static final int PLATFORM_RADIUS = 5;
    
    private final java.util.Random random = new java.util.Random();

    public TemporaryGlassPlatformEffect(JavaPlugin plugin) {
        super(plugin, "一時ガラス足場", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation().clone(); // プレイヤーの足元を中心に
        List<Block> allGlassBlocks = new ArrayList<>();
        
        // 初期通知
        player.sendMessage(ChatColor.AQUA + "🔮 一時ガラス足場が断続的に生成されます！" + EFFECT_DURATION_SECONDS + "秒間持続。");
        player.playSound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.2f);
        
        // 初期エフェクト
        center.getWorld().spawnParticle(
            Particle.ENCHANT,
            center,
            30, PLATFORM_RADIUS, 2, PLATFORM_RADIUS, 0.1
        );
        
        // 断続的生成タスク
        new BukkitRunnable() {
            int ticksElapsed = 0;
            final int maxTicks = EFFECT_DURATION_SECONDS * 20;
            
            @Override
            public void run() {
                // 時間切れの場合は終了
                if (ticksElapsed >= maxTicks) {
                    finishGlassPlatform(player, allGlassBlocks);
                    cancel();
                    return;
                }
                
                // 生成間隔ごとに実行
                if (ticksElapsed % GENERATION_INTERVAL_TICKS == 0) {
                    generateGlassBlocks(center, allGlassBlocks);
                }
                
                // 継続的なエフェクト（1秒ごと）
                if (ticksElapsed % 20 == 0) {
                    center.getWorld().spawnParticle(
                        Particle.FIREWORK,
                        center.clone().add(
                            (random.nextDouble() - 0.5) * PLATFORM_RADIUS * 2,
                            random.nextDouble() * 3,
                            (random.nextDouble() - 0.5) * PLATFORM_RADIUS * 2
                        ),
                        5, 0.5, 0.5, 0.5, 0.1
                    );
                    
                    // 生成音
                    player.playSound(center, Sound.BLOCK_GLASS_PLACE, 0.3f, 1.0f + random.nextFloat() * 0.4f);
                }
                
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        return getDescription() + " (" + EFFECT_DURATION_SECONDS + "秒間)";
    }
    
    /**
     * ガラスブロックを生成する
     * @param center 中心位置
     * @param allGlassBlocks 全てのガラスブロックのリスト
     */
    private void generateGlassBlocks(Location center, List<Block> allGlassBlocks) {
        // ランダムに3-7個のガラスブロックを生成
        int blocksToGenerate = 3 + random.nextInt(5); // 3-7個
        
        for (int i = 0; i < blocksToGenerate; i++) {
            // プレイヤーの足元周辺の範囲でランダムな位置を選択
            int offsetX = random.nextInt(PLATFORM_RADIUS * 2 + 1) - PLATFORM_RADIUS;
            int offsetZ = random.nextInt(PLATFORM_RADIUS * 2 + 1) - PLATFORM_RADIUS;
            int offsetY = random.nextInt(3) - 2; // -2, -1, 0の範囲（足元より下）
            
            Location glassLoc = center.clone().add(offsetX, offsetY, offsetZ);
            Block glassBlock = glassLoc.getBlock();
            
            // 空気ブロックの場合のみガラスを設置
            if (glassBlock.getType() == Material.AIR) {
                glassBlock.setType(Material.GLASS);
                allGlassBlocks.add(glassBlock);
                
                // 生成エフェクト
                glassBlock.getLocation().getWorld().spawnParticle(
                    Particle.BLOCK,
                    glassBlock.getLocation().add(0.5, 0.5, 0.5),
                    8, 0.3, 0.3, 0.3, 0.1,
                    Material.GLASS.createBlockData()
                );
            }
        }
    }
    
    /**
     * ガラス足場を終了する
     * @param player プレイヤー
     * @param allGlassBlocks 全てのガラスブロックのリスト
     */
    private void finishGlassPlatform(Player player, List<Block> allGlassBlocks) {
        int blocksRemoved = 0;
        
        // 全てのガラスブロックを削除
        for (Block block : allGlassBlocks) {
            if (block.getType() == Material.GLASS) {
                block.setType(Material.AIR);
                blocksRemoved++;
                
                // 消失エフェクト
                block.getLocation().getWorld().spawnParticle(
                    Particle.BLOCK,
                    block.getLocation().add(0.5, 0.5, 0.5),
                    10, 0.3, 0.3, 0.3, 0.1,
                    Material.GLASS.createBlockData()
                );
            }
        }
        
        if (blocksRemoved > 0) {
            // 消失音とメッセージ
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            player.sendMessage(ChatColor.GRAY + "⭐ ガラス足場が時間経過により消失しました。");
            
            // 最終エフェクト
            if (!allGlassBlocks.isEmpty()) {
                Block centerBlock = allGlassBlocks.get(allGlassBlocks.size() / 2);
                centerBlock.getLocation().getWorld().spawnParticle(
                    Particle.POOF,
                    centerBlock.getLocation().add(0, 1, 0),
                    15, 1, 0.5, 1, 0.1
                );
            }
        }
    }
}