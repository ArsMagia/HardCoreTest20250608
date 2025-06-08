package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemDuplicationEffect extends LuckyEffectBase {

    public ItemDuplicationEffect(JavaPlugin plugin) {
        super(plugin, "複製の奇跡", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        
        if (heldItem != null && heldItem.getType() != Material.AIR && 
            !heldItem.getType().toString().contains("LUCKY_BOX")) {
            
            ItemStack duplicate = heldItem.clone();
            player.getInventory().addItem(duplicate);
            
            player.sendMessage(ChatColor.LIGHT_PURPLE + "複製の奇跡！手に持っているアイテムが複製されました！");
            player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
            
            player.getWorld().spawnParticle(
                Particle.ENCHANT,
                player.getLocation().add(0, 1, 0),
                50, 1, 1, 1, 0.3
            );
            
            return getDescription();
        } else {
            player.sendMessage(ChatColor.YELLOW + "複製できるアイテムを手に持っていません。代わりにダイヤモンドを獲得します。");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 3));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
            return "ダイヤモンド×3";
        }
    }
}