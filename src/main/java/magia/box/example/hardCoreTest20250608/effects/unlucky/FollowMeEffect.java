package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FollowMeEffect extends UnluckyEffectBase {
    private static final Set<BukkitTask> activeTasks = new HashSet<>();
    private static final Set<UUID> affectedPlayers = new HashSet<>();

    public FollowMeEffect(JavaPlugin plugin) {
        super(plugin, "俺についてこい", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "💀 「俺についてこい」が発動しました！");
        player.sendMessage(ChatColor.GRAY + "20秒間、周囲のプレイヤーを強制的に引き寄せます...");
        
        Bukkit.broadcastMessage(ChatColor.RED + "⚠ " + player.getName() + " の「俺についてこい」が発動！");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "半径30ブロック以内のプレイヤーは20秒間強制的に引き寄せられます！");
        
        // 効果開始音
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
        
        // 20秒間の強制移動効果
        BukkitTask forceTask = new BukkitRunnable() {
            int remainingTicks = 400; // 20秒 = 400 tick
            
            @Override
            public void run() {
                if (remainingTicks <= 0 || !player.isOnline()) {
                    // 効果終了
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GREEN + "✨ 「俺についてこい」の効果が終了しました。");
                        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " の「俺についてこい」効果が終了しました。");
                    }
                    
                    // 全プレイヤーを解放
                    for (UUID uuid : affectedPlayers) {
                        Player affectedPlayer = Bukkit.getPlayer(uuid);
                        if (affectedPlayer != null && affectedPlayer.isOnline()) {
                            affectedPlayer.sendMessage(ChatColor.GREEN + "✨ 強制移動から解放されました！");
                        }
                    }
                    affectedPlayers.clear();
                    
                    this.cancel();
                    activeTasks.remove(this);
                    return;
                }
                
                forcePlayersToFollow(player);
                
                // 5秒ごとに残り時間を通知（リーダーと影響を受けるプレイヤー両方に）
                if (remainingTicks % 100 == 0) { // 5秒 = 100 tick
                    int remainingSeconds = remainingTicks / 20;
                    player.sendMessage(ChatColor.YELLOW + "「俺についてこい」残り時間: " + remainingSeconds + "秒");
                    
                    // 影響を受けているプレイヤーにも通知
                    for (UUID uuid : affectedPlayers) {
                        Player affectedPlayer = Bukkit.getPlayer(uuid);
                        if (affectedPlayer != null && affectedPlayer.isOnline()) {
                            affectedPlayer.sendMessage(ChatColor.RED + "強制移動残り時間: " + remainingSeconds + "秒");
                        }
                    }
                }
                
                remainingTicks--;
            }
        }.runTaskTimer(plugin, 0L, 1L); // 毎tick実行
        
        activeTasks.add(forceTask);
        
        // パーティクルエフェクト表示タスク
        BukkitTask particleTask = new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 400; // 20秒 = 400 tick
            
            @Override
            public void run() {
                if (ticks >= maxTicks || !player.isOnline()) { // 20秒で終了
                    this.cancel();
                    activeTasks.remove(this);
                    return;
                }
                
                showLeadershipAura(player);
                ticks += 5; // 5tick分進める（0.25秒間隔で実行されるため）
            }
        }.runTaskTimer(plugin, 0L, 5L); // 0.25秒ごと
        
        activeTasks.add(particleTask);
        
        return "「俺についてこい」効果が発動しました";
    }

    private void forcePlayersToFollow(Player leader) {
        Location leaderLoc = leader.getLocation();
        List<Player> nearbyPlayers = leaderLoc.getWorld().getPlayers();
        
        for (Player nearbyPlayer : nearbyPlayers) {
            if (nearbyPlayer == leader || !nearbyPlayer.isOnline()) continue;
            
            double distance = nearbyPlayer.getLocation().distance(leaderLoc);
            
            if (distance <= 30.0) { // 半径30ブロック以内
                if (!affectedPlayers.contains(nearbyPlayer.getUniqueId())) {
                    affectedPlayers.add(nearbyPlayer.getUniqueId());
                    nearbyPlayer.sendMessage(ChatColor.RED + "💀 " + leader.getName() + " に強制的に引き寄せられています！");
                }
                
                // プレイヤーをリーダーに向けて移動させる
                pullPlayerTowards(nearbyPlayer, leaderLoc);
            } else {
                // 範囲外に出た場合は解放
                if (affectedPlayers.contains(nearbyPlayer.getUniqueId())) {
                    affectedPlayers.remove(nearbyPlayer.getUniqueId());
                    nearbyPlayer.sendMessage(ChatColor.YELLOW + "範囲外に出たため、強制移動から一時的に解放されました。");
                }
            }
        }
    }

    private void pullPlayerTowards(Player player, Location targetLocation) {
        Location playerLoc = player.getLocation();
        double distance = playerLoc.distance(targetLocation);
        
        // 距離に応じて引き寄せる力を調整
        double pullStrength;
        if (distance > 20) {
            pullStrength = 0.8; // 遠い場合は強く
        } else if (distance > 10) {
            pullStrength = 0.5; // 中距離は中程度
        } else if (distance > 5) {
            pullStrength = 0.3; // 近い場合は弱く
        } else {
            pullStrength = 0.1; // 非常に近い場合は最小限
        }
        
        // リーダーに向かうベクトルを計算
        Vector direction = targetLocation.toVector().subtract(playerLoc.toVector()).normalize();
        direction.multiply(pullStrength);
        
        // Y軸の移動を制限（空中に浮かせすぎない）
        if (direction.getY() > 0.3) {
            direction.setY(0.3);
        } else if (direction.getY() < -0.5) {
            direction.setY(-0.5);
        }
        
        // ベロシティを適用
        player.setVelocity(player.getVelocity().add(direction));
        
        // 引き寄せエフェクト
        if (Math.random() < 0.1) { // 10%の確率でパーティクル
            player.getWorld().spawnParticle(Particle.ENCHANT,
                playerLoc, 5, 0.5, 0.5, 0.5, 0.5);
        }
        
        // 稀に音効果
        if (Math.random() < 0.05) { // 5%の確率
            player.getWorld().playSound(playerLoc, Sound.BLOCK_BEACON_POWER_SELECT, 0.3f, 1.5f);
        }
    }

    private void showLeadershipAura(Player leader) {
        Location loc = leader.getLocation();
        World world = leader.getWorld();
        
        // リーダーの周りに威圧的なオーラを表示
        world.spawnParticle(Particle.DRAGON_BREATH, loc.clone().add(0, 1, 0), 8, 1, 1, 1, 0.05);
        world.spawnParticle(Particle.WITCH, loc.clone().add(0, 2, 0), 5, 0.8, 0.3, 0.8, 0.1);
        
        // 範囲を示すリング状のパーティクル
        double radius = 30.0;
        for (int i = 0; i < 16; i++) {
            double angle = (Math.PI * 2 * i) / 16;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            
            Location ringLoc = loc.clone().add(x, 0.5, z);
            world.spawnParticle(Particle.DUST, ringLoc, 1, 0, 0, 0, 0,
                new Particle.DustOptions(Color.PURPLE, 0.8f));
        }
        
        // 威圧的な音効果（稀に）
        if (Math.random() < 0.05) { // 5%の確率
            world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.3f, 1.2f);
        }
    }

    public static void cleanup() {
        for (BukkitTask task : activeTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();
        
        // 全プレイヤーを解放
        for (UUID uuid : affectedPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.sendMessage(ChatColor.GREEN + "✨ 強制移動から解放されました！");
            }
        }
        affectedPlayers.clear();
    }

    public static boolean isBeingForced(Player player) {
        return affectedPlayers.contains(player.getUniqueId());
    }
}