package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CursedFogEffect extends UnluckyEffectBase {

    public CursedFogEffect(JavaPlugin plugin) {
        super(plugin, "呪いの霧", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 600, 1));
        
        player.sendMessage(ChatColor.DARK_GRAY + "呪いの霧に包まれました...30秒間、視界と行動が制限されます。");
        
        return getDescription();
    }
}