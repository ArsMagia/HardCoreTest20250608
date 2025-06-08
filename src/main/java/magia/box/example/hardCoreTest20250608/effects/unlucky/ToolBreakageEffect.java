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
        int damagedCount = 0;
        
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().getMaxDurability() > 0) {
                ItemMeta meta = item.getItemMeta();
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;
                    int currentDamage = damageable.getDamage();
                    int maxDurability = item.getType().getMaxDurability();
                    int additionalDamage = random.nextInt(maxDurability / 3) + 1;
                    
                    damageable.setDamage(Math.min(currentDamage + additionalDamage, maxDurability - 1));
                    item.setItemMeta(meta);
                    damagedCount++;
                }
            }
        }
        
        if (damagedCount > 0) {
            player.sendMessage(ChatColor.RED + "道具が破損しました！" + damagedCount + "個のアイテムの耐久値が減少しました。");
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
        } else {
            player.sendMessage(ChatColor.YELLOW + "破損する道具がありませんでした。");
        }
        
        return getDescription();
    }
}