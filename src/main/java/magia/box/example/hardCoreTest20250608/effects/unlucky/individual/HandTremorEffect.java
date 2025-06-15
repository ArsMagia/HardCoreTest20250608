package magia.box.example.hardCoreTest20250608.effects.unlucky.individual;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@EffectRegistration(
    id = "hand_tremor",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "手が震えて作業効率が大幅に低下する",
    category = "physical"
)
public class HandTremorEffect extends UnluckyEffectBase {
    
    public HandTremorEffect(JavaPlugin plugin) {
        super(plugin, "手の震え", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 2));
        player.sendMessage(ChatColor.YELLOW + "手が震えて作業効率が落ちました...");
        return getDescription();
    }
}