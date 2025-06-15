package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ToolBreakageEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public ToolBreakageEffect(JavaPlugin plugin) {
        super(plugin, "道具破損", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // ホットバーのツールを検索
        java.util.List<Integer> toolSlots = new java.util.ArrayList<>();
        
        for (int i = 0; i < 9; i++) { // ホットバーのみ
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && isTool(item.getType()) && item.getType().getMaxDurability() > 0) {
                toolSlots.add(i);
            }
        }
        
        if (toolSlots.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "道具破損が発動しましたが、ホットバーにツールがありませんでした。");
            return getDescription();
        }
        
        // ランダムで1つのツールを選択
        int selectedSlot = toolSlots.get(random.nextInt(toolSlots.size()));
        ItemStack item = player.getInventory().getItem(selectedSlot);
        
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            int maxDurability = item.getType().getMaxDurability();
            
            // 耐久値を30以下に設定（残り耐久30以下）
            int targetDamage = Math.max(maxDurability - 30, 0);
            damageable.setDamage(targetDamage);
            item.setItemMeta(meta);
            
            player.sendMessage(ChatColor.RED + "道具破損！" + item.getType().name() + "の耐久値が大幅に減少しました！");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
        }
        
        return getDescription();
    }
    
    private boolean isTool(Material material) {
        String name = material.toString();
        return name.contains("PICKAXE") || name.contains("AXE") || name.contains("SHOVEL") || 
               name.contains("HOE") || name.contains("SWORD") || name.contains("BOW") ||
               name.contains("SHEARS") || name.contains("FLINT_AND_STEEL") || name.contains("FISHING_ROD");
    }
}