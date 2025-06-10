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

/**
 * 3回発動毒雨システム
 * - 20秒間で7秒間隔で3回発動
 * - ポーション種類をランダム化（毒・弱体化・遅化・盲目）
 * - 全プレイヤー対象で回避手段あり
 * - 3秒の事前警告で回避時間提供
 */
public class PotionRainEffect extends UnluckyEffectBase {

    private final Random random = new Random();

    public PotionRainEffect(JavaPlugin plugin) {
        super(plugin, "毒雨", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "⚠ 災いの雨雲が集結しました！");
        player.sendMessage(ChatColor.YELLOW + "🌧️ 20秒間で7秒間隔で3回、危険なポーションが降り注ぎます！");
        player.sendMessage(ChatColor.GOLD + "🏠 建物の中に逃げてください！");
        
        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 0.8f);
        
        // 初期エフェクト
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 5, 0),
            30, 3, 2, 3, 0.1,
            new Particle.DustOptions(Color.PURPLE, 1.5f)
        );

        // 3回発動システム開始
        new BukkitRunnable() {
            int cycles = 0;
            final int maxCycles = 3; // 3回発動
            
            @Override
            public void run() {
                if (cycles >= maxCycles) {
                    finishPotionRain(player);
                    this.cancel();
                    return;
                }
                
                // 各サイクルで警告と雨をスケジュール
                scheduleWarningAndRain(player, cycles + 1, maxCycles);
                cycles++;
            }
        }.runTaskTimer(plugin, 0L, 140L); // 7秒間隔

        return getDescription();
    }

    /**
     * 警告とポーション雨をスケジュール
     */
    private void scheduleWarningAndRain(Player player, int currentWave, int totalWaves) {
        // 3秒前の警告
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.DARK_RED + "⚠ 毒雨警告 ⚠");
                Bukkit.broadcastMessage(ChatColor.RED + "🌧️ 第" + currentWave + "/" + totalWaves + "波: 3秒後に全プレイヤーの頭上に毒ポーションが降り注ぎます！");
                Bukkit.broadcastMessage(ChatColor.GOLD + "🏠 建物の中や屋根下に避難してください！");
                Bukkit.broadcastMessage("");
                
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
                    
                    // 警告パーティクルエフェクト
                    p.getWorld().spawnParticle(
                        Particle.DUST,
                        p.getLocation().add(0, 4, 0),
                        15, 2, 1, 2, 0.1,
                        new Particle.DustOptions(Color.MAROON, 1.2f)
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
                        executeRain(currentWave);
                    }
                }.runTaskLater(plugin, 60L); // 3秒後
            }
        }.runTask(plugin);
    }

    /**
     * ランダムポーション雨を実行
     */
    private void executeRain(int waveNumber) {
        // 屋根下保護チェック
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.isOnline()) continue;
            
            // 完全保護チェック
            if (isPlayerProtectedFromRain(target)) {
                target.sendMessage(ChatColor.GREEN + "✅ 建物によって毒雨を回避しました！");
                continue;
            }
            
            // ポーションを生成・投下
            dropRandomPotion(target, waveNumber);
        }
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "☠ 第" + waveNumber + "波の毒雨が降り注いでいます！");
    }
    
    /**
     * プレイヤーが毒雨から保護されているかチェック
     */
    private boolean isPlayerProtectedFromRain(Player player) {
        Location loc = player.getLocation();
        
        // 屋根チェック（頭上3ブロックをチェック）
        for (int y = 1; y <= 3; y++) {
            if (loc.clone().add(0, y, 0).getBlock().getType().isSolid()) {
                return true;
            }
        }
        
        // 水中チェック
        if (loc.getBlock().getType() == Material.WATER) {
            return true;
        }
        
        return false;
    }
    
    /**
     * ランダムポーションを投下
     */
    private void dropRandomPotion(Player target, int waveNumber) {
        Location dropLoc = target.getLocation().add(
            (random.nextDouble() - 0.5) * 3, // ランダム散布
            12, 
            (random.nextDouble() - 0.5) * 3
        );
        
        // ランダムポーション種類選択
        ItemStack potionItem = createRandomBadPotion();
        
        // ポーションを投下
        ThrownPotion thrownPotion = target.getWorld().spawn(dropLoc, ThrownPotion.class);
        thrownPotion.setItem(potionItem);
        
        // 自然な落下ベクトル
        org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(
            (random.nextDouble() - 0.5) * 0.1,
            -0.3,
            (random.nextDouble() - 0.5) * 0.1
        );
        thrownPotion.setVelocity(velocity);
        
        // 投下音（確率的）
        if (random.nextInt(100) < 30) {
            target.getWorld().playSound(dropLoc, Sound.ENTITY_SPLASH_POTION_THROW, 0.4f, 1.0f);
        }
    }
    
    /**
     * ランダムな悪性ポーションを作成
     */
    private ItemStack createRandomBadPotion() {
        // 4種類の悪性ポーションからランダム選択
        int potionType = random.nextInt(4);
        ItemStack potionItem;
        
        switch (potionType) {
            case 0: // 毒ポーション
                potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta poisonMeta = (PotionMeta) potionItem.getItemMeta();
                if (poisonMeta != null) {
                    poisonMeta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60, 0), true); // 3秒間
                    potionItem.setItemMeta(poisonMeta);
                }
                break;
                
            case 1: // 弱体化ポーション
                potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta weaknessMeta = (PotionMeta) potionItem.getItemMeta();
                if (weaknessMeta != null) {
                    weaknessMeta.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0), true); // 5秒間
                    potionItem.setItemMeta(weaknessMeta);
                }
                break;
                
            case 2: // 遅化ポーション
                potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta slownessMeta = (PotionMeta) potionItem.getItemMeta();
                if (slownessMeta != null) {
                    slownessMeta.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 0), true); // 4秒間
                    potionItem.setItemMeta(slownessMeta);
                }
                break;
                
            default: // 盲目ポーション
                potionItem = new ItemStack(Material.SPLASH_POTION);
                PotionMeta blindnessMeta = (PotionMeta) potionItem.getItemMeta();
                if (blindnessMeta != null) {
                    blindnessMeta.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0), true); // 3秒間
                    potionItem.setItemMeta(blindnessMeta);
                }
                break;
        }
        
        return potionItem;
    }
    
    /**
     * 毒雨終了処理
     */
    private void finishPotionRain(Player player) {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GREEN + "✨ 災いの雨雲が去っていきました...");
        Bukkit.broadcastMessage(ChatColor.GRAY + "空が晴れてきました。");
        Bukkit.broadcastMessage("");
        
        // 終了エフェクト
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.WEATHER_RAIN_ABOVE, 0.8f, 1.2f);
            p.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                p.getLocation().add(0, 2, 0),
                10, 2, 1, 2, 0.1
            );
        }
    }
}