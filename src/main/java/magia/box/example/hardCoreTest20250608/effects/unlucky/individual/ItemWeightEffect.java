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
    id = "item_weight",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "所持アイテムが重くなり移動速度が低下する",
    category = "physical"
)
public class ItemWeightEffect extends UnluckyEffectBase {
    
    public ItemWeightEffect(JavaPlugin plugin) {
        super(plugin, "アイテム重量化", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
        player.sendMessage(ChatColor.GRAY + "アイテムが重くなりました...移動速度が低下します。");
        return getDescription();
    }
}