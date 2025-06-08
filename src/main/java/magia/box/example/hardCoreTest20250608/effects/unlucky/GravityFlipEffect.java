package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GravityFlipEffect extends UnluckyEffectBase {

    public GravityFlipEffect(JavaPlugin plugin) {
        super(plugin, "重力反転", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 2));
        player.sendMessage(ChatColor.LIGHT_PURPLE + "重力が反転しました！");
        return getDescription();
    }
}