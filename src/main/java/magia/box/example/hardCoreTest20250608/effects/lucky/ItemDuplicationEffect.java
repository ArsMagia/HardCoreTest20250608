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
        super(plugin, "複製の奇跡", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int duplicatedCount = 0;
        
        // インベントリからランダムに3枠を選んで複製
        java.util.List<Integer> validSlots = new java.util.ArrayList<>();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR &&
                !contents[i].getType().toString().contains("LUCKY_BOX")) {
                validSlots.add(i);
            }
        }
        
        if (validSlots.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "複製できるアイテムがありません。代わりにダイヤモンドを獲得します。");
            player.getInventory().addItem(new ItemStack(Material.DIAMOND, 3));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
            return "ダイヤモンド×3";
        }
        
        // ランダムに最大3つの枠を選択
        java.util.Collections.shuffle(validSlots);
        int slotsToProcess = Math.min(3, validSlots.size());
        
        for (int i = 0; i < slotsToProcess; i++) {
            int slotIndex = validSlots.get(i);
            ItemStack original = contents[slotIndex];
            if (original != null && original.getType() != Material.AIR) {
                ItemStack duplicate = original.clone();
                
                // インベントリに空きがあれば追加、なければドロップ
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(duplicate);
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), duplicate);
                }
                duplicatedCount++;
            }
        }
        
        player.sendMessage(ChatColor.LIGHT_PURPLE + "複製の奇跡！インベントリから" + duplicatedCount + "種類のアイテムが複製されました！");
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            50, 1, 1, 1, 0.3
        );
        
        return getDescription();
    }
}