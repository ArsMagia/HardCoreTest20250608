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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DimensionPocketEffect extends LuckyEffectBase {

    public DimensionPocketEffect(JavaPlugin plugin) {
        super(plugin, "次元ポケット（10秒間）", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Inventory pocketInventory = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "次元ポケット");
        
        // 新しい内容物を設定
        pocketInventory.setItem(0, new ItemStack(Material.IRON_INGOT, 10));
        pocketInventory.setItem(1, new ItemStack(Material.DRIED_KELP_BLOCK, 3));
        pocketInventory.setItem(2, new ItemStack(Material.DIAMOND, 1));
        pocketInventory.setItem(3, new ItemStack(Material.RAIL, 20));
        pocketInventory.setItem(4, new ItemStack(Material.POWERED_RAIL, 10));
        pocketInventory.setItem(5, new ItemStack(Material.MINECART, 1));
        pocketInventory.setItem(6, new ItemStack(Material.COBWEB, 1));
        
        // Potion of Healing I を作成
        ItemStack healingPotion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) healingPotion.getItemMeta();
        if (potionMeta != null) {
            potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0), true);
            potionMeta.setDisplayName(ChatColor.RED + "治癒のポーション I");
            healingPotion.setItemMeta(potionMeta);
        }
        pocketInventory.setItem(7, healingPotion);
        
        player.openInventory(pocketInventory);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "次元ポケットが開かれました！10秒間利用可能です。");
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
        }.runTaskLater(plugin, 200L); // 10秒に変更
        
        return getDescription();
    }
}