package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class HealthReductionEffect extends UnluckyEffectBase {

    public HealthReductionEffect(JavaPlugin plugin) {
        super(plugin, "最大体力減少", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr != null) {
            double currentMaxHealth = healthAttr.getBaseValue();
            
            if (currentMaxHealth > 4) { // 最低4まで（ハート2個）
                healthAttr.setBaseValue(currentMaxHealth - 2); // ハート1個分減少
                
                // 現在の体力が新しい上限を超えている場合は調整
                if (player.getHealth() > healthAttr.getValue()) {
                    player.setHealth(healthAttr.getValue());
                }
                
                player.sendMessage(ChatColor.DARK_RED + "最大体力が減少しました...現在: " + 
                    ChatColor.RED + "❤ " + (int)(healthAttr.getValue() / 2) + " ハート");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 0.8f);
                
                player.getWorld().spawnParticle(
                    Particle.DAMAGE_INDICATOR,
                    player.getLocation().add(0, 1, 0),
                    10, 0.5, 0.5, 0.5, 0.1
                );
                
                return getDescription() + " (-1❤)";
            } else {
                player.sendMessage(ChatColor.YELLOW + "最大体力が下限（2ハート）に達しています。代わりに軽微なダメージを受けます。");
                player.damage(2.0); // 1ハート分のダメージ
                
                player.getWorld().spawnParticle(
                    Particle.DAMAGE_INDICATOR,
                    player.getLocation().add(0, 1, 0),
                    5, 0.3, 0.3, 0.3, 0.1
                );
                
                return "軽微ダメージ";
            }
        }
        
        return getDescription();
    }
}