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
    id = "mining_fatigue_unlucky",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "採掘速度が大幅に低下する",
    category = "mining"
)
public class MiningFatigueUnluckyEffect extends UnluckyEffectBase {
    
    public MiningFatigueUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "採掘疲労", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 2));
        player.sendMessage(ChatColor.YELLOW + "腕に疲労感が溜まり、採掘速度が低下しました...");
        return getDescription();
    }
}