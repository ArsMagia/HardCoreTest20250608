package magia.box.example.hardCoreTest20250608.commands;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CustomItemsCMD implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private static final String GUI_TITLE = "カスタムアイテム一覧";

    public CustomItemsCMD(JavaPlugin plugin) {
        this.plugin = plugin;
        // イベントリスナーとして登録
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(EffectConstants.PLAYER_ONLY_MESSAGE);
            return true;
        }

        Player player = (Player) sender;

        // 管理者権限チェック
        if (!player.isOp()) {
            player.sendMessage(EffectConstants.PERMISSION_DENIED_MESSAGE);
            return true;
        }

        openCustomItemsGUI(player);
        return true;
    }

    /**
     * カスタムアイテム一覧GUIを開く
     * @param player プレイヤー
     */
    private void openCustomItemsGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, GUI_TITLE);

        // カスタムアイテムを配置
        setupCustomItems(gui);

        player.openInventory(gui);
        player.sendMessage(ChatColor.GREEN + "📦 カスタムアイテム一覧を開きました！左クリックでアイテムを取得できます。");
        
        // 開放音
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
    }

    /**
     * GUIにカスタムアイテムを配置
     * @param gui 対象GUI
     */
    private void setupCustomItems(Inventory gui) {
        int slot = 0;

        // 1. グラップル
        ItemStack grapple = ItemRegistryAccessor.getGrappleItem().createItem();
        addItemToGUI(gui, slot++, grapple, "無制限使用可能なグラップリングツール");

        // 2. ラッキーボックス
        ItemStack luckyBox = ItemRegistryAccessor.getLuckyBoxItem().createItem();
        addItemToGUI(gui, slot++, luckyBox, "50%の確率でラッキー/アンラッキー効果");

        // 3. ロンソード
        ItemStack loneSword = ItemRegistryAccessor.getLoneSwordItem().createItem();
        addItemToGUI(gui, slot++, loneSword, "右クリックで体力回復する剣");

        // 4. ファントムブレード
        ItemStack phantomBlade = ItemRegistryAccessor.getPhantomBladeItem().createItem();
        addItemToGUI(gui, slot++, phantomBlade, "右クリックで前方ダッシュする剣");

        // 5. シャッフルアイテム
        ItemStack shuffleItem = ItemRegistryAccessor.getShuffleItem().createItem();
        addItemToGUI(gui, slot++, shuffleItem, "プレイヤーと位置・体力を交換");

        // 6. 強化ピッケル
        ItemStack enhancedPickaxe = ItemRegistryAccessor.getEnhancedPickaxeItem().createItem();
        addItemToGUI(gui, slot++, enhancedPickaxe, "5x5範囲で一度に採掘するピッケル");

        // 7. プレイヤー追跡コンパス
        ItemStack trackingCompass = ItemRegistryAccessor.getPlayerTrackingCompassItem().createItem();
        addItemToGUI(gui, slot++, trackingCompass, "他プレイヤーの位置を追跡するコンパス");

        // 8. 特殊マルチツール
        ItemStack multiTool = ItemRegistryAccessor.getSpecialMultiToolItem().createItem();
        addItemToGUI(gui, slot++, multiTool, "作業台・エンダーチェスト・金床・醸造台をポータブル利用");

        // 9. ヒールキット
        ItemStack healKit = ItemRegistryAccessor.getHealKitItem().createItem();
        addItemToGUI(gui, slot++, healKit, "プレイヤーを左クリックでRegeneration II付与");

        // 空きスロットを装飾
        fillEmptySlots(gui, slot);
    }

    /**
     * GUIにアイテムを追加（説明付き）
     * @param gui 対象GUI
     * @param slot スロット番号
     * @param item アイテム
     * @param description 追加説明
     */
    private void addItemToGUI(Inventory gui, int slot, ItemStack item, String description) {
        if (slot >= gui.getSize()) return;

        // アイテムメタを複製して説明を追加
        ItemStack displayItem = item.clone();
        ItemMeta meta = displayItem.getItemMeta();
        
        if (meta != null) {
            // 既存のLoreを取得
            var lore = meta.getLore();
            if (lore == null) {
                lore = Arrays.asList();
            }
            
            // 新しいLoreリストを作成
            var newLore = new java.util.ArrayList<>(lore);
            newLore.add("");
            newLore.add(ChatColor.YELLOW + "📖 " + description);
            newLore.add("");
            newLore.add(ChatColor.GREEN + "左クリックで取得");
            
            meta.setLore(newLore);
            displayItem.setItemMeta(meta);
        }

        gui.setItem(slot, displayItem);
    }

    /**
     * 空きスロットを装飾で埋める
     * @param gui 対象GUI
     * @param startSlot 開始スロット
     */
    private void fillEmptySlots(Inventory gui, int startSlot) {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }

        for (int i = startSlot; i < gui.getSize(); i++) {
            gui.setItem(i, filler);
        }
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

        // 装飾アイテムの場合は無視
        if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return;
        }

        // 左クリックのみ対応
        if (!event.getClick().isLeftClick()) {
            player.sendMessage(ChatColor.RED + "左クリックでアイテムを取得してください。");
            return;
        }

        // 元のカスタムアイテムを作成（説明なしの状態）
        ItemStack originalItem = createOriginalItem(clickedItem);
        if (originalItem == null) {
            player.sendMessage(ChatColor.RED + "アイテムの取得に失敗しました。");
            return;
        }

        // アイテムを配布
        boolean success = EffectUtils.safeGiveItem(player, originalItem);

        if (success) {
            String itemName = originalItem.getItemMeta() != null ? 
                originalItem.getItemMeta().getDisplayName() : originalItem.getType().name();
            player.sendMessage(ChatColor.GREEN + "✅ " + itemName + ChatColor.GREEN + " を取得しました！");
        } else {
            String itemName = originalItem.getItemMeta() != null ? 
                originalItem.getItemMeta().getDisplayName() : originalItem.getType().name();
            player.sendMessage(ChatColor.YELLOW + "✅ " + itemName + ChatColor.YELLOW + 
                " を取得しました！インベントリが満杯のため足元にドロップしました。");
        }

        // 取得音
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 
                EffectConstants.STANDARD_VOLUME, 1.5f);
    }

    /**
     * 表示用アイテムから元のカスタムアイテムを作成
     * @param displayItem 表示用アイテム
     * @return 元のカスタムアイテム
     */
    private ItemStack createOriginalItem(ItemStack displayItem) {
        Material material = displayItem.getType();

        // マテリアルに基づいてカスタムアイテムを判定
        return switch (material) {
            case FISHING_ROD -> ItemRegistryAccessor.getGrappleItem().createItem();
            case NETHER_STAR -> ItemRegistryAccessor.getLuckyBoxItem().createItem();
            case IRON_SWORD -> {
                // ロンソードかファントムブレードかを判定（表示名で）
                if (displayItem.hasItemMeta() && displayItem.getItemMeta().hasDisplayName()) {
                    String displayName = displayItem.getItemMeta().getDisplayName();
                    if (displayName.contains("Lone Sword")) {
                        yield ItemRegistryAccessor.getLoneSwordItem().createItem();
                    } else if (displayName.contains("Phantom Blade")) {
                        yield ItemRegistryAccessor.getPhantomBladeItem().createItem();
                    }
                }
                yield null;
            }
            case ENDER_PEARL -> ItemRegistryAccessor.getShuffleItem().createItem();
            case IRON_PICKAXE -> ItemRegistryAccessor.getEnhancedPickaxeItem().createItem();
            case COMPASS -> ItemRegistryAccessor.getPlayerTrackingCompassItem().createItem();
            case TRIPWIRE_HOOK -> ItemRegistryAccessor.getSpecialMultiToolItem().createItem();
            case SHEARS -> ItemRegistryAccessor.getHealKitItem().createItem();
            default -> null;
        };
    }
}