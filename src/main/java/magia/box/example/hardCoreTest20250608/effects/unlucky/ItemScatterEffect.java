package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ItemScatterEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public ItemScatterEffect(JavaPlugin plugin) {
        super(plugin, "アイテム分散", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        int scatteredCount = 0;
        
        for (int i = 9; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() != Material.AIR) {
                if (random.nextInt(3) == 0) {
                    Location dropLoc = player.getLocation().add(
                        random.nextDouble() * 10 - 5,
                        random.nextDouble() * 3,
                        random.nextDouble() * 10 - 5
                    );
                    
                    player.getWorld().dropItemNaturally(dropLoc, item);
                    player.getInventory().setItem(i, null);
                    scatteredCount++;
                    
                    dropLoc.getWorld().spawnParticle(
                        Particle.ITEM,
                        dropLoc,
                        5, 0.2, 0.2, 0.2, 0.1,
                        item
                    );
                }
            }
        }
        
        if (scatteredCount > 0) {
            player.sendMessage(ChatColor.RED + "インベントリが爆散！" + scatteredCount + "個のアイテムが周囲に散らばりました。");
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.2f);
            
            player.getLocation().getWorld().spawnParticle(
                Particle.EXPLOSION,
                player.getLocation().add(0, 1, 0),
                3, 1, 1, 1, 0.1
            );
        } else {
            player.sendMessage(ChatColor.YELLOW + "散らばるアイテムがありませんでした。代わりに混乱効果を受けます。");
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.NAUSEA, 200, 1));
        }
        
        return getDescription();
    }
}