package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * もたらされた災厄
 * 発動者に次の2回、他の全プレイヤーに次の1回、アンラッキーエフェクトの発動保証を設定
 */
public class BroughtCalamityEffect extends UnluckyEffectBase {

    public BroughtCalamityEffect(JavaPlugin plugin) {
        super(plugin, "もたらされた災厄", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player cursedPlayer) {
        // 不吉な音とエフェクト
        cursedPlayer.playSound(cursedPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);
        cursedPlayer.getWorld().spawnParticle(
            Particle.SMOKE,
            cursedPlayer.getLocation().add(0, 2, 0),
            50, 2, 2, 2, 0.2
        );
        
        // 災厄の宣言
        cursedPlayer.sendMessage(ChatColor.DARK_RED + "💀 あなたは災厄をもたらしました...");
        cursedPlayer.sendMessage(ChatColor.RED + "⚠ 次の2回のラッキーボックスでアンラッキーが確定します！");
        
        // FutureGuaranteeManagerを取得
        FutureGuaranteeManager guaranteeManager = FutureGuaranteeManager.getInstance();
        if (guaranteeManager == null) {
            // マネージャーが初期化されていない場合は再初期化
            FutureGuaranteeManager.initialize(plugin);
            guaranteeManager = FutureGuaranteeManager.getInstance();
        }
        
        if (guaranteeManager != null) {
            // 発動者に次の2回アンラッキー保証
            guaranteeManager.setGuarantee(cursedPlayer, FutureGuaranteeManager.GuaranteeType.UNLUCKY_CALAMITY, 2);
            
            // 他の全プレイヤーに次の1回アンラッキー保証
            int affectedPlayers = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(cursedPlayer)) {
                    guaranteeManager.setGuarantee(player, FutureGuaranteeManager.GuaranteeType.UNLUCKY_CALAMITY, 1);
                    player.sendMessage(ChatColor.DARK_PURPLE + "💀 " + cursedPlayer.getName() + "の災厄があなたにも影響を与えました...");
                    player.sendMessage(ChatColor.RED + "⚠ 次の1回のラッキーボックスでアンラッキーが確定します！");
                    
                    // 他のプレイヤーにも不吉なエフェクト
                    player.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_AMBIENT, 0.7f, 0.8f);
                    player.getWorld().spawnParticle(
                        Particle.DUST,
                        player.getLocation().add(0, 2, 0),
                        10, 1, 1, 1, 0.1,
                        new org.bukkit.Particle.DustOptions(org.bukkit.Color.fromRGB(64, 0, 64), 1.5f)
                    );
                    
                    affectedPlayers++;
                }
            }
            
            // サーバー全体への警告メッセージ
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "💀 災厄が世界を覆いました... 💀");
            Bukkit.broadcastMessage(ChatColor.RED + "🌟 " + cursedPlayer.getName() + " が全プレイヤーに災いをもたらしました！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠ 影響を受けたプレイヤー: " + (affectedPlayers + 1) + "人");
            Bukkit.broadcastMessage(ChatColor.GRAY + "災厄の発動者: " + cursedPlayer.getName() + " (次の2回アンラッキー確定)");
            Bukkit.broadcastMessage(ChatColor.GRAY + "他のプレイヤー: " + affectedPlayers + "人 (次の1回アンラッキー確定)");
            Bukkit.broadcastMessage("");
            
            // 世界規模の不吉なエフェクト
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getWorld().spawnParticle(
                    Particle.DUST,
                    player.getLocation().add(0, 10, 0),
                    20, 5, 5, 5, 0.1,
                    new org.bukkit.Particle.DustOptions(org.bukkit.Color.fromRGB(128, 0, 0), 2.0f)
                );
            }
            
            return "全プレイヤーにアンラッキー保証を設定しました (発動者: 2回, 他: 1回)";
        } else {
            cursedPlayer.sendMessage(ChatColor.RED + "⚠ 災厄システムの初期化に失敗しました。");
            return "災厄システムエラー";
        }
    }
}