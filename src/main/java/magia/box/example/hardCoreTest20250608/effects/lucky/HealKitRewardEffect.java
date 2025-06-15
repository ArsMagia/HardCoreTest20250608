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

public class HealKitRewardEffect extends LuckyEffectBase {

    public HealKitRewardEffect(JavaPlugin plugin) {
        super(plugin, "ヒールキット", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        ItemStack healKit = ItemRegistryAccessor.getHealKitItem().createItem();
        player.getInventory().addItem(healKit);
        
        player.sendMessage(ChatColor.GREEN + "医療キット ヒールキット を獲得しました！");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
        
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1
        );
        
        return getDescription();
    }
}