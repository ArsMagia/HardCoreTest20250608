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
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.isOnline()) continue;
            
            Location loc = target.getLocation().add(0, 20, 0);
            
            // ランダムでDamage Splash または Poison Linger
            ItemStack potionItem;
            if (random.nextBoolean()) {
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
                    meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 200, 0), true);
                    potionItem.setItemMeta(meta);
                }
            }
            
            // ポーションを落下させる
            ThrownPotion thrownPotion = target.getWorld().spawn(loc, ThrownPotion.class);
            thrownPotion.setItem(potionItem);
            
            // より確実な落下ベクトルを設定
            org.bukkit.util.Vector velocity = new org.bukkit.util.Vector(
                (random.nextDouble() - 0.5) * 0.2, // X軸のランダム性
                -0.3, // 下向きの速度
                (random.nextDouble() - 0.5) * 0.2  // Z軸のランダム性
            );
            thrownPotion.setVelocity(velocity);
            
            // 落下音とパーティクル
            target.getWorld().playSound(loc, Sound.ENTITY_SPLASH_POTION_THROW, 1.0f, 1.0f);
        }
        
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "☠ 毒雨が降り注いでいます！");
    }
}