package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AdrenalineRushEffect extends LuckyEffectBase {

    public AdrenalineRushEffect(JavaPlugin plugin) {
        super(plugin, "アドレナリンラッシュ", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // 次回バフコンビネーションを保証
        FutureGuaranteeManager manager = FutureGuaranteeManager.getInstance();
        if (manager == null) {
            FutureGuaranteeManager.initialize(plugin);
            manager = FutureGuaranteeManager.getInstance();
        }
        
        if (manager != null) {
            manager.setGuarantee(
                player, 
                FutureGuaranteeManager.GuaranteeType.ADRENALINE_RUSH, 
                1
            );
        }
        
        player.sendMessage(ChatColor.GOLD + "⚡ アドレナリンラッシュが発動！");
        player.sendMessage(ChatColor.YELLOW + "次にラッキーボックスをクリックすると「バフコンビネーション」が確定で発動します。");
        
        // アドレナリンを表現するエフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        player.getWorld().spawnParticle(
            Particle.CRIT,
            player.getLocation().add(0, 1, 0),
            40, 1, 2, 1, 0.5
        );
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.1,
            new Particle.DustOptions(org.bukkit.Color.YELLOW, 1.2f)
        );
        
        return "アドレナリンラッシュが発動し、次回バフコンビネーションが保証されました";
    }
}