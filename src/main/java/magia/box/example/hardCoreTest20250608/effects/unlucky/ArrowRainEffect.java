package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowRainEffect extends UnluckyEffectBase {
    
    /** 矢雨開始までの遅延時間（秒） */
    private static final int START_DELAY_SECONDS = 3;
    
    /** 矢雨の持続時間（秒） */
    private static final int DURATION_SECONDS = 15;
    
    /** 矢の降下間隔（ティック） */
    private static final int ARROW_INTERVAL_TICKS = 10; // 0.5秒間隔
    
    /** 降下範囲の半径 */
    private static final int RAIN_RADIUS = 8;
    
    /** 上空からの高さ */
    private static final int SKY_HEIGHT = 20;
    
    /** ランダム生成器 */
    private final Random random = new Random();

    public ArrowRainEffect(JavaPlugin plugin) {
        super(plugin, "矢雨", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // 初期通知
        player.sendMessage(ChatColor.DARK_RED + "⚠ 不吉な雲がたちこめています...");
        player.sendMessage(ChatColor.RED + "💀 " + START_DELAY_SECONDS + "秒後に矢雨が降り注ぎます！");
        
        // 初期エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 
                EffectConstants.STANDARD_VOLUME, 0.5f);
        
        player.getWorld().spawnParticle(
            Particle.SMOKE,
            player.getLocation().add(0, 3, 0),
            20, 2, 2, 2, 0.1
        );
        
        // 遅延後に矢雨開始
        new BukkitRunnable() {
            @Override
            public void run() {
                startArrowRain(player);
            }
        }.runTaskLater(plugin, START_DELAY_SECONDS * 20L);
        
        return getDescription() + " (3秒後開始)";
    }
    
    /**
     * 矢雨を開始
     * @param player 対象プレイヤー
     */
    private void startArrowRain(Player player) {
        // プレイヤーがオフラインの場合は中止
        if (!player.isOnline()) {
            return;
        }
        
        // 矢雨開始通知
        player.sendMessage(ChatColor.DARK_RED + "🏹 矢雨が降り始めました！" + DURATION_SECONDS + "秒間続きます！");
        player.sendMessage(ChatColor.YELLOW + "💡 建物の中に避難してください！");
        
        // 開始エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 
                EffectConstants.STANDARD_VOLUME, 0.8f);
        
        Location playerLoc = player.getLocation();
        player.getWorld().spawnParticle(
            Particle.CRIT,
            playerLoc.add(0, 10, 0),
            50, RAIN_RADIUS, 3, RAIN_RADIUS, 0.1
        );
        
        // 矢雨タスクを開始
        new BukkitRunnable() {
            int ticksElapsed = 0;
            final int maxTicks = DURATION_SECONDS * 20;
            
            @Override
            public void run() {
                // プレイヤーがオフラインまたは時間切れの場合は終了
                if (!player.isOnline() || ticksElapsed >= maxTicks) {
                    finishArrowRain(player);
                    cancel();
                    return;
                }
                
                // 矢の降下間隔ごとに実行（パフォーマンス最適化）
                if (ticksElapsed % ARROW_INTERVAL_TICKS == 0) {
                    // 矢の数を制限（2-3本に削減）
                    int arrowCount = 2 + random.nextInt(2); // 2-3本の矢
                    for (int i = 0; i < arrowCount; i++) {
                        spawnRainArrow(player);
                    }
                }
                
                // 継続的なエフェクト（2秒ごと）
                if (ticksElapsed % 40 == 0) {
                    Location currentLoc = player.getLocation();
                    player.getWorld().spawnParticle(
                        Particle.FALLING_DUST,
                        currentLoc.add(0, 15, 0),
                        10, RAIN_RADIUS / 2, 2, RAIN_RADIUS / 2, 0.1,
                        Material.STONE.createBlockData()
                    );
                    
                    // 時々警告音
                    if (ticksElapsed % 60 == 0) { // 3秒ごと
                        player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 
                                EffectConstants.STANDARD_VOLUME * 0.3f, 1.2f);
                    }
                }
                
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * 矢を1本降らせる（完全回避システム対応）
     * @param player 対象プレイヤー
     */
    private void spawnRainArrow(Player player) {
        Location playerLoc = player.getLocation();
        
        // 完全回避条件チェック
        if (isPlayerFullyProtected(playerLoc)) {
            // 完全回避時は矢を降らせない
            return;
        }
        
        // 部分回避条件チェック
        boolean partiallyProtected = isPlayerPartiallyProtected(playerLoc);
        if (partiallyProtected && random.nextInt(100) < 50) {
            // 50%の確率で回避
            return;
        }
        
        // プレイヤーの周囲ランダム位置を計算（追跡型）
        double offsetX, offsetZ;
        if (random.nextInt(100) < 70) {
            // 70%の確率でプレイヤー周辺を狙う（追跡要素）
            offsetX = (random.nextDouble() - 0.5) * 6; // 狭い範囲
            offsetZ = (random.nextDouble() - 0.5) * 6;
        } else {
            // 30%の確率で完全ランダム
            offsetX = (random.nextDouble() - 0.5) * RAIN_RADIUS * 2;
            offsetZ = (random.nextDouble() - 0.5) * RAIN_RADIUS * 2;
        }
        
        // 上空からの降下位置を設定
        Location spawnLoc = playerLoc.clone().add(offsetX, SKY_HEIGHT, offsetZ);
        
        // 矢を生成
        Arrow arrow = spawnLoc.getWorld().spawnArrow(spawnLoc, new Vector(0, -1, 0), 0.8f, 2.0f);
        
        // 矢の設定（安全な値に調整）
        arrow.setShooter(null); // シューターなしで自然災害として扱う
        arrow.setCritical(false);
        arrow.setKnockbackStrength(1); // 軽いノックバック
        arrow.setPierceLevel(0);
        arrow.setDamage(1.5); // 0.75ハートのダメージ（低体力時の即死防止）
        
        // 矢に重力を適用して自然な落下にする
        arrow.setGravity(true);
        
        // 着地エフェクト用のタスク（パフォーマンス最適化）
        new BukkitRunnable() {
            int checks = 0;
            final int maxChecks = 10; // 最大チェック回数制限
            
            @Override
            public void run() {
                if (arrow.isDead() || arrow.isOnGround() || checks >= maxChecks) {
                    // 着地エフェクト（確率的に表示）
                    if (arrow.isDead() || arrow.isOnGround()) {
                        if (random.nextInt(100) < 30) { // 30%の確率で着地エフェクト
                            Location arrowLoc = arrow.getLocation();
                            arrowLoc.getWorld().spawnParticle(
                                Particle.BLOCK,
                                arrowLoc,
                                3, 0.2, 0.1, 0.2, 0.05,
                                Material.DIRT.createBlockData()
                            );
                        }
                    }
                    cancel();
                }
                checks++;
            }
        }.runTaskTimer(plugin, 20L, 10L); // 1秒後から10ティックごとにチェック
    }
    
    /**
     * プレイヤーが完全に保護されているかチェック
     * @param location プレイヤーの位置
     * @return 完全保護されている場合true
     */
    private boolean isPlayerFullyProtected(Location location) {
        // 1. 屋根下判定
        if (location.clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            return true;
        }
        
        // 2. 水中判定
        if (location.getBlock().getType() == Material.WATER) {
            return true;
        }
        
        // 3. 建物内判定（4方向+上下チェック）
        boolean enclosed = true;
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                if (!location.clone().add(x, 0, z).getBlock().getType().isSolid()) {
                    enclosed = false;
                    break;
                }
            }
        }
        if (enclosed) return true;
        
        // 4. 地下構造判定（Y座標 + 周囲ブロック密度）
        if (location.getY() < 50) {
            int solidBlocks = 0;
            for (int y = 1; y <= 3; y++) {
                if (location.clone().add(0, y, 0).getBlock().getType().isSolid()) {
                    solidBlocks++;
                }
            }
            if (solidBlocks >= 2) return true; // 頭上に固体ブロックが2つ以上
        }
        
        return false;
    }
    
    /**
     * プレイヤーが部分的に保護されているかチェック
     * @param location プレイヤーの位置
     * @return 部分保護されている場合true
     */
    private boolean isPlayerPartiallyProtected(Location location) {
        Material headBlock = location.clone().add(0, 1, 0).getBlock().getType();
        
        // 1. 木の下（葉ブロック）
        if (headBlock.name().contains("LEAVES")) {
            return true;
        }
        
        // 2. 半ブロック下（階段・スラブ等）
        if (headBlock.name().contains("STAIRS") || 
            headBlock.name().contains("SLAB") ||
            headBlock.name().contains("TRAPDOOR")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 矢雨終了処理
     * @param player 対象プレイヤー
     */
    private void finishArrowRain(Player player) {
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "🌤 矢雨が止みました。");
            player.sendMessage(ChatColor.GRAY + "嵐は去りました...");
            
            // 終了エフェクト
            player.playSound(player.getLocation(), Sound.WEATHER_RAIN_ABOVE, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 2, 0),
                15, 3, 1, 3, 0.1
            );
            
            // 周囲にパーティクルリング（安全の象徴）
            Location playerLoc = player.getLocation();
            for (int i = 0; i < 12; i++) {
                double angle = i * Math.PI * 2 / 12;
                Location ringLoc = playerLoc.clone().add(
                    Math.cos(angle) * 4,
                    1,
                    Math.sin(angle) * 4
                );
                
                playerLoc.getWorld().spawnParticle(
                    Particle.END_ROD,
                    ringLoc,
                    1, 0, 0, 0, 0
                );
            }
        }
    }
}