package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedLuckyEffect extends LuckyEffectBase {

    public SpeedLuckyEffect(JavaPlugin plugin) {
        super(plugin, "移動速度上昇", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        int duration = 60 * 20;
        int amplifier = 3;
        
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, amplifier));
        return getDescription();
    }
}