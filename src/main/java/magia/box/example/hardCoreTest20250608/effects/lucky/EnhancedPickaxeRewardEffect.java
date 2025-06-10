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

public class EnhancedPickaxeRewardEffect extends LuckyEffectBase {

    public EnhancedPickaxeRewardEffect(JavaPlugin plugin) {
        super(plugin, "強化ピッケル獲得", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        // 強化ピッケルアイテムを作成
        ItemStack enhancedPickaxe = ItemRegistryAccessor.getEnhancedPickaxeItem().createItem();
        
        // アイテムを安全に配布
        boolean success = EffectUtils.safeGiveItem(player, enhancedPickaxe);
        
        if (success) {
            player.sendMessage(ChatColor.GOLD + "✨ " + ChatColor.LIGHT_PURPLE + 
                "強化ピッケル" + ChatColor.GOLD + " を獲得しました！");
            player.sendMessage(ChatColor.GRAY + "5x5範囲で一度に採掘可能な特別なピッケルです");
        } else {
            player.sendMessage(ChatColor.GOLD + "✨ " + ChatColor.LIGHT_PURPLE + 
                "強化ピッケル" + ChatColor.GOLD + " を獲得！インベントリが満杯のため足元にドロップしました。");
        }
        
        // 豪華なエフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, 0.5f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 
                EffectConstants.STANDARD_VOLUME, 1.5f);
        
        // パーティクルエフェクト
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 2, 0),
            50, 1, 1, 1, 0.5
        );
        
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1
        );
        
        
        return getDescription();
    }
}