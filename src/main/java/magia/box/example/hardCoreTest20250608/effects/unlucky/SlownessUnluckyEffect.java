package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SlownessUnluckyEffect extends UnluckyEffectBase {

    public SlownessUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "移動速度低下", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        int duration = 45 * 20;
        int amplifier = 3;
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, amplifier));
        return getDescription();
    }
}