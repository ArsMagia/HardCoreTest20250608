package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * ダイナミックチキン化
 * - 20秒間のチキンモード
 * - チキン状態中は特殊音効とパーティクル追加
 * - 他プレイヤーからチキンとして見える視覚効果
 * - 段階的解除システム
 */
public class ChickenTransformEffect extends UnluckyEffectBase {

    private final Random random = new Random();
    
    public ChickenTransformEffect(JavaPlugin plugin) {
        super(plugin, "チキン化", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // チキン化効果を適用（20秒間）
        int duration = 400; // 20秒間
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, duration, 255));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, duration, 2)); // レベル下げ
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 0)); // レベル下げ
        
        // 初期通知とエフェクト
        player.sendMessage(ChatColor.YELLOW + "🐔 チキンに変身してしまいました！20秒間、移動以外何もできません。");
        
        // 初期音効とパーティクル
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 1.0f, 1.0f);
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 2, 0),
            10, 1, 1, 1, 0.1
        );
        
        // 全員に通知
        Bukkit.broadcastMessage(ChatColor.YELLOW + "🐔 " + player.getName() + "がチキンに変身しました！");
        
        // ダイナミックエフェクトタスク
        new BukkitRunnable() {
            int counter = 0;
            final int maxDuration = 20; // 20秒
            
            @Override
            public void run() {
                if (counter >= maxDuration || !player.isOnline()) {
                    finishChickenTransform(player);
                    this.cancel();
                    return;
                }
                
                // 定期的なチキン音（2秒ごと）
                if (counter % 2 == 0) {
                    float pitch = 0.8f + random.nextFloat() * 0.4f; // 0.8-1.2のランダムピッチ
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 0.6f, pitch);
                    
                    // チキンパーティクル
                    player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        player.getLocation().add(0, 1.5, 0),
                        3, 0.3, 0.3, 0.3, 0.02
                    );
                }
                
                // 特別エフェクトＨ5秒ごと）
                if (counter % 5 == 0 && counter > 0) {
                    createChickenEffect(player);
                }
                
                // カウントダウン表示（最後5秒）
                if (counter >= maxDuration - 5) {
                    int remaining = maxDuration - counter;
                    player.sendTitle(
                        "",
                        ChatColor.YELLOW + "🐔 変身解除まで " + remaining + " 秒",
                        0, 25, 5
                    );
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
    
    /**
     * チキン特別エフェクトを作成
     */
    private void createChickenEffect(Player player) {
        Location loc = player.getLocation();
        
        // 羽毛パーティクルリング
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI * 2 / 8;
            Location ringLoc = loc.clone().add(
                Math.cos(angle) * 1.5,
                1,
                Math.sin(angle) * 1.5
            );
            
            player.getWorld().spawnParticle(
                Particle.WHITE_ASH,
                ringLoc,
                1, 0, 0, 0, 0
            );
        }
        
        // ランダムなチキンメッセージ
        String[] chickenMessages = {
            "🐔 コケコッコー！",
            "🐔 羽ばたきしたい気分です...",
            "🐔 高いところに登りたい！",
            "🐔 エサが欲しい..."
        };
        
        if (random.nextInt(100) < 30) { // 30%の確率
            String message = chickenMessages[random.nextInt(chickenMessages.length)];
            player.sendMessage(ChatColor.YELLOW + message);
        }
    }
    
    /**
     * チキン変身終了処理
     */
    private void finishChickenTransform(Player player) {
        if (!player.isOnline()) return;
        
        player.sendMessage(ChatColor.GREEN + "✅ 人間の姿に戻りました！");
        player.sendMessage(ChatColor.GRAY + "「あれは一体何だったんだ...」");
        
        // 終了エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        
        // 変身解除パーティクル
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.3
        );
        
        // 光の柱エフェクト
        Location playerLoc = player.getLocation();
        for (int y = 0; y < 5; y++) {
            playerLoc.getWorld().spawnParticle(
                Particle.END_ROD,
                playerLoc.clone().add(0, y * 0.5, 0),
                1, 0, 0, 0, 0
            );
        }
        
        // 全員に解除通知
        Bukkit.broadcastMessage(ChatColor.GREEN + "✨ " + player.getName() + "が人間の姿に戻りました！");
    }
}