package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RushAddictionEffect extends LuckyEffectBase {

    public RushAddictionEffect(JavaPlugin plugin) {
        super(plugin, "突進中毒", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // 次回マルファイトのULTを保証
        FutureGuaranteeManager manager = FutureGuaranteeManager.getInstance();
        if (manager == null) {
            FutureGuaranteeManager.initialize(plugin);
            manager = FutureGuaranteeManager.getInstance();
        }
        
        if (manager != null) {
            manager.setGuarantee(
                player, 
                FutureGuaranteeManager.GuaranteeType.RUSH_ADDICTION, 
                1
            );
        }
        
        player.sendMessage(ChatColor.GOLD + "💥 突進中毒が発動！");
        player.sendMessage(ChatColor.YELLOW + "次にラッキーボックスをクリックすると「マルファイトのULT」が確定で発動します。");
        
        // 突進を表現するエフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_RAVAGER_ROAR, 1.0f, 0.8f);
        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1
        );
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 1, 0),
            40, 2, 2, 2, 0.1,
            new Particle.DustOptions(org.bukkit.Color.RED, 1.5f)
        );
        
        return "突進中毒が発動し、次回マルファイトのULTが保証されました";
    }
}