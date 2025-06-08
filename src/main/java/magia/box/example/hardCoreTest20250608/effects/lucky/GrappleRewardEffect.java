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

public class GrappleRewardEffect extends LuckyEffectBase {

    public GrappleRewardEffect(JavaPlugin plugin) {
        super(plugin, "グラップルアイテム", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        ItemStack grapple = ItemRegistryAccessor.getGrappleItem().createItem();
        player.getInventory().addItem(grapple);
        
        player.sendMessage(ChatColor.AQUA + "グラップルアイテムを獲得しました！");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.1
        );
        
        return getDescription();
    }
}