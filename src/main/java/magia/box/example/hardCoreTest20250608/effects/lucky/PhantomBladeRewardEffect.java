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

public class PhantomBladeRewardEffect extends LuckyEffectBase {

    public PhantomBladeRewardEffect(JavaPlugin plugin) {
        super(plugin, "Phantom Blade", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        ItemStack phantomBlade = ItemRegistryAccessor.getPhantomBladeItem().createItem();
        player.getInventory().addItem(phantomBlade);
        
        player.sendMessage(ChatColor.DARK_PURPLE + "幻影の刃 Phantom Blade を獲得しました！");
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.8f);
        
        player.getWorld().spawnParticle(
            Particle.REVERSE_PORTAL,
            player.getLocation().add(0, 1, 0),
            40, 1, 2, 1, 0.2
        );
        
        return getDescription();
    }
}