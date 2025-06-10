package magia.box.example.hardCoreTest20250608.commands;

import magia.box.example.hardCoreTest20250608.effects.EffectRegistry;
import magia.box.example.hardCoreTest20250608.effects.LuckyEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuckyTestGUI implements Listener {
    
    private final JavaPlugin plugin;
    private final EffectRegistry effectRegistry;
    private static final String LUCKY_GUI_TITLE = ChatColor.GREEN + "ラッキー効果テスト";
    private static final String UNLUCKY_GUI_TITLE = ChatColor.RED + "アンラッキー効果テスト";
    
    public LuckyTestGUI(JavaPlugin plugin, EffectRegistry effectRegistry) {
        this.plugin = plugin;
        this.effectRegistry = effectRegistry;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public EffectRegistry getEffectRegistry() {
        return effectRegistry;
    }
    
    public void openLuckyEffectsGUI(Player player, int page) {
        List<LuckyEffect> luckyEffects = effectRegistry.getAllLuckyEffects();
        openEffectsGUI(player, luckyEffects, LUCKY_GUI_TITLE, page, true);
    }
    
    public void openUnluckyEffectsGUI(Player player, int page) {
        List<LuckyEffect> unluckyEffects = effectRegistry.getAllUnluckyEffects();
        openEffectsGUI(player, unluckyEffects, UNLUCKY_GUI_TITLE, page, false);
    }
    
    private void openEffectsGUI(Player player, List<LuckyEffect> effects, String title, int page, boolean isLucky) {
        int itemsPerPage = 45; // 9x5のスペース（最下段はナビゲーション用）
        int totalPages = (int) Math.ceil((double) effects.size() / itemsPerPage);
        
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;
        
        String fullTitle = title + " (ページ " + page + "/" + totalPages + ")";
        Inventory gui = Bukkit.createInventory(null, 54, fullTitle);
        
        // 効果アイテムを配置
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, effects.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            LuckyEffect effect = effects.get(i);
            ItemStack item = createEffectItem(effect, isLucky);
            gui.setItem(i - startIndex, item);
        }
        
        // ナビゲーションアイテムを配置
        setupNavigation(gui, page, totalPages, isLucky);
        
        player.openInventory(gui);
    }
    
    private ItemStack createEffectItem(LuckyEffect effect, boolean isLucky) {
        Material material = getRarityMaterial(effect.getRarity(), isLucky);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(effect.getRarity().getColor() + effect.getDescription());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "レアリティ: " + effect.getRarity().getColoredName());
            lore.add(ChatColor.GRAY + "重み: " + effect.getWeight());
            lore.add(ChatColor.GRAY + "タイプ: " + (isLucky ? 
                ChatColor.GREEN + "ラッキー" : ChatColor.RED + "アンラッキー"));
            lore.add("");
            lore.add(ChatColor.YELLOW + "クリックで効果を発動！");
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    private Material getRarityMaterial(magia.box.example.hardCoreTest20250608.effects.EffectRarity rarity, boolean isLucky) {
        switch (rarity) {
            case LEGENDARY:
                return isLucky ? Material.NETHER_STAR : Material.WITHER_SKELETON_SKULL;
            case EPIC:
                return isLucky ? Material.DIAMOND : Material.OBSIDIAN;
            case RARE:
                return isLucky ? Material.GOLD_INGOT : Material.NETHERITE_SCRAP;
            case UNCOMMON:
                return isLucky ? Material.IRON_INGOT : Material.COAL;
            case COMMON:
            default:
                return isLucky ? Material.EMERALD : Material.REDSTONE;
        }
    }
    
    private void setupNavigation(Inventory gui, int currentPage, int totalPages, boolean isLucky) {
        // 前のページ
        if (currentPage > 1) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta meta = prevPage.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + "前のページ");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "ページ " + (currentPage - 1) + " に移動");
                meta.setLore(lore);
                prevPage.setItemMeta(meta);
            }
            gui.setItem(45, prevPage);
        }
        
        // 現在のページ情報
        ItemStack pageInfo = new ItemStack(Material.BOOK);
        ItemMeta meta = pageInfo.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "現在のページ: " + currentPage + "/" + totalPages);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "効果タイプ: " + 
                (isLucky ? ChatColor.GREEN + "ラッキー" : ChatColor.RED + "アンラッキー"));
            meta.setLore(lore);
            pageInfo.setItemMeta(meta);
        }
        gui.setItem(49, pageInfo);
        
        // 次のページ
        if (currentPage < totalPages) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "次のページ");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "ページ " + (currentPage + 1) + " に移動");
                nextMeta.setLore(lore);
                nextPage.setItemMeta(nextMeta);
            }
            gui.setItem(53, nextPage);
        }
        
        // 戻るボタン
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "閉じる");
            back.setItemMeta(backMeta);
        }
        gui.setItem(47, back);
        
        // 切り替えボタン
        ItemStack toggle = new ItemStack(isLucky ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK);
        ItemMeta toggleMeta = toggle.getItemMeta();
        if (toggleMeta != null) {
            toggleMeta.setDisplayName(ChatColor.GOLD + (isLucky ? "アンラッキー効果を見る" : "ラッキー効果を見る"));
            toggle.setItemMeta(toggleMeta);
        }
        gui.setItem(51, toggle);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        if (!title.contains("効果テスト")) return;
        
        event.setCancelled(true);
        
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        
        ItemStack clickedItem = event.getCurrentItem();
        ItemMeta meta = clickedItem.getItemMeta();
        
        if (meta == null || meta.getDisplayName() == null) return;
        
        boolean isLucky = title.contains(LUCKY_GUI_TITLE);
        int currentPage = extractPageFromTitle(title);
        
        // ナビゲーションアイテムの処理
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        
        if (displayName.equals("前のページ")) {
            if (isLucky) {
                openLuckyEffectsGUI(player, currentPage - 1);
            } else {
                openUnluckyEffectsGUI(player, currentPage - 1);
            }
            return;
        }
        
        if (displayName.equals("次のページ")) {
            if (isLucky) {
                openLuckyEffectsGUI(player, currentPage + 1);
            } else {
                openUnluckyEffectsGUI(player, currentPage + 1);
            }
            return;
        }
        
        if (displayName.equals("閉じる")) {
            player.closeInventory();
            return;
        }
        
        if (displayName.equals("アンラッキー効果を見る")) {
            openUnluckyEffectsGUI(player, 1);
            return;
        }
        
        if (displayName.equals("ラッキー効果を見る")) {
            openLuckyEffectsGUI(player, 1);
            return;
        }
        
        // 効果の実行
        executeEffect(player, meta.getDisplayName(), isLucky);
    }
    
    private int extractPageFromTitle(String title) {
        try {
            int startIndex = title.indexOf("ページ ") + 3;
            int endIndex = title.indexOf("/", startIndex);
            return Integer.parseInt(title.substring(startIndex, endIndex));
        } catch (Exception e) {
            return 1;
        }
    }
    
    private void executeEffect(Player player, String effectDisplayName, boolean isLucky) {
        String cleanDisplayName = ChatColor.stripColor(effectDisplayName);
        
        List<LuckyEffect> effects = isLucky ? 
            effectRegistry.getAllLuckyEffects() : 
            effectRegistry.getAllUnluckyEffects();
        
        for (LuckyEffect effect : effects) {
            if (effect.getDescription().equals(cleanDisplayName)) {
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "[テスト] " + 
                    (isLucky ? ChatColor.GREEN + "ラッキー" : ChatColor.RED + "アンラッキー") + 
                    ChatColor.GOLD + "効果を発動: " + ChatColor.WHITE + cleanDisplayName);
                
                String result = effect.apply(player);
                player.sendMessage(ChatColor.GRAY + "効果結果: " + result);
                return;
            }
        }
        
        player.sendMessage(ChatColor.RED + "効果が見つかりませんでした: " + cleanDisplayName);
    }
}