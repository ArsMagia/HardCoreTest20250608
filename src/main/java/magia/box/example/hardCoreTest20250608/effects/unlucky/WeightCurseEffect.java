package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeightCurseEffect extends UnluckyEffectBase {

    public WeightCurseEffect(JavaPlugin plugin) {
        super(plugin, "重量増加", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 600, -2));
        
        player.sendMessage(ChatColor.GRAY + "体が鉛のように重くなりました...30秒間動きが鈍くなります。");
        return getDescription();
    }
}