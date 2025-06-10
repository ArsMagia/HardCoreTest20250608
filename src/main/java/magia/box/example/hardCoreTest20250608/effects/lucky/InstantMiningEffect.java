package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InstantMiningEffect extends LuckyEffectBase {

    public InstantMiningEffect(JavaPlugin plugin) {
        super(plugin, "時間停止採掘", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // 超高速採掘効果
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 200, 10, false, false));
        
        player.sendMessage(ChatColor.AQUA + "時間停止採掘が発動！10秒間、ブロック破壊が超高速になります！");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.8f);
        
        player.getWorld().spawnParticle(
            org.bukkit.Particle.CRIT,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.2
        );
        
        return getDescription();
    }
}