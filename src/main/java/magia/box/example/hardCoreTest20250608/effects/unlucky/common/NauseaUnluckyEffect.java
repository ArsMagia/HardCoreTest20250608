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
    id = "nausea_unlucky",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    description = "強烈な吐き気で画面が揺れる",
    category = "disorientation"
)
public class NauseaUnluckyEffect extends UnluckyEffectBase {
    
    public NauseaUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "吐き気", EffectRarity.COMMON);
    }
    
    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
        player.sendMessage(ChatColor.GREEN + "強烈な吐き気で世界が回転しています...");
        return getDescription();
    }
}