package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 頼りない回復
 * 体力を1(0.5❤)回復し、緩衝体力1(0.5❤)を10秒つける
 */
public class WeakRecoveryEffect extends LuckyEffectBase {

    public WeakRecoveryEffect(JavaPlugin plugin) {
        super(plugin, "頼りない回復", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        // 現在の体力と最大体力を取得
        double currentHealth = player.getHealth();
        double maxHealth = player.getMaxHealth();
        
        // 体力を1(0.5❤)回復（最大体力を超えないように）
        double newHealth = Math.min(currentHealth + 1.0, maxHealth);
        player.setHealth(newHealth);
        
        // 緩衝体力1(0.5❤)を10秒間付与（Absorption I for 10 seconds）
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0)); // 200tick = 10秒, レベル1
        
        // メッセージ
        player.sendMessage(ChatColor.GREEN + "✨ 少しだけ体力が回復しました！");
        player.sendMessage(ChatColor.YELLOW + "🛡 緩衝体力も少し付与されました (10秒間)");
        
        // 回復エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.5f);
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 1.5, 0),
            3, 0.5, 0.5, 0.5, 0.1
        );
        
        // 緩衝体力のエフェクト
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            5, 0.3, 0.3, 0.3, 0.05
        );
        
        return getDescription();
    }
}