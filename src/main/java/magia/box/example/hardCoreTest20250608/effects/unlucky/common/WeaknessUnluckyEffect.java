package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@EffectRegistration(
    id = "weakness_unlucky",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "攻撃力が大幅に低下する",
    category = "combat"
)
public class WeaknessUnluckyEffect extends UnluckyEffectBase {
    
    public WeaknessUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "弱体化", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 2));
        player.sendMessage(ChatColor.GRAY + "力が抜けて、攻撃力が低下しました...");
        return getDescription();
    }
}