package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;
import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SpecialMultiToolItem extends AbstractCustomItemV2 {

    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "特殊マルチツール";
    
    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** Lectern機能専用クールダウン管理 */
    private static final Map<UUID, Long> lecternLastActivation = new HashMap<>();
    
    /** 選択GUIタイトル */
    private static final String GUI_TITLE = "特殊マルチツール - 選択";

    public SpecialMultiToolItem(JavaPlugin plugin) {
        super(plugin, builder("special_multi_tool", "特殊マルチツール")
                .material(Material.TRIPWIRE_HOOK)
                .rarity(ItemRarity.UNCOMMON)
                .addLore("右クリックで便利ツールを選択")
                .addLore("作業台・エンダーチェスト・ランダムアイテム")
                .addHint("使用時に1つ消費"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // カスタムアイテムチェックを先に実行
        if (!isCustomItem(item)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);
        
        // クールダウンチェック（複数回発動防止）
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUse = lastActivation.get(playerId);
        
        if (lastUse != null && (currentTime - lastUse) < EffectConstants.ITEM_COOLDOWN_MS) {
            // クールダウン中は無言でリターン（メッセージ重複防止）
            return;
        }
        
        // クールダウン設定（複数回発動防止のため早期設定）
        lastActivation.put(playerId, currentTime);

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
        gui.setItem(2, craftingTable);
        
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
        gui.setItem(4, enderChest);
        
        // 書見台（ランダムアイテム錬成）
        ItemStack lectern = new ItemStack(Material.LECTERN);
        ItemMeta lecternMeta = lectern.getItemMeta();
        if (lecternMeta != null) {
            lecternMeta.setDisplayName(ChatColor.GOLD + "アイテム錬成");
            lecternMeta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "レアリティごとにアイテムをランダム配布",
                ChatColor.GREEN + "COMMON から LEGENDARY まで",
                ChatColor.YELLOW + "クリックで錬成"
            ));
            lectern.setItemMeta(lecternMeta);
        }
        gui.setItem(6, lectern);
        
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
                
            case LECTERN:
                player.closeInventory();
                // ランダムアイテム錬成（クールダウンなし - 即座に発動）
                performRandomItemGeneration(player);
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
     * ランダムアイテム錬成を実行
     * @param player プレイヤー
     */
    private void performRandomItemGeneration(Player player) {
        Random random = new Random();
        
        // レアリティ決定（確率：COMMON:50%, UNCOMMON:30%, RARE:15%, EPIC:4%, LEGENDARY:1%）
        double rand = random.nextDouble();
        String rarity;
        ChatColor rarityColor;
        ItemStack[] possibleItems;
        
        if (rand < 0.01) { // 1%
            rarity = "LEGENDARY";
            rarityColor = ChatColor.GOLD;
            possibleItems = getLegendaryItems();
        } else if (rand < 0.05) { // 4%
            rarity = "EPIC";
            rarityColor = ChatColor.DARK_PURPLE;
            possibleItems = getEpicItems();
        } else if (rand < 0.20) { // 15%
            rarity = "RARE";
            rarityColor = ChatColor.BLUE;
            possibleItems = getRareItems();
        } else if (rand < 0.50) { // 30%
            rarity = "UNCOMMON";
            rarityColor = ChatColor.GREEN;
            possibleItems = getUncommonItems();
        } else { // 50%
            rarity = "COMMON";
            rarityColor = ChatColor.WHITE;
            possibleItems = getCommonItems();
        }
        
        // ランダムにアイテムを選択
        ItemStack selectedItem = possibleItems[random.nextInt(possibleItems.length)].clone();
        
        // アイテムを付与
        giveItemToPlayer(player, selectedItem, rarity, rarityColor);
    }
    
    /**
     * COMMONアイテムのリストを取得
     */
    private ItemStack[] getCommonItems() {
        return new ItemStack[]{
            new ItemStack(Material.FLINT, 5),
            createSpeedPotion(),
            createEnchantedBook("depth_strider", 1),
            new ItemStack(Material.CHORUS_FRUIT, 1),
            new ItemStack(Material.POTATO, 3),
            new ItemStack(Material.OAK_LOG, 4),
            new ItemStack(Material.TROPICAL_FISH, 1),
            new ItemStack(Material.GLASS_BOTTLE, 1), // Water Bottle
            new ItemStack(Material.SNOWBALL, 3),
            new ItemStack(Material.IRON_INGOT, 1)
        };
    }
    
    /**
     * UNCOMMONアイテムのリストを取得
     */
    private ItemStack[] getUncommonItems() {
        return new ItemStack[]{
            new ItemStack(Material.WIND_CHARGE, 1),
            new ItemStack(Material.CROSSBOW, 1),
            new ItemStack(Material.NETHER_WART, 1),
            createHealingPotion(),
            new ItemStack(Material.GLOWSTONE_DUST, 1),
            new ItemStack(Material.GUNPOWDER, 1),
            new ItemStack(Material.ENDER_PEARL, 1),
            new ItemStack(Material.EXPERIENCE_BOTTLE, 3),
            new ItemStack(Material.ARROW, 8),
            new ItemStack(Material.ANVIL, 1),
            new ItemStack(Material.GLASS_BOTTLE, 3), // Grass Bottle (placeholder)
            new ItemStack(Material.DRIED_KELP_BLOCK, 3),
            new ItemStack(Material.OAK_LOG, 32)
        };
    }
    
    /**
     * RAREアイテムのリストを取得
     */
    private ItemStack[] getRareItems() {
        return new ItemStack[]{
            createCustomItem("phantom_blade"),
            new ItemStack(Material.NETHER_WART, 2),
            new ItemStack(Material.SADDLE, 1),
            new ItemStack(Material.PAPER, 3),
            new ItemStack(Material.DIAMOND, 1),
            new ItemStack(Material.TRIDENT, 1),
            new ItemStack(Material.BREWING_STAND, 1),
            new ItemStack(Material.APPLE, 1),
            createCustomItem("lucky_box")
        };
    }
    
    /**
     * EPICアイテムのリストを取得
     */
    private ItemStack[] getEpicItems() {
        return new ItemStack[]{
            createCustomItem("lone_sword"),
            new ItemStack(Material.ENDER_CHEST, 1),
            createFeatherFallingBoots(),
            createCustomItem("enhanced_pickaxe"),
            createMultipleItems(createCustomItem("phantom_blade"), 2),
            createWitherSkeletonSet(),
            new ItemStack(Material.ENDER_EYE, 1),
            createHealingArrows()
        };
    }
    
    /**
     * LEGENDARYアイテムのリストを取得
     */
    private ItemStack[] getLegendaryItems() {
        return new ItemStack[]{
            createLegendarySet1(), // Phantom Blade x2 + Lone Sword x2 + Enchanted Golden Apple
            createLegendarySet2()  // Power IV Bow + Spectral Arrow x32
        };
    }
    
    // =============================================================================
    // ヘルパーメソッド
    // =============================================================================
    
    /**
     * プレイヤーにアイテムを付与
     */
    private void giveItemToPlayer(Player player, ItemStack item, String rarity, ChatColor rarityColor) {
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
            player.sendMessage(ChatColor.GOLD + "🎁 アイテム錬成成功！");
            player.sendMessage(rarityColor + "[" + rarity + "] " + ChatColor.WHITE + getItemName(item) + " を入手しました！");
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(ChatColor.GOLD + "🎁 アイテム錬成成功！");
            player.sendMessage(rarityColor + "[" + rarity + "] " + ChatColor.WHITE + getItemName(item) + " を足元にドロップしました！");
        }
        
        // レアリティ別エフェクト
        playRarityEffects(player, rarity);
    }
    
    /**
     * レアリティ別エフェクト
     */
    private void playRarityEffects(Player player, String rarity) {
        Location loc = player.getLocation();
        
        switch (rarity) {
            case "LEGENDARY":
                player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc.add(0, 1, 0), 30, 1, 1, 1, 0.1);
                break;
            case "EPIC":
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                player.getWorld().spawnParticle(Particle.ENCHANT, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                break;
            case "RARE":
                player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.CRIT, loc.add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.1);
                break;
            case "UNCOMMON":
                player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                break;
            default: // COMMON
                player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                player.getWorld().spawnParticle(Particle.ITEM, loc.add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.1);
                break;
        }
    }
    
    /**
     * カスタムアイテムを作成
     */
    private ItemStack createCustomItem(String itemType) {
        switch (itemType) {
            case "phantom_blade":
                return ItemRegistryAccessor.getPhantomBladeItem() != null ? 
                       ItemRegistryAccessor.getPhantomBladeItem().createItem() : 
                       new ItemStack(Material.IRON_SWORD);
            case "lone_sword":
                return ItemRegistryAccessor.getLoneSwordItem() != null ? 
                       ItemRegistryAccessor.getLoneSwordItem().createItem() : 
                       new ItemStack(Material.IRON_SWORD);
            case "enhanced_pickaxe":
                return ItemRegistryAccessor.getEnhancedPickaxeItem() != null ? 
                       ItemRegistryAccessor.getEnhancedPickaxeItem().createItem() : 
                       new ItemStack(Material.IRON_PICKAXE);
            case "lucky_box":
                return ItemRegistryAccessor.getLuckyBoxItem() != null ? 
                       ItemRegistryAccessor.getLuckyBoxItem().createItem() : 
                       new ItemStack(Material.NETHER_STAR);
            default:
                return new ItemStack(Material.STONE);
        }
    }
    
    /**
     * Speed II Splash Potion (30s) を作成
     */
    private ItemStack createSpeedPotion() {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1), true); // 30秒、レベル2
            meta.setDisplayName(ChatColor.AQUA + "スピードII スプラッシュポーション (30秒)");
            potion.setItemMeta(meta);
        }
        return potion;
    }
    
    /**
     * Healing I Potion を作成
     */
    private ItemStack createHealingPotion() {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0), true);
            meta.setDisplayName(ChatColor.RED + "治癒のポーション");
            potion.setItemMeta(meta);
        }
        return potion;
    }
    
    /**
     * エンチャント本を作成
     */
    private ItemStack createEnchantedBook(String enchantType, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = book.getItemMeta();
        if (meta != null) {
            switch (enchantType) {
                case "depth_strider":
                    meta.addEnchant(Enchantment.DEPTH_STRIDER, level, true);
                    break;
            }
            book.setItemMeta(meta);
        }
        return book;
    }
    
    /**
     * Feather Falling I Iron Boots を作成
     */
    private ItemStack createFeatherFallingBoots() {
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemMeta meta = boots.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.FEATHER_FALLING, 1, true);
            meta.setDisplayName(ChatColor.GRAY + "落下耐性I 鉄のブーツ");
            boots.setItemMeta(meta);
        }
        return boots;
    }
    
    /**
     * Wither Skeleton Skull + Soul Sand セットを作成
     */
    private ItemStack createWitherSkeletonSet() {
        // 代表アイテムとしてWither Skeleton Skullを返し、実際にはセットで付与
        return new ItemStack(Material.WITHER_SKELETON_SKULL, 1);
    }
    
    /**
     * Healing Arrow x5 を作成
     */
    private ItemStack createHealingArrows() {
        ItemStack arrows = new ItemStack(Material.TIPPED_ARROW, 5);
        PotionMeta meta = (PotionMeta) arrows.getItemMeta();
        if (meta != null) {
            meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 0), true);
            meta.setDisplayName(ChatColor.RED + "治癒の矢");
            arrows.setItemMeta(meta);
        }
        return arrows;
    }
    
    /**
     * 複数個のアイテムを作成
     */
    private ItemStack createMultipleItems(ItemStack base, int count) {
        ItemStack result = base.clone();
        result.setAmount(count);
        return result;
    }
    
    /**
     * Legendary Set 1: Phantom Blade x2 + Lone Sword x2 + Enchanted Golden Apple
     */
    private ItemStack createLegendarySet1() {
        // 代表アイテムとしてEnchanted Golden Appleを返す
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1);
    }
    
    /**
     * Legendary Set 2: Power IV Bow + Spectral Arrow x32
     */
    private ItemStack createLegendarySet2() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.POWER, 4, true);
            meta.setDisplayName(ChatColor.GOLD + "パワーIV 弓");
            bow.setItemMeta(meta);
        }
        return bow;
    }
    
    /**
     * アイテム名を取得
     */
    private String getItemName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().name().toLowerCase().replace("_", " ") + 
               (item.getAmount() > 1 ? " x" + item.getAmount() : "");
    }
}