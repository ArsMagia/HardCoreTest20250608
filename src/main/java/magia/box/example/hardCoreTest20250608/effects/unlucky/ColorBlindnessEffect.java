package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ColorBlindnessEffect extends UnluckyEffectBase {

    public ColorBlindnessEffect(JavaPlugin plugin) {
        super(plugin, "色盲の呪い", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 600, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 600, 0));
        
        player.sendMessage(ChatColor.DARK_GRAY + "世界が白黒に見えるようになりました...30秒間色彩を失います。");
        return getDescription();
    }
}