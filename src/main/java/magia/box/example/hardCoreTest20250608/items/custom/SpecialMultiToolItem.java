package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpecialMultiToolItem extends AbstractCustomItemV2 {

    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "特殊マルチツール";
    
    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** 選択GUIタイトル */
    private static final String GUI_TITLE = "特殊マルチツール - 選択";

    public SpecialMultiToolItem(JavaPlugin plugin) {
        super(plugin, builder("special_multi_tool", "特殊マルチツール")
                .material(Material.TRIPWIRE_HOOK)
                .rarity(ItemRarity.UNCOMMON)
                .addLore("右クリックで便利ツールを選択")
                .addLore("作業台・エンダーチェスト・金床・醸造台")
                .addHint("使用時に1つ消費"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isCustomItem(item)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // クールダウンチェック
        if (EffectUtils.checkCooldown(player, lastActivation.get(player.getUniqueId()), 
                EffectConstants.ITEM_COOLDOWN_MS, ITEM_NAME)) {
            return;
        }
        lastActivation.put(player.getUniqueId(), System.currentTimeMillis());

        event.setCancelled(true);

        // アイテムを1つ消費
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        // 選択GUIを開く
        openSelectionGUI(player);
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 
                EffectConstants.STANDARD_VOLUME, 1.0f);
    }
    
    /**
     * ツール選択GUIを開く
     * @param player プレイヤー
     */
    private void openSelectionGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_TITLE);
        
        // 作業台
        ItemStack craftingTable = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta craftingMeta = craftingTable.getItemMeta();
        if (craftingMeta != null) {
            craftingMeta.setDisplayName(ChatColor.GREEN + "作業台");
            craftingMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "3x3の作業台を開きます",
                ChatColor.YELLOW + "クリックで選択"
            ));
            craftingTable.setItemMeta(craftingMeta);
        }
        gui.setItem(1, craftingTable);
        
        // エンダーチェスト
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderMeta = enderChest.getItemMeta();
        if (enderMeta != null) {
            enderMeta.setDisplayName(ChatColor.DARK_PURPLE + "エンダーチェスト");
            enderMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "エンダーチェストを開きます",
                ChatColor.YELLOW + "クリックで選択"
            ));
            enderChest.setItemMeta(enderMeta);
        }
        gui.setItem(3, enderChest);
        
        // 金床
        ItemStack anvil = new ItemStack(Material.ANVIL);
        ItemMeta anvilMeta = anvil.getItemMeta();
        if (anvilMeta != null) {
            anvilMeta.setDisplayName(ChatColor.DARK_GRAY + "金床");
            anvilMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "金床を開きます",
                ChatColor.YELLOW + "クリックで選択"
            ));
            anvil.setItemMeta(anvilMeta);
        }
        gui.setItem(5, anvil);
        
        // 醸造台
        ItemStack brewingStand = new ItemStack(Material.BREWING_STAND);
        ItemMeta brewingMeta = brewingStand.getItemMeta();
        if (brewingMeta != null) {
            brewingMeta.setDisplayName(ChatColor.GOLD + "醸造台");
            brewingMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "醸造台を開きます",
                ChatColor.YELLOW + "クリックで選択"
            ));
            brewingStand.setItemMeta(brewingMeta);
        }
        gui.setItem(7, brewingStand);
        
        // 装飾用アイテム（空きスロット）
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        
        for (int i = 0; i < 9; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
        
        player.openInventory(gui);
        player.sendMessage(ChatColor.GREEN + "🔧 " + ITEM_NAME + " を使用しました！ツールを選択してください。");
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        
        if (!GUI_TITLE.equals(event.getView().getTitle())) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // クリックされたアイテムに応じてGUIを開く
        Material clickedType = clickedItem.getType();
        
        switch (clickedType) {
            case CRAFTING_TABLE:
                player.closeInventory();
                player.openWorkbench(null, true);
                player.sendMessage(ChatColor.GREEN + "🔨 作業台を開きました！");
                player.playSound(player.getLocation(), Sound.BLOCK_WOOD_PLACE, 
                        EffectConstants.STANDARD_VOLUME, 1.2f);
                break;
                
            case ENDER_CHEST:
                player.closeInventory();
                player.openInventory(player.getEnderChest());
                player.sendMessage(ChatColor.DARK_PURPLE + "📦 エンダーチェストを開きました！");
                player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 
                        EffectConstants.STANDARD_VOLUME, 1.0f);
                break;
                
            case ANVIL:
                player.closeInventory();
                // 金床の簡易GUIを開く
                openAnvilGUI(player);
                player.sendMessage(ChatColor.DARK_GRAY + "⚒ 金床を開きました！");
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 
                        EffectConstants.STANDARD_VOLUME, 1.0f);
                break;
                
            case BREWING_STAND:
                player.closeInventory();
                // 醸造台のGUIを開く
                openBrewingStandGUI(player);
                player.sendMessage(ChatColor.GOLD + "🧪 醸造台を開きました！");
                player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, 
                        EffectConstants.STANDARD_VOLUME, 1.0f);
                break;
                
            case GRAY_STAINED_GLASS_PANE:
                // 装飾用アイテムは無視
                break;
                
            default:
                player.sendMessage(ChatColor.RED + "無効な選択です。");
                break;
        }
    }
    
    /**
     * 金床GUIを開く
     * @param player プレイヤー
     */
    private void openAnvilGUI(Player player) {
        // 簡易的な金床風GUIを作成
        Inventory anvilGUI = Bukkit.createInventory(null, 27, "金床");
        
        // 金床の説明アイテム
        ItemStack info = new ItemStack(Material.ANVIL);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.DARK_GRAY + "金床の使い方");
            infoMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "左側にアイテムを配置",
                ChatColor.GRAY + "中央に修理材料やエンチャント本",
                ChatColor.GRAY + "右側で結果を取得",
                ChatColor.YELLOW + "※これは簡易版です"
            ));
            info.setItemMeta(infoMeta);
        }
        anvilGUI.setItem(13, info);
        
        // ガラス板で装飾
        ItemStack glass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        
        // 周囲を装飾
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                anvilGUI.setItem(i, glass);
            }
        }
        
        player.openInventory(anvilGUI);
    }
    
    /**
     * 醸造台GUIを開く
     * @param player プレイヤー
     */
    private void openBrewingStandGUI(Player player) {
        // 簡易的な醸造台風GUIを作成
        Inventory brewingGUI = Bukkit.createInventory(null, 27, "醸造台");
        
        // 醸造台の説明アイテム
        ItemStack info = new ItemStack(Material.POTION);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + "醸造台の使い方");
            infoMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "左側のスロットにポーション瓶を配置",
                ChatColor.GRAY + "上部にブレイズパウダー（燃料）",
                ChatColor.GRAY + "上部に醸造材料を配置",
                ChatColor.YELLOW + "※これは簡易版です"
            ));
            info.setItemMeta(infoMeta);
        }
        brewingGUI.setItem(13, info);
        
        // ガラス板で装飾
        ItemStack glass = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }
        
        // 周囲を装飾
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                brewingGUI.setItem(i, glass);
            }
        }
        
        player.openInventory(brewingGUI);
    }
}