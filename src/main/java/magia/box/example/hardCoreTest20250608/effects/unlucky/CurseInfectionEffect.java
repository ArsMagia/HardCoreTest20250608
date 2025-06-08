package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CurseInfectionEffect extends UnluckyEffectBase {

    public CurseInfectionEffect(JavaPlugin plugin) {
        super(plugin, "呪いの伝染", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        int infectedCount = 0;
        
        for (Entity entity : center.getWorld().getNearbyEntities(center, 8, 8, 8)) {
            if (entity instanceof Player && entity != player) {
                Player nearbyPlayer = (Player) entity;
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                nearbyPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 0));
                nearbyPlayer.sendMessage(ChatColor.DARK_PURPLE + player.getName() + " の呪いが伝染しました...軽微な弱体化を受けます。");
                infectedCount++;
            }
        }
        
        if (infectedCount > 0) {
            player.sendMessage(ChatColor.RED + "呪いが周囲の " + infectedCount + " 人に伝染しました！");
        } else {
            player.sendMessage(ChatColor.YELLOW + "近くに誰もいないため、呪いは伝染しませんでした。");
        }
        
        return getDescription();
    }
}