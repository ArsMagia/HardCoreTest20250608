package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Collection;

public class MalphiteUltEffect extends LuckyEffectBase {

    public MalphiteUltEffect(JavaPlugin plugin) {
        super(plugin, "マルファイトのULT", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // ディメンション制限チェック
        World world = player.getWorld();
        if (world.getEnvironment() == World.Environment.NETHER) {
            player.sendMessage(ChatColor.RED + "⚠ ネザーでは「マルファイトのULT」は無効化されます。");
            return "ネザーでは無効化されました";
        }
        
        // エンドではエンダードラゴン優先
        if (world.getEnvironment() == World.Environment.THE_END) {
            return performEndDragonRush(player);
        }
        
        // 通常の突進処理
        return performNormalRush(player);
    }
    
    private String performEndDragonRush(Player player) {
        // エンダードラゴンを探す
        EnderDragon dragon = null;
        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof EnderDragon) {
                dragon = (EnderDragon) entity;
                break;
            }
        }
        
        if (dragon == null) {
            player.sendMessage(ChatColor.YELLOW + "エンダードラゴンが見つかりません。通常の突進を実行します。");
            return performNormalRush(player);
        }
        
        // エンダードラゴンへの突進
        rushToTarget(player, dragon, dragon.getLocation());
        
        // サーバー全体通知
        Bukkit.broadcastMessage(ChatColor.GOLD + "🐲 " + player.getName() + " がエンダードラゴンに「マルファイトのULT」を発動！");
        
        return "エンダードラゴンに突進しました";
    }
    
    private String performNormalRush(Player player) {
        // 半径30ブロック以内の最も近いモブを探す
        LivingEntity nearestMob = findNearestMob(player, 30.0);
        
        if (nearestMob == null) {
            player.sendMessage(ChatColor.YELLOW + "半径30ブロック以内にモブが見つかりません。");
            return "ターゲットが見つかりませんでした";
        }
        
        // ターゲットへの突進
        rushToTarget(player, nearestMob, nearestMob.getLocation());
        
        // サーバー全体通知
        String mobName = nearestMob instanceof Player ? 
            ((Player) nearestMob).getName() : 
            nearestMob.getType().name();
        Bukkit.broadcastMessage(ChatColor.GOLD + "💥 " + player.getName() + " が" + mobName + "に「マルファイトのULT」を発動！");
        
        return "最も近いモブに突進しました";
    }
    
    private LivingEntity findNearestMob(Player player, double radius) {
        LivingEntity nearest = null;
        double nearestDistance = radius + 1;
        
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(
            player.getLocation(), radius, radius, radius);
        
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity && entity != player) {
                double distance = player.getLocation().distance(entity.getLocation());
                if (distance < nearestDistance) {
                    nearest = (LivingEntity) entity;
                    nearestDistance = distance;
                }
            }
        }
        
        return nearest;
    }
    
    private void rushToTarget(Player player, LivingEntity target, Location targetLoc) {
        Location startLoc = player.getLocation().clone();
        
        player.sendMessage(ChatColor.GOLD + "💥 マルファイトのULT発動！突進します！");
        
        // 派手なエフェクト
        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            startLoc,
            20, 2, 2, 2, 0.1
        );
        player.getWorld().playSound(startLoc, Sound.ENTITY_WITHER_SPAWN, 2.0f, 0.5f);
        
        // 突進アニメーション
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 15;
            final Vector velocity = targetLoc.toVector().subtract(startLoc.toVector()).normalize().multiply(1.5);
            Location currentLoc = startLoc.clone();
            
            @Override
            public void run() {
                if (ticks >= maxTicks || currentLoc.distance(targetLoc) < 2.0) {
                    // 着地処理
                    performLanding(player, target, targetLoc);
                    this.cancel();
                    return;
                }
                
                // 移動中の5x5地形破壊
                destroyTerrain(currentLoc, 2);
                
                // 突進中のエフェクト
                currentLoc.getWorld().spawnParticle(
                    Particle.DUST,
                    currentLoc,
                    10, 0.5, 0.5, 0.5, 0.1,
                    new Particle.DustOptions(Color.ORANGE, 2.0f)
                );
                
                currentLoc.add(velocity);
                player.teleport(currentLoc);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    private void performLanding(Player player, LivingEntity target, Location targetLoc) {
        // 着地地点の半径10x10ブロック爆破
        destroyTerrain(targetLoc, 5);
        
        // 着地エフェクト
        targetLoc.getWorld().spawnParticle(
            Particle.EXPLOSION,
            targetLoc,
            30, 3, 3, 3, 0.2
        );
        targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_GENERIC_EXPLODE, 3.0f, 0.8f);
        
        // ダメージと効果適用
        if (target instanceof Player) {
            Player targetPlayer = (Player) target;
            // プレイヤーには1ダメージ、お互いに耐性V
            targetPlayer.damage(1.0, player);
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4)); // 5秒間耐性V
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4));
            
            player.sendMessage(ChatColor.GREEN + "プレイヤーに突進！お互いに5秒間の耐性Vを獲得しました。");
        } else {
            // モブには100❤ダメージ、自身に耐性V
            target.damage(200.0, player); // 100❤ = 200HP
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4));
            
            player.sendMessage(ChatColor.GREEN + "モブに大ダメージ！5秒間の耐性Vを獲得しました。");
        }
        
        // 周囲への追加エフェクト
        targetLoc.getWorld().spawnParticle(
            Particle.LAVA,
            targetLoc,
            50, 4, 2, 4, 0.1
        );
    }
    
    private void destroyTerrain(Location center, int radius) {
        int blocksDestroyed = 0;
        final int maxBlocks = 100; // パフォーマンス制限
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (blocksDestroyed >= maxBlocks) return; // パフォーマンス保護
                    
                    Location blockLoc = center.clone().add(x, y, z);
                    Block block = blockLoc.getBlock();
                    
                    // 黒曜石含む全ブロック破壊
                    if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        block.setType(Material.AIR);
                        blocksDestroyed++;
                        
                        // パーティクル削減（5分の1に）
                        if (blocksDestroyed % 5 == 0) {
                            blockLoc.getWorld().spawnParticle(
                                Particle.BLOCK,
                                blockLoc.add(0.5, 0.5, 0.5),
                                2, 0.2, 0.2, 0.2, 0.05,
                                block.getBlockData()
                            );
                        }
                    }
                }
            }
        }
    }
}