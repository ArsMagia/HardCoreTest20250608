package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiderWebPowderSnowTrapEffect extends UnluckyEffectBase {

    /** トラップの持続時間（秒） */
    private static final int TRAP_DURATION_SECONDS = 10;
    
    /** トラップの範囲（3x3） */
    private static final int TRAP_RANGE = 1; // 中心から1ブロック = 3x3
    
    /** 粉雪ダメージ量 */
    private static final double POWDER_SNOW_DAMAGE = 1.0;
    
    /** ダメージ間隔（秒） */
    private static final int DAMAGE_INTERVAL_SECONDS = 2;
    
    /** アクティブなトラップを管理するマップ */
    private static final Map<Location, Material> activeTraps = new HashMap<>();

    public SpiderWebPowderSnowTrapEffect(JavaPlugin plugin) {
        super(plugin, "蜘蛛の巣粉雪トラップ", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        Location playerLoc = player.getLocation();
        Location trapCenter = playerLoc.clone(); // プレイヤーの位置
        List<Location> trapBlocks = new ArrayList<>();
        
        // 3x3の範囲で蜘蛛の巣と粉雪を交互に配置
        int blocksPlaced = 0;
        for (int x = -TRAP_RANGE; x <= TRAP_RANGE; x++) {
            for (int z = -TRAP_RANGE; z <= TRAP_RANGE; z++) {
                Location blockLoc = trapCenter.clone().add(x, 0, z);
                Block block = blockLoc.getBlock();
                
                // 空気ブロックのみを置換
                if (block.getType() == Material.AIR) {
                    // 元のブロック情報を保存
                    activeTraps.put(blockLoc.clone(), Material.AIR);
                    
                    // チェス盤状に蜘蛛の巣と粉雪を配置
                    Material trapMaterial = ((x + z) % 2 == 0) ? Material.COBWEB : Material.POWDER_SNOW;
                    block.setType(trapMaterial);
                    trapBlocks.add(blockLoc.clone());
                    blocksPlaced++;
                    
                    // 配置エフェクト
                    blockLoc.getWorld().spawnParticle(
                        Particle.SMOKE,
                        blockLoc.clone().add(0.5, 0.5, 0.5),
                        8, 0.3, 0.3, 0.3, 0.1
                    );
                }
            }
        }
        
        if (blocksPlaced == 0) {
            player.sendMessage(ChatColor.RED + "トラップを設置する空間がありませんでした。");
            return getDescription() + " (失敗)";
        }
        
        // プレイヤーへの通知とエフェクト
        player.sendMessage(ChatColor.DARK_RED + "💀 危険な蜘蛛の巣粉雪トラップが設置されました！");
        player.sendMessage(ChatColor.GRAY + "(" + TRAP_DURATION_SECONDS + "秒間持続)");
        
        // 生成エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 
                EffectConstants.STANDARD_VOLUME, 0.7f);
        player.playSound(player.getLocation(), Sound.BLOCK_POWDER_SNOW_PLACE, 
                EffectConstants.STANDARD_VOLUME, 1.0f);
        
        player.getWorld().spawnParticle(
            Particle.FALLING_DUST,
            trapCenter.clone().add(0, 1, 0),
            50, 1.5, 0.5, 1.5, 0.1,
            Material.COBWEB.createBlockData()
        );
        
        // ダメージタスクを開始
        startDamageTask(trapBlocks);
        
        // 削除タスクをスケジュール
        new BukkitRunnable() {
            @Override
            public void run() {
                removeTrap(player, trapBlocks);
            }
        }.runTaskLater(plugin, TRAP_DURATION_SECONDS * 20L);
        
        return getDescription() + " (" + blocksPlaced + "ブロック)";
    }
    
    /**
     * 定期的なダメージタスクを開始
     * @param trapBlocks トラップブロックのリスト
     */
    private void startDamageTask(List<Location> trapBlocks) {
        new BukkitRunnable() {
            int ticksElapsed = 0;
            final int maxTicks = TRAP_DURATION_SECONDS * 20;
            final int damageInterval = DAMAGE_INTERVAL_SECONDS * 20;
            
            @Override
            public void run() {
                if (ticksElapsed >= maxTicks) {
                    cancel();
                    return;
                }
                
                // ダメージ間隔ごとに実行
                if (ticksElapsed % damageInterval == 0) {
                    for (Location loc : trapBlocks) {
                        Block block = loc.getBlock();
                        
                        // 粉雪ブロックのみダメージ処理
                        if (block.getType() == Material.POWDER_SNOW) {
                            applyPowderSnowDamage(loc);
                        }
                        
                        // 蜘蛛の巣ブロックでは移動速度低下
                        if (block.getType() == Material.COBWEB) {
                            applyWebSlowness(loc);
                        }
                    }
                }
                
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * 粉雪ダメージを適用
     * @param location 粉雪ブロックの位置
     */
    private void applyPowderSnowDamage(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // 周囲のエンティティを取得
        for (Entity entity : world.getNearbyEntities(location, 1.0, 1.0, 1.0)) {
            if (entity instanceof LivingEntity livingEntity) {
                // ダメージを与える
                livingEntity.damage(POWDER_SNOW_DAMAGE);
                
                // 凍結効果
                if (livingEntity instanceof Player player) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 1));
                    player.sendMessage(ChatColor.BLUE + "❄ 凍える寒さがあなたを襲います！");
                }
                
                // 凍結エフェクト
                world.spawnParticle(
                    Particle.SNOWFLAKE,
                    livingEntity.getLocation().add(0, 1, 0),
                    10, 0.5, 0.5, 0.5, 0.1
                );
                
                // 凍結音
                world.playSound(location, Sound.BLOCK_POWDER_SNOW_STEP, 
                        EffectConstants.STANDARD_VOLUME, 0.5f);
            }
        }
    }
    
    /**
     * 蜘蛛の巣による移動速度低下を適用
     * @param location 蜘蛛の巣ブロックの位置
     */
    private void applyWebSlowness(Location location) {
        World world = location.getWorld();
        if (world == null) return;
        
        // 周囲のエンティティを取得
        for (Entity entity : world.getNearbyEntities(location, 1.0, 1.0, 1.0)) {
            if (entity instanceof Player player) {
                // 移動速度低下効果
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                
                // 蜘蛛の巣エフェクト
                world.spawnParticle(
                    Particle.BLOCK,
                    player.getLocation().add(0, 1, 0),
                    5, 0.3, 0.3, 0.3, 0.1,
                    Material.COBWEB.createBlockData()
                );
            }
        }
    }
    
    /**
     * トラップを削除
     * @param player プレイヤー
     * @param trapBlocks トラップブロックのリスト
     */
    private void removeTrap(Player player, List<Location> trapBlocks) {
        int blocksRemoved = 0;
        
        for (Location loc : trapBlocks) {
            Block block = loc.getBlock();
            if ((block.getType() == Material.COBWEB || block.getType() == Material.POWDER_SNOW) 
                && activeTraps.containsKey(loc)) {
                
                // 元のブロックタイプに戻す
                Material originalType = activeTraps.remove(loc);
                block.setType(originalType);
                blocksRemoved++;
                
                // 消失エフェクト
                loc.getWorld().spawnParticle(
                    Particle.POOF,
                    loc.clone().add(0.5, 0.5, 0.5),
                    8, 0.3, 0.3, 0.3, 0.1
                );
            }
        }
        
        if (blocksRemoved > 0) {
            // 消失音とメッセージ
            player.playSound(player.getLocation(), Sound.ENTITY_SPIDER_DEATH, 
                    EffectConstants.STANDARD_VOLUME, 1.2f);
            
            player.sendMessage(ChatColor.GRAY + "蜘蛛の巣粉雪トラップが消失しました。");
            
            // 最終エフェクト
            Location centerLoc = trapBlocks.get(trapBlocks.size() / 2);
            centerLoc.getWorld().spawnParticle(
                Particle.EXPLOSION,
                centerLoc.clone().add(0, 0.5, 0),
                5, 1, 0.5, 1, 0.1
            );
        }
    }
}