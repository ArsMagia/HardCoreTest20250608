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

public class PlayerTrackingCompassRewardEffect extends LuckyEffectBase {

    public PlayerTrackingCompassRewardEffect(JavaPlugin plugin) {
        super(plugin, "追跡コンパス獲得", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // プレイヤー追跡コンパスアイテムを作成
        ItemStack trackingCompass = ItemRegistryAccessor.getPlayerTrackingCompassItem().createItem();
        
        // アイテムを安全に配布
        boolean success = EffectUtils.safeGiveItem(player, trackingCompass);
        
        if (success) {
            player.sendMessage(ChatColor.GOLD + "🧭 " + ChatColor.AQUA + 
                "プレイヤー追跡コンパス" + ChatColor.GOLD + " を獲得しました！");
            player.sendMessage(ChatColor.GRAY + "他プレイヤーの位置を追跡できる便利なアイテムです");
        } else {
            player.sendMessage(ChatColor.GOLD + "🧭 " + ChatColor.AQUA + 
                "プレイヤー追跡コンパス" + ChatColor.GOLD + " を獲得！インベントリが満杯のため足元にドロップしました。");
        }
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.ITEM_LODESTONE_COMPASS_LOCK, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        
        // パーティクルエフェクト
        player.getWorld().spawnParticle(
            Particle.COMPOSTER,
            player.getLocation().add(0, 2, 0),
            20, 1, 1, 1, 0.1
        );
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.3
        );
        
        return getDescription();
    }
}