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
    id = "slowness_unlucky",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "移動速度が大幅に低下する",
    category = "movement"
)
public class SlownessUnluckyEffect extends UnluckyEffectBase {
    
    public SlownessUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "遅化", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 2));
        player.sendMessage(ChatColor.BLUE + "体が重くなり、動きが鈍くなりました...");
        return getDescription();
    }
}