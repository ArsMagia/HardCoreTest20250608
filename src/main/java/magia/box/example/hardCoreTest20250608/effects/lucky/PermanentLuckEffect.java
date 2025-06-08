package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PermanentLuckEffect extends LuckyEffectBase {

    public PermanentLuckEffect(JavaPlugin plugin) {
        super(plugin, "永続幸運", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // 30分間の長時間幸運効果
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 36000, 2, false, true));
        
        player.sendMessage(ChatColor.GOLD + "永続幸運が発動！30分間、Lv.3の幸運効果を受けます。");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 2.0f);
        
        player.getWorld().spawnParticle(
            org.bukkit.Particle.HAPPY_VILLAGER,
            player.getLocation().add(0, 1, 0),
            100, 2, 3, 2, 0.2
        );
        
        return getDescription();
    }
}