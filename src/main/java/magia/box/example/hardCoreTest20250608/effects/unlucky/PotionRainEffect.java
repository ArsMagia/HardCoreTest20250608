package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class PotionRainEffect extends UnluckyEffectBase {

    private final Random random = new Random();

    public PotionRainEffect(JavaPlugin plugin) {
        super(plugin, "毒雨", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "災いの雨雲が集まっています...20秒間、10秒ごとに危険なポーションが降り注ぎます！");
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.8f);

        // 1分間、10秒ごとにポーション雨を降らせる
        new BukkitRunnable() {
            int cycles = 0;
            
            @Override
            public void run() {
                if (cycles >= 2 || !player.isOnline()) { // 6回 = 1分間
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GRAY + "災いの雨雲が去っていきました...");
                    }
                    this.cancel();
                    return;
                }
                
                // 3秒前に警告
                scheduleWarningAndRain(player);
                cycles++;
            }
        }.runTaskTimer(plugin, 0L, 200L); // 10秒間隔

        return getDescription();
    }

    private void scheduleWarningAndRain(Player player) {
        // 3秒前の警告
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "⚠ 警告: " + ChatColor.RED + 
                    "3秒後に全プレイヤーの頭上に危険なポーションが降り注ぎます！建物に避難してください！");
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
                    p.getWorld().spawnParticle(
                        Particle.DUST,
                        p.getLocation().add(0, 3, 0),
                        20, 2, 1, 2, 0.1,
                        new Particle.DustOptions(Color.RED, 1.0f)
                    );
                }
                
                // 3秒後にポーション雨を実行
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isOnline()) {
                            this.cancel();
                            return;
                        }
                        executeRain();
                    }
                }.runTaskLater(plugin, 60L); // 3秒後
            }
        }.runTask(plugin);
    }

    private void executeRain() {
        int playersProcessed = 0;
        final int maxPotions = 8; // 最大ポーション数制限
        
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.isOnline() || playersProcessed >= maxPotions) {
                break; // パフォーマンス保護
            }
            
            Location loc = target.getLocation().add(0, 15, 0); // 高度を下げる
            
            // ランダムでDamage Splash または Poison Linger（確率調整）
            ItemStack potionItem;
            if (random.nextInt(100) < 60) { // 60%の確率でダメージポーション
                // Damage(Hurt) I Splash Potion
                potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
                if (meta != null) {
                    meta.setBasePotionType(PotionType.HARMING);
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), true);
                    potionItem.setItemMeta(meta);
                }
            } else {
                // Poison I Lingering Potion
                potionItem = new ItemStack(Material.LINGERING_POTION);
                PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
                if (meta != null) {
                    meta.setBasePotionType(PotionType.POISON);
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 0), true); // 持続時間削減
                    potionItem.setItemMeta(meta);
                }
            }
            
            // ポーションを落下させる
            ThrownPotion thrownPotion = target.getWorld().spawn(loc, ThrownPotion.class);
            thrownPotion.setItem(potionItem);
            
            // より確実な落下ベクトルを設定
            org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(
                (random.nextDouble() - 0.5) * 0.15, // ランダム性削減
                -0.4, // 落下速度上昇
                (random.nextDouble() - 0.5) * 0.15
            );
            thrownPotion.setVelocity(velocity);
            
            // 音響効果削減（4人に1人）
            if (playersProcessed % 4 == 0) {
                target.getWorld().playSound(loc, Sound.ENTITY_SPLASH_POTION_THROW, 0.5f, 1.0f);
            }
            
            playersProcessed++;
        }
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "☠ 毒雨が降り注いでいます！");
    }
}