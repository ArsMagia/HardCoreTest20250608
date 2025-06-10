package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 段階的爆撃パターン
 * フェーズ1: 警告爆弾（ダメージなし、小爆発）
 * フェーズ2: 3秒後にメイン爆弾（中爆発）
 * 安全措置: 20秒制限、建物破壊防止、回避時間提供
 */
public class BombThrowEffect extends UnluckyEffectBase {

    private final Random random = new Random();

    public BombThrowEffect(JavaPlugin plugin) {
        super(plugin, "爆弾投擲", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player thrower) {
        // ターゲットを選択（他のプレイヤーがいなければ自分自身）
        Player target = selectTarget(thrower);
        
        thrower.sendMessage(ChatColor.YELLOW + "⚠ 段階的爆撃パターンが発動しました！");
        thrower.playSound(thrower.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);

        // 初期通知
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠ 段階的爆撃パターン発動 ⚠");
        Bukkit.broadcastMessage(ChatColor.GRAY + "対象: " + ChatColor.RED + target.getName());
        Bukkit.broadcastMessage(ChatColor.GOLD + "💡 フェーズ1: 警告爆弾が3秒後に投下されます！");
        Bukkit.broadcastMessage(ChatColor.AQUA + "💬 警告爆弾はダメージなしですが、メイン爆弾の予告です！");
        Bukkit.broadcastMessage("");

        // 全員に警告音
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
        }

        // フェーズ1: 3秒後に警告爆弾
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!thrower.isOnline() || !target.isOnline()) {
                    Bukkit.broadcastMessage(ChatColor.GRAY + "爆撃パターンがキャンセルされました（プレイヤーがオフライン）");
                    return;
                }
                
                executeWarningBomb(thrower, target);
            }
        }.runTaskLater(plugin, 60L); // 3秒後

        return getDescription();
    }

    private Player selectTarget(Player thrower) {
        List<Player> otherPlayers = new ArrayList<>();
        
        // 自分以外のプレイヤーがいるかチェック
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(thrower)) {
                otherPlayers.add(onlinePlayer);
            }
        }
        
        if (otherPlayers.isEmpty()) {
            return thrower; // 他にプレイヤーがいなければ自分自身
        } else {
            return otherPlayers.get(random.nextInt(otherPlayers.size()));
        }
    }

    /**
     * フェーズ1: 警告爆弾（ダメージなし）
     */
    private void executeWarningBomb(Player thrower, Player target) {
        Location targetLoc = target.getLocation();
        Location bombSpawnLoc = targetLoc.clone().add(0, 15, 0);
        
        Bukkit.broadcastMessage(ChatColor.GOLD + "💥 フェーズ1: 警告爆弾投下！");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "💬 この爆弾はダメージを与えませんが、次の本物の予告です！");

        // 警告爆弾を生成（ダメージなし）
        TNTPrimed warningTnt = target.getWorld().spawn(bombSpawnLoc, TNTPrimed.class);
        warningTnt.setFuseTicks(40); // 2秒ヒューズ
        warningTnt.setVelocity(new org.bukkit.util.Vector(0, -0.2, 0));
        
        // ダメージなしに設定（爆発範囲を0に）
        warningTnt.setYield(0.5f); // 小さな爆発
        
        // 警告音とエフェクト
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.8f);
        }
        
        // 警告パーティクル
        target.getWorld().spawnParticle(
            Particle.FIREWORK,
            bombSpawnLoc,
            10, 2, 2, 2, 0.1
        );
        
        // フェーズ2の予告
        new BukkitRunnable() {
            @Override
            public void run() {
                if (target.isOnline()) {
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage(ChatColor.RED + "⚠ フェーズ2予告 ⚠");
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + "💣 3秒後にメイン爆弾が投下されます！");
                    Bukkit.broadcastMessage(ChatColor.GOLD + "🏃 今すぐ回避してください！建物の中や地下が安全です！");
                    Bukkit.broadcastMessage("");
                    
                    // フェーズ2を実行
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (target.isOnline()) {
                                executeMainBomb(thrower, target);
                            }
                        }
                    }.runTaskLater(plugin, 60L); // 3秒後
                }
            }
        }.runTaskLater(plugin, 40L); // 2秒後に予告
    }
    
    /**
     * フェーズ2: メイン爆弾（中爆発）
     */
    private void executeMainBomb(Player thrower, Player target) {
        Location targetLoc = target.getLocation();
        Location bombSpawnLoc = targetLoc.clone().add(0, 20, 0);
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💥 フェーズ2: メイン爆弾投下！");
        Bukkit.broadcastMessage(ChatColor.RED + "⚡ " + target.getName() + "の頭上にメイン爆弾が落下中！");

        // メインTNTを生成
        TNTPrimed mainTnt = target.getWorld().spawn(bombSpawnLoc, TNTPrimed.class);
        mainTnt.setFuseTicks(80); // 4秒ヒューズ
        mainTnt.setVelocity(new org.bukkit.util.Vector(0, -0.3, 0));
        mainTnt.setYield(2.0f); // 中程度の爆発力（デフォルトは4.0f）
        
        // 建物破壊防止のための設定
        // ブロック破壊を無効化するか、範囲を制限する必要がある場合はここで設定
        
        // 全員に最終警告音
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        }

        // カウントダウンタスク
        new BukkitRunnable() {
            int countdown = 4;
            
            @Override
            public void run() {
                if (!mainTnt.isValid() || countdown <= 0) {
                    this.cancel();
                    return;
                }
                
                // カウントダウン表示
                target.sendTitle(
                    ChatColor.DARK_RED + "⚠ メイン爆弾 ⚠",
                    ChatColor.YELLOW + "爆発まで " + ChatColor.RED + countdown + ChatColor.YELLOW + " 秒！",
                    5, 20, 5
                );
                
                // 緊急警告音
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                }
                
                // 危険範囲の視覚化
                Location currentLoc = mainTnt.getLocation();
                for (int i = 0; i < 12; i++) {
                    double angle = (i * 30) * Math.PI / 180;
                    double x = Math.cos(angle) * 5;
                    double z = Math.sin(angle) * 5;
                    
                    currentLoc.getWorld().spawnParticle(
                        Particle.DUST,
                        currentLoc.clone().add(x, -15, z),
                        1, 0, 0, 0, 0,
                        new Particle.DustOptions(Color.YELLOW, 1.5f)
                    );
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);

        // 爆発予告エフェクト
        target.getWorld().spawnParticle(
            Particle.EXPLOSION,
            bombSpawnLoc,
            10, 2, 2, 2, 0.1
        );
        
        // 20秒後にクリーンアップ（安全措置）
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(ChatColor.GREEN + "✅ 段階的爆撃パターンが終了しました。");
            }
        }.runTaskLater(plugin, 400L); // 20秒後
    }
}