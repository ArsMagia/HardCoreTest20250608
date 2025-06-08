package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UpsideDownVisionEffect extends UnluckyEffectBase {

    public UpsideDownVisionEffect(JavaPlugin plugin) {
        super(plugin, "逆さま視点", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 1));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "世界が逆さまに見えます...20秒間混乱します。");
        return getDescription();
    }
}