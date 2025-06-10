package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class AnnoyingYoutuberEffect extends UnluckyEffectBase {
    private static final Set<BukkitTask> activeTasks = new HashSet<>();
    private static final String[] ANNOYING_PHRASES = {
        "チャンネル登録よろしくお願いします！",
        "高評価ボタンも押してね！",
        "コメント欄で教えてください！",
        "次回もお楽しみに！",
        "今日の動画はいかがでしたか？",
        "ベルマークも忘れずに！",
        "概要欄にリンク貼っておきます！",
        "みなさんこんにちは〜！"
    };

    public AnnoyingYoutuberEffect(JavaPlugin plugin) {
        super(plugin, "ご迷惑をおかけします", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // 迷惑系Youtuberらしいセリフを言う
        String phrase = ANNOYING_PHRASES[new Random().nextInt(ANNOYING_PHRASES.length)];
        
        // プレイヤーのセリフを全体チャットで表示
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + ": " + 
                                ChatColor.LIGHT_PURPLE + "「" + phrase + "」");
        
        player.sendMessage(ChatColor.RED + "⚠ 5秒後に矢の嵐が始まります！");
        
        // 警告エフェクト
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
        
        // 5秒後に矢の嵐開始
        BukkitTask warningTask = new BukkitRunnable() {
            @Override
            public void run() {
                startArrowStorm(player);
                activeTasks.remove(this);
            }
        }.runTaskLater(plugin, 100L); // 5秒 = 100 tick
        
        activeTasks.add(warningTask);
        
        return "迷惑系Youtuberの矢の嵐を発動しました";
    }

    private void startArrowStorm(Player player) {
        if (!player.isOnline()) return;
        
        player.sendMessage(ChatColor.DARK_RED + "💀 矢の嵐が始まりました！");
        Bukkit.broadcastMessage(ChatColor.RED + "⚠ " + player.getName() + " の周囲で矢の嵐が発生しています！");
        
        // 10秒間の矢の嵐（効果時間を半分に短縮）
        BukkitTask stormTask = new BukkitRunnable() {
            int ticks = 0;
            final int DURATION_TICKS = 200; // 10秒（20秒から半分に短縮）
            final Random random = new Random();
            
            @Override
            public void run() {
                if (ticks >= DURATION_TICKS || !player.isOnline()) {
                    // 終了エフェクト
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GREEN + "✨ 矢の嵐が終了しました。");
                        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + " 周囲の矢の嵐が終了しました。");
                    }
                    this.cancel();
                    activeTasks.remove(this);
                    return;
                }
                
                // プレイヤーの現在位置を取得（移動に追従）
                Location currentPlayerLoc = player.getLocation();
                
                // 半径30ブロック以内の全てのエンティティを取得（プレイヤーの現在位置基準）
                Collection<Entity> nearbyEntities = currentPlayerLoc.getWorld().getNearbyEntities(currentPlayerLoc, 30, 30, 30);
                
                // プレイヤーと他のエンティティを分離
                List<Player> nearbyPlayers = new ArrayList<>();
                List<LivingEntity> otherEntities = new ArrayList<>();
                
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity && entity != player) {
                        if (entity instanceof Player) {
                            nearbyPlayers.add((Player) entity);
                        } else {
                            otherEntities.add((LivingEntity) entity);
                        }
                    }
                }
                
                // 最優先でプレイヤーを狙う
                for (Player targetPlayer : nearbyPlayers) {
                    if (random.nextDouble() < 0.5) { // プレイヤーには高確率で攻撃
                        shootArrowAtTarget(currentPlayerLoc, targetPlayer);
                    }
                }
                
                // プレイヤーがいない場合のみ他のエンティティを狙う
                if (nearbyPlayers.isEmpty()) {
                    for (LivingEntity entity : otherEntities) {
                        // ランダムで矢を発射（確率を少し下げて発動スパンを調整）
                        if (random.nextDouble() < 0.25) { // 25%の確率（30%から調整）
                            shootArrowAtTarget(currentPlayerLoc, entity);
                        }
                    }
                }
                
                // 特に激しい攻撃（1.5秒ごとに変更）
                if (ticks % 30 == 0) { // 1.5秒ごと（1秒から調整）
                    // プレイヤーを最優先で集中攻撃
                    for (Player targetPlayer : nearbyPlayers) {
                        if (random.nextDouble() < 0.9) { // プレイヤーには90%の確率で攻撃
                            shootArrowAtTarget(currentPlayerLoc, targetPlayer);
                        }
                    }
                    
                    // プレイヤーがいない場合のみ他のエンティティに集中攻撃
                    if (nearbyPlayers.isEmpty()) {
                        for (LivingEntity entity : otherEntities) {
                            if (random.nextDouble() < 0.7) { // 70%の確率（80%から調整）
                                shootArrowAtTarget(currentPlayerLoc, entity);
                            }
                        }
                    }
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L); // 2tickごと実行（発動スパンを遅くする）
        
        activeTasks.add(stormTask);
    }

    private void shootArrowAtTarget(Location playerLocation, LivingEntity target) {
        // 発動プレイヤーの現在位置から少し上（目線の高さ）から射出
        Location shootLocation = playerLocation.clone().add(0, 1.6, 0);
        
        // ターゲットの位置にY座標+1して狙う（モブの胴体を狙う）
        Location targetLocation = target.getLocation().add(0, 1, 0);
        
        // 距離を計算
        double distance = shootLocation.distance(targetLocation);
        
        // ターゲットに向けて矢を発射（重力補正を含む）
        Vector direction = targetLocation.subtract(shootLocation).toVector();
        
        // 重力による落下を補正するため、Y軸に追加の上向き成分を加える
        double horizontalDistance = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
        double timeToTarget = horizontalDistance / 2.0; // 矢の水平速度を2.0と仮定
        double gravityCompensation = 0.5 * 0.05 * timeToTarget * timeToTarget; // 重力補正（重力加速度 ≈ 0.05）
        direction.setY(direction.getY() + gravityCompensation);
        
        // 正規化して適切な速度を設定
        direction = direction.normalize();
        direction.multiply(2.0); // 矢の速度を上げる（1.5 → 2.0）
        
        Arrow arrow = playerLocation.getWorld().spawnArrow(shootLocation, direction, 1.2f, 0.02f); // 力と精度を調整
        arrow.setDamage(0.5); // 0.5ダメージ
        arrow.setCritical(true);
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        
        // 射出エフェクト（発動プレイヤーから撃っているように見せる）
        shootLocation.getWorld().spawnParticle(Particle.CRIT, shootLocation, 3, 0.2, 0.2, 0.2, 0.1);
        
        // 矢の状態監視と地面着地後の削除タスク
        new BukkitRunnable() {
            private boolean hasLanded = false;
            private int landedTicks = 0;
            
            @Override
            public void run() {
                if (arrow.isDead() || !arrow.isValid()) {
                    this.cancel();
                    return;
                }
                
                Location arrowLoc = arrow.getLocation();
                
                // 矢が地面に着地したかチェック（矢が動いていない状態）
                if (!hasLanded && arrow.isInBlock()) {
                    hasLanded = true;
                    landedTicks = 0;
                }
                
                // 着地後の時間をカウント
                if (hasLanded) {
                    landedTicks++;
                    // 1秒（20tick）後に矢を削除
                    if (landedTicks >= 20) {
                        arrow.remove();
                        this.cancel();
                        return;
                    }
                }
                
                // 飛行中はパーティクルエフェクト表示
                if (!hasLanded) {
                    arrowLoc.getWorld().spawnParticle(Particle.CRIT, arrowLoc, 2, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L); // 毎tick監視
        
        // 矢の射出音効果（発動プレイヤーの現在位置から）
        if (Math.random() < 0.15) { // 15%の確率で音
            playerLocation.getWorld().playSound(playerLocation, Sound.ENTITY_ARROW_SHOOT, 0.4f, 1.0f + (float)(Math.random() * 0.5));
        }
        
        // 最大生存時間による強制削除（5秒）- フェイルセーフ
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!arrow.isDead() && arrow.isValid()) {
                    arrow.remove();
                }
            }
        }.runTaskLater(plugin, 100L);
    }

    public static void cleanup() {
        for (BukkitTask task : activeTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();
    }
}