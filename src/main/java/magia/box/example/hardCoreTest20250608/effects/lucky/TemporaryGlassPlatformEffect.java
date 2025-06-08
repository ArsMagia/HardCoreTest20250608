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

    /** 足場の持続時間（秒） */
    private static final int PLATFORM_DURATION_SECONDS = 8;
    
    /** 消える前の警告時間（秒） */
    private static final int WARNING_TIME_SECONDS = 2;
    
    /** 足場の範囲（3x3） */
    private static final int PLATFORM_RANGE = 1; // 中心から1ブロック = 3x3
    
    /** アクティブな足場を管理するマップ */
    private static final Map<Location, Material> activePlatforms = new HashMap<>();

    public TemporaryGlassPlatformEffect(JavaPlugin plugin) {
        super(plugin, "一時ガラス足場", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        Location playerLoc = player.getLocation();
        Location platformCenter = playerLoc.clone().subtract(0, 1, 0); // プレイヤーの足元
        List<Location> platformBlocks = new ArrayList<>();
        
        // 3x3の範囲でガラスブロックを配置
        int blocksPlaced = 0;
        for (int x = -PLATFORM_RANGE; x <= PLATFORM_RANGE; x++) {
            for (int z = -PLATFORM_RANGE; z <= PLATFORM_RANGE; z++) {
                Location blockLoc = platformCenter.clone().add(x, 0, z);
                Block block = blockLoc.getBlock();
                
                // 空気ブロックのみを置換
                if (block.getType() == Material.AIR) {
                    // 元のブロック情報を保存
                    activePlatforms.put(blockLoc.clone(), Material.AIR);
                    
                    // ガラスブロックを配置
                    block.setType(Material.GLASS);
                    platformBlocks.add(blockLoc.clone());
                    blocksPlaced++;
                    
                    // 配置エフェクト
                    blockLoc.getWorld().spawnParticle(
                        Particle.CLOUD,
                        blockLoc.clone().add(0.5, 1, 0.5),
                        5, 0.2, 0.2, 0.2, 0.1
                    );
                }
            }
        }
        
        if (blocksPlaced == 0) {
            player.sendMessage(ChatColor.YELLOW + "足場を配置する空間がありませんでした。");
            return getDescription() + " (失敗)";
        }
        
        // プレイヤーへの通知とエフェクト
        player.sendMessage(ChatColor.AQUA + "✨ 一時的なガラス足場が生成されました！");
        player.sendMessage(ChatColor.GRAY + "(" + PLATFORM_DURATION_SECONDS + "秒後に自動的に消失します)");
        
        // 生成エフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_PLACE, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            platformCenter.clone().add(0, 1, 0),
            30, 1.5, 0.5, 1.5, 0.2
        );
        
        // 警告タスクをスケジュール（消える2秒前）
        new BukkitRunnable() {
            @Override
            public void run() {
                scheduleWarningEffects(player, platformBlocks);
            }
        }.runTaskLater(plugin, (PLATFORM_DURATION_SECONDS - WARNING_TIME_SECONDS) * 20L);
        
        // 削除タスクをスケジュール
        new BukkitRunnable() {
            @Override
            public void run() {
                removePlatform(player, platformBlocks);
            }
        }.runTaskLater(plugin, PLATFORM_DURATION_SECONDS * 20L);
        
        return getDescription() + " (" + blocksPlaced + "ブロック)";
    }
    
    /**
     * 消失前の警告エフェクトをスケジュール
     * @param player プレイヤー
     * @param platformBlocks 足場ブロックのリスト
     */
    private void scheduleWarningEffects(Player player, List<Location> platformBlocks) {
        // 警告音
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 
                EffectConstants.STANDARD_VOLUME, 0.5f);
        
        // 警告メッセージ
        player.sendMessage(ChatColor.RED + "⚠ ガラス足場が間もなく消失します！");
        
        // 警告パーティクルを点滅させる
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = WARNING_TIME_SECONDS * 20; // 2秒間
            
            @Override
            public void run() {
                if (ticks >= maxTicks) {
                    cancel();
                    return;
                }
                
                // 0.5秒ごとに点滅
                if (ticks % 10 == 0) {
                    for (Location loc : platformBlocks) {
                        if (loc.getBlock().getType() == Material.GLASS) {
                            loc.getWorld().spawnParticle(
                                Particle.DUST,
                                loc.clone().add(0.5, 1, 0.5),
                                8, 0.3, 0.3, 0.3, 0,
                                new Particle.DustOptions(Color.RED, 1.0f)
                            );
                        }
                    }
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * 足場を削除
     * @param player プレイヤー
     * @param platformBlocks 足場ブロックのリスト
     */
    private void removePlatform(Player player, List<Location> platformBlocks) {
        int blocksRemoved = 0;
        
        for (Location loc : platformBlocks) {
            if (loc.getBlock().getType() == Material.GLASS && activePlatforms.containsKey(loc)) {
                // 元のブロックタイプに戻す
                Material originalType = activePlatforms.remove(loc);
                loc.getBlock().setType(originalType);
                blocksRemoved++;
                
                // 消失エフェクト
                loc.getWorld().spawnParticle(
                    Particle.BLOCK,
                    loc.clone().add(0.5, 0.5, 0.5),
                    10, 0.3, 0.3, 0.3, 0.1,
                    Material.GLASS.createBlockData()
                );
            }
        }
        
        if (blocksRemoved > 0) {
            // 消失音とメッセージ
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            player.sendMessage(ChatColor.GRAY + "ガラス足場が消失しました。");
            
            // 最終エフェクト
            Location centerLoc = platformBlocks.get(platformBlocks.size() / 2);
            centerLoc.getWorld().spawnParticle(
                Particle.POOF,
                centerLoc.clone().add(0, 1, 0),
                15, 1, 0.5, 1, 0.1
            );
        }
    }
}