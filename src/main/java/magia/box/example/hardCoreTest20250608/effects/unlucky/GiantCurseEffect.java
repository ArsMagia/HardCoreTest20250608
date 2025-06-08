package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GiantCurseEffect extends UnluckyEffectBase {

    public GiantCurseEffect(JavaPlugin plugin) {
        super(plugin, "巨大化呪い", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 800, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 800, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 800, 1));
        
        player.sendMessage(ChatColor.RED + "巨大化の呪い！体が大きくなりすぎて動きにくくなりました。40秒間持続します。");
        return getDescription();
    }
}