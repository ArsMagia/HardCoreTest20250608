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

public class LoneSwordRewardEffect extends LuckyEffectBase {

    public LoneSwordRewardEffect(JavaPlugin plugin) {
        super(plugin, "Lone Sword", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        ItemStack loneSword = ItemRegistryAccessor.getLoneSwordItem().createItem();
        player.getInventory().addItem(loneSword);
        
        player.sendMessage(ChatColor.GOLD + "伝説の武器 Lone Sword を獲得しました！");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            50, 1, 2, 1, 0.3
        );
        
        return getDescription();
    }
}