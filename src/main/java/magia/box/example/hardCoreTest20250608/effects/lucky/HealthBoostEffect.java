package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HealthBoostEffect extends LuckyEffectBase {

    public HealthBoostEffect(JavaPlugin plugin) {
        super(plugin, "最大体力増加", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        if (!EffectUtils.isPlayerValid(player)) {
            return getDescription() + " (失敗)";
        }
        
        double currentMaxHealth = EffectUtils.getMaxHealth(player);
        if (currentMaxHealth <= 0) {
            player.sendMessage(ChatColor.RED + "体力情報を取得できませんでした。");
            return getDescription() + " (失敗)";
        }
        
        if (canIncreaseHealth(currentMaxHealth)) {
            return executeHealthIncrease(player, currentMaxHealth);
        } else {
            return executeFullHeal(player, currentMaxHealth);
        }
    }
    
    /**
     * 体力を増加できるかチェック
     */
    private boolean canIncreaseHealth(double currentMaxHealth) {
        return currentMaxHealth < EffectConstants.MAX_HEALTH_VALUE;
    }
    
    /**
     * 最大体力増加を実行
     */
    private String executeHealthIncrease(Player player, double currentMaxHealth) {
        double newMaxHealth = currentMaxHealth + EffectConstants.HEALTH_PER_HEART;
        if (!EffectUtils.setMaxHealthSafely(player, newMaxHealth)) {
            return getDescription() + " (失敗)";
        }
        
        // 現在体力も増加
        player.setHealth(Math.min(player.getHealth() + EffectConstants.HEALTH_PER_HEART, newMaxHealth));
        
        // メッセージとエフェクト
        int newHeartCount = (int)(newMaxHealth / EffectConstants.HEALTH_PER_HEART);
        player.sendMessage(ChatColor.RED + "最大体力が増加しました！現在: " + 
            ChatColor.DARK_RED + "❤ " + newHeartCount + " ハート");
        
        playSuccessEffects(player, 15);
        
        return getDescription() + " (+1❤)";
    }
    
    /**
     * 完全回復を実行
     */
    private String executeFullHeal(Player player, double maxHealth) {
        player.sendMessage(ChatColor.YELLOW + "最大体力が上限（" + 
            EffectConstants.MAX_HEALTH_HEARTS + "ハート）に達しています。代わりに完全回復します。");
        
        player.setHealth(maxHealth);
        playSuccessEffects(player, 10);
        
        return "完全回復";
    }
    
    /**
     * 成功エフェクトを再生
     */
    private void playSuccessEffects(Player player, int particleCount) {
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, 1.8f);
        
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 2, 0),
            particleCount, 1, 1, 1, 0.1
        );
    }
}