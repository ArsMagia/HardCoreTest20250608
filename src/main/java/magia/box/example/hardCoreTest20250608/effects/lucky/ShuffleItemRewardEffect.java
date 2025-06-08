package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ShuffleItemRewardEffect extends LuckyEffectBase {

    public ShuffleItemRewardEffect(JavaPlugin plugin) {
        super(plugin, "シャッフルアイテム", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        ItemStack shuffleItem = ItemRegistryAccessor.getShuffleItem().createItem();
        player.getInventory().addItem(shuffleItem);
        
        player.sendMessage(ChatColor.YELLOW + "シャッフルアイテムを獲得しました！");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.3f);
        
        player.getWorld().spawnParticle(
            Particle.HAPPY_VILLAGER,
            player.getLocation().add(0, 1, 0),
            25, 1, 1, 1, 0.1
        );
        
        return getDescription();
    }
}