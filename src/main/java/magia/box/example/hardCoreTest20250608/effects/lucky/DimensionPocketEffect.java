package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DimensionPocketEffect extends LuckyEffectBase {

    public DimensionPocketEffect(JavaPlugin plugin) {
        super(plugin, "次元ポケット（30秒間）", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        Inventory pocketInventory = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "次元ポケット");
        
        for (int i = 0; i < 9; i++) {
            pocketInventory.setItem(i, new ItemStack(Material.EMERALD, 2));
            pocketInventory.setItem(i + 9, new ItemStack(Material.GOLD_INGOT, 3));
        }
        pocketInventory.setItem(18, new ItemStack(Material.DIAMOND, 5));
        pocketInventory.setItem(19, new ItemStack(Material.NETHERITE_INGOT, 1));
        
        player.openInventory(pocketInventory);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "次元ポケットが開かれました！30秒間利用可能です。");
        player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.2f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.GRAY + "次元ポケットが閉じられました。");
                    player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0f, 0.8f);
                }
            }
        }.runTaskLater(plugin, 600L);
        
        return getDescription();
    }
}