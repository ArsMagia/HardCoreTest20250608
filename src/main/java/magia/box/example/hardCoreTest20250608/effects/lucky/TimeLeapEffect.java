package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TimeLeapEffect extends LuckyEffectBase {

    public TimeLeapEffect(JavaPlugin plugin) {
        super(plugin, "タイムリープ", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // 次回時間巻き戻しを保証
        FutureGuaranteeManager manager = FutureGuaranteeManager.getInstance();
        if (manager == null) {
            FutureGuaranteeManager.initialize(plugin);
            manager = FutureGuaranteeManager.getInstance();
        }
        
        if (manager != null) {
            manager.setGuarantee(
                player, 
                FutureGuaranteeManager.GuaranteeType.TIME_LEAP, 
                1
            );
        }
        
        player.sendMessage(ChatColor.GOLD + "⏰ タイムリープが発動！");
        player.sendMessage(ChatColor.YELLOW + "次にラッキーボックスをクリックすると「時間巻き戻し」が確定で発動します。");
        
        // 時間を表現するエフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            player.getLocation().add(0, 1, 0),
            50, 1, 2, 1, 1.0
        );
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 2, 0),
            30, 0.5, 0.5, 0.5, 1.0
        );
        
        return "タイムリープが発動し、次回時間巻き戻しが保証されました";
    }
}