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
    id = "blindness_unlucky",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "視界が真っ暗になる",
    category = "vision"
)
public class BlindnessUnluckyEffect extends UnluckyEffectBase {
    
    public BlindnessUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "盲目", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
        player.sendMessage(ChatColor.BLACK + "目の前が真っ暗になりました...");
        return getDescription();
    }
}