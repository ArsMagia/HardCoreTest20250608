package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class StableFutureEffect extends LuckyEffectBase {

    public StableFutureEffect(JavaPlugin plugin) {
        super(plugin, "安定した将来", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        // 次の5回まで良い効果を保証
        FutureGuaranteeManager manager = FutureGuaranteeManager.getInstance();
        if (manager == null) {
            // マネージャーが初期化されていない場合は初期化
            FutureGuaranteeManager.initialize(plugin);
            manager = FutureGuaranteeManager.getInstance();
        }
        
        if (manager != null) {
            manager.setGuarantee(
                player, 
                FutureGuaranteeManager.GuaranteeType.STABLE_FUTURE, 
                5
            );
        } else {
            // フォールバック処理
            player.sendMessage(ChatColor.RED + "⚠ 将来保証システムの初期化に失敗しました。");
            return "安定した将来の効果が失敗しました";
        }
        
        player.sendMessage(ChatColor.GOLD + "✨ 安定した将来が発動！");
        player.sendMessage(ChatColor.YELLOW + "次の5回まで、ラッキーボックスで良い効果が保証されます。");
        
        // 派手なエフェクト
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            50, 2, 3, 2, 0.1
        );
        player.getWorld().spawnParticle(
            Particle.END_ROD,
            player.getLocation().add(0, 2, 0),
            30, 1, 1, 1, 0.05
        );
        
        return "安定した将来が発動し、次の5回の良い効果が保証されました";
    }
}