package magia.box.example.hardCoreTest20250608.effects.lucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import magia.box.example.hardCoreTest20250608.effects.core.EffectCommons;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@EffectRegistration(
    id = "speed_lucky",
    type = EffectType.LUCKY,
    rarity = EffectRarity.COMMON,
    description = "移動速度が大幅に向上する",
    category = "movement"
)
public class SpeedLuckyEffect extends LuckyEffectBase {
    
    public SpeedLuckyEffect(JavaPlugin plugin) {
        super(plugin, "移動速度上昇", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        if (!EffectCommons.isPlayerValid(player)) {
            return "プレイヤーが無効な状態です";
        }
        
        // 効果適用
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 2));
        player.sendMessage(ChatColor.AQUA + "体が軽やかになり、素早く移動できるようになりました！");
        
        // 統一エフェクト
        EffectCommons.playSuccessEffect(player, getRarity());
        
        return getDescription();
    }
}