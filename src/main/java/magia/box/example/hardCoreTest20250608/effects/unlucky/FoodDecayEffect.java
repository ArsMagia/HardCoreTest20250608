package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FoodDecayEffect extends UnluckyEffectBase {

    public FoodDecayEffect(JavaPlugin plugin) {
        super(plugin, "腐敗の呪い", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        int decayedCount = 0;
        
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) {
                int amount = item.getAmount();
                player.getInventory().setItem(i, new ItemStack(Material.ROTTEN_FLESH, amount));
                decayedCount++;
            }
        }
        
        if (decayedCount > 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "食料が腐ってしまいました...すべて腐肉に変化しました。");
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 0.8f);
        } else {
            player.sendMessage(ChatColor.YELLOW + "腐る食料がありませんでした。代わりに空腹効果を受けます。");
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.HUNGER, 400, 1));
        }
        
        return getDescription();
    }
}