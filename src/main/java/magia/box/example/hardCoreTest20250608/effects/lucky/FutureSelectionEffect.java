package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FutureSelectionEffect extends LuckyEffectBase implements Listener {
    
    private static final String GUI_TITLE = "未来選択 - どの力を求めますか？";
    private static final Map<UUID, Long> openGUIs = new HashMap<>();
    
    public FutureSelectionEffect(JavaPlugin plugin) {
        super(plugin, "未来選択", EffectRarity.EPIC);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.GOLD + "🔮 未来選択が発動！");
        player.sendMessage(ChatColor.YELLOW + "5つの選択肢から1つを選んでください...");
        
        // タイムアウトなしのGUIを開く
        openSelectionGUI(player);
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 0.5f);
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            player.getLocation().add(0, 2, 0),
            50, 1, 1, 1, 2.0
        );
        
        return "未来選択GUIが開かれました";
    }
    
    private void openSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);
        
        // 5つの選択肢を配置
        gui.setItem(10, createSelectionItem(Material.TOTEM_OF_UNDYING, "安定した将来", 
            "次の5回まで良い効果を保証"));
        gui.setItem(12, createSelectionItem(Material.TNT, "突進中毒", 
            "次回「マルファイトのULT」確定"));
        gui.setItem(14, createSelectionItem(Material.CLOCK, "タイムリープ", 
            "次回「時間巻き戻し」確定"));
        gui.setItem(16, createSelectionItem(Material.GOLDEN_APPLE, "アドレナリンラッシュ", 
            "次回「バフコンビネーション」確定"));
        gui.setItem(22, createSelectionItem(Material.ENDER_EYE, "未来視", 
            "次回ダメージ無効化"));
        
        // 装飾用アイテム
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        
        // 空きスロットを埋める
        for (int i = 0; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        
        player.openInventory(gui);
        openGUIs.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    private ItemStack createSelectionItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + name);
            meta.setLore(Arrays.asList(
                ChatColor.GRAY + description,
                "",
                ChatColor.YELLOW + "クリックして選択"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        if (!GUI_TITLE.equals(event.getView().getTitle())) return;
        
        event.setCancelled(true);
        
        if (!openGUIs.containsKey(player.getUniqueId())) return;
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        // 黒ガラスなどの装飾アイテムをクリックした場合は何もしない
        if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) return;
        
        String itemName = ChatColor.stripColor(meta.getDisplayName());
        
        // GUIを閉じて選択された効果を発動
        openGUIs.remove(player.getUniqueId());
        player.closeInventory();
        
        switch (itemName) {
            case "安定した将来":
                new StableFutureEffect(plugin).apply(player);
                break;
            case "突進中毒":
                new RushAddictionEffect(plugin).apply(player);
                break;
            case "タイムリープ":
                new TimeLeapEffect(plugin).apply(player);
                break;
            case "アドレナリンラッシュ":
                new AdrenalineRushEffect(plugin).apply(player);
                break;
            case "未来視":
                new FutureVisionEffect(plugin).apply(player);
                break;
            default:
                // 無効な選択の場合は何も反応しない
                return;
        }
        
        player.sendMessage(ChatColor.GREEN + "✨ " + itemName + " を選択しました！");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }
}