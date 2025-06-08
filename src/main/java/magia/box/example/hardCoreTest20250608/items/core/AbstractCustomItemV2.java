package magia.box.example.hardCoreTest20250608.items.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCustomItemV2 implements Listener {
    
    protected final JavaPlugin plugin;
    protected final String itemId;
    protected final String displayName;
    protected final Material material;
    protected final ItemRarity rarity;
    protected final List<String> lore;
    protected final List<String> hints;
    protected final NamespacedKey customItemKey;

    public AbstractCustomItemV2(JavaPlugin plugin, ItemBuilder builder) {
        this.plugin = plugin;
        this.itemId = builder.itemId;
        this.displayName = builder.displayName;
        this.material = builder.material;
        this.rarity = builder.rarity;
        this.lore = new ArrayList<>(builder.lore);
        this.hints = new ArrayList<>(builder.hints);
        this.customItemKey = new NamespacedKey(plugin, "custom_item");
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static ItemBuilder builder(String itemId, String displayName) {
        return new ItemBuilder(itemId, displayName);
    }

    public boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(customItemKey)) {
            return false;
        }
        
        String storedId = meta.getPersistentDataContainer().get(customItemKey, PersistentDataType.STRING);
        return itemId.equals(storedId);
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(rarity.getColor() + displayName);
            
            List<String> finalLore = new ArrayList<>();
            finalLore.addAll(lore);
            if (!hints.isEmpty()) {
                finalLore.add("");
                finalLore.addAll(hints);
            }
            finalLore.add("");
            finalLore.add(ChatColor.GRAY + "Rarity: " + rarity.getColor() + rarity.getDisplayName());
            
            meta.setLore(finalLore);
            meta.getPersistentDataContainer().set(customItemKey, PersistentDataType.STRING, itemId);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    public static class ItemBuilder {
        private final String itemId;
        private final String displayName;
        private Material material = Material.STICK;
        private ItemRarity rarity = ItemRarity.COMMON;
        private final List<String> lore = new ArrayList<>();
        private final List<String> hints = new ArrayList<>();

        public ItemBuilder(String itemId, String displayName) {
            this.itemId = itemId;
            this.displayName = displayName;
        }

        public ItemBuilder material(Material material) {
            this.material = material;
            return this;
        }

        public ItemBuilder rarity(ItemRarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public ItemBuilder addLore(String line) {
            this.lore.add(ChatColor.GRAY + line);
            return this;
        }

        public ItemBuilder addHint(String hint) {
            this.hints.add(ChatColor.YELLOW + hint);
            return this;
        }
    }
}