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

public class BombThrowEffect extends UnluckyEffectBase {

    private final Random random = new Random();

    public BombThrowEffect(JavaPlugin plugin) {
        super(plugin, "爆弾投擲", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player thrower) {
        // ターゲットを選択（他のプレイヤーがいなければ自分自身）
        Player target = selectTarget(thrower);
        
        thrower.sendMessage(ChatColor.RED + "あなたは爆弾を投げつけることになりました！");
        thrower.playSound(thrower.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);

        // 5秒後の投擲を全体チャットで通知
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💣 " + ChatColor.YELLOW + thrower.getName() + 
                ChatColor.GRAY + " が " + ChatColor.RED + target.getName() + 
                ChatColor.GRAY + " に向けて爆弾を投げる準備をしています！");
        Bukkit.broadcastMessage(ChatColor.RED + "5秒後に爆弾が投擲されます！対象者は回避行動を取ってください！");
        Bukkit.broadcastMessage("");

        // 全員に警告音
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
        }

        // 5秒後に爆弾投擲を実行
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!thrower.isOnline() || !target.isOnline()) {
                    Bukkit.broadcastMessage(ChatColor.GRAY + "爆弾投擲がキャンセルされました（プレイヤーがオフライン）");
                    return;
                }
                
                executeBombThrow(thrower, target);
            }
        }.runTaskLater(plugin, 100L); // 5秒後

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

    private void executeBombThrow(Player thrower, Player target) {
        Location targetLoc = target.getLocation();
        
        // ターゲットの頭上20ブロックにTNTを生成
        Location bombSpawnLoc = targetLoc.clone().add(0, 20, 0);
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💥 " + ChatColor.YELLOW + thrower.getName() + 
                ChatColor.GRAY + " が爆弾を投擲しました！ " + ChatColor.RED + target.getName() + 
                ChatColor.GRAY + " の頭上にTNTが落下中！");

        // 全員に爆弾投擲音
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);
        }

        // TNTを生成（4秒のヒューズ）
        TNTPrimed tnt = target.getWorld().spawn(bombSpawnLoc, TNTPrimed.class);
        tnt.setFuseTicks(80); // 4秒（80ticks）
        tnt.setVelocity(org.bukkit.util.Vector.getRandom().multiply(0.1).setY(-0.3)); // 軽いランダム性

        // 落下中の警告エフェクト
        new BukkitRunnable() {
            int countdown = 4;
            
            @Override
            public void run() {
                if (!tnt.isValid() || countdown <= 0) {
                    this.cancel();
                    return;
                }
                
                // カウントダウン表示
                target.sendTitle(
                    ChatColor.RED + "⚠ 危険 ⚠",
                    ChatColor.YELLOW + "爆発まで " + ChatColor.RED + countdown + ChatColor.YELLOW + " 秒！",
                    5, 20, 5
                );
                
                // 警告音
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
                }
                
                // 危険範囲のパーティクル表示
                Location currentLoc = tnt.getLocation();
                for (int i = 0; i < 8; i++) {
                    double angle = (i * 45) * Math.PI / 180;
                    double x = Math.cos(angle) * 4;
                    double z = Math.sin(angle) * 4;
                    
                    currentLoc.getWorld().spawnParticle(
                        Particle.DUST,
                        currentLoc.clone().add(x, -10, z),
                        1, 0, 0, 0, 0,
                        new Particle.DustOptions(Color.RED, 1.0f)
                    );
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L); // 1秒間隔

        // 爆発予告のパーティクル
        target.getWorld().spawnParticle(
            Particle.EXPLOSION,
            bombSpawnLoc,
            5, 1, 1, 1, 0.1
        );
    }
}