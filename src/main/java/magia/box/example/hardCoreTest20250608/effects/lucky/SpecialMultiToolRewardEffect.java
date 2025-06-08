package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SpecialMultiToolRewardEffect extends LuckyEffectBase {

    public SpecialMultiToolRewardEffect(JavaPlugin plugin) {
        super(plugin, "特殊マルチツール獲得", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // 特殊マルチツールを複数個作成（3個セット）
        ItemStack specialMultiTool = ItemRegistryAccessor.getSpecialMultiToolItem().createItem();
        specialMultiTool.setAmount(3);
        
        // アイテムを安全に配布
        boolean success = EffectUtils.safeGiveItem(player, specialMultiTool);
        
        if (success) {
            player.sendMessage(ChatColor.GOLD + "🔧 " + ChatColor.AQUA + 
                "特殊マルチツール x3" + ChatColor.GOLD + " を獲得しました！");
            player.sendMessage(ChatColor.GRAY + "作業台・エンダーチェスト・金床・醸造台をどこでも利用可能");
        } else {
            player.sendMessage(ChatColor.GOLD + "🔧 " + ChatColor.AQUA + 
                "特殊マルチツール x3" + ChatColor.GOLD + " を獲得！インベントリが満杯のため足元にドロップしました。");
        }
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 
                EffectConstants.STANDARD_VOLUME, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        
        // パーティクルエフェクト
        player.getWorld().spawnParticle(
            Particle.CRIT,
            player.getLocation().add(0, 2, 0),
            25, 1, 1, 1, 0.2
        );
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.3
        );
        
        return getDescription() + " (x3)";
    }
}