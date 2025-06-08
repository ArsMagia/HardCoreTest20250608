package magia.box.example.hardCoreTest20250608.core;

import magia.box.example.hardCoreTest20250608.items.custom.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * カスタムアイテムの登録と管理を行うレジストリクラス
 */
public class ItemRegistry {
    private final JavaPlugin plugin;
    private final Map<String, Object> items = new HashMap<>();
    
    public ItemRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 全てのカスタムアイテムを初期化・登録
     */
    public void initializeItems() {
        // カスタムアイテムの初期化
        items.put("grapple", new GrappleItem(plugin));
        items.put("lucky_box", new LuckyBoxItem(plugin));
        items.put("lone_sword", new LoneSwordItem(plugin));
        items.put("phantom_blade", new PhantomBladeItem(plugin));
        items.put("shuffle", new ShuffleItem(plugin));
        items.put("enhanced_pickaxe", new EnhancedPickaxeItem(plugin));
        items.put("player_tracking_compass", new PlayerTrackingCompassItem(plugin));
        items.put("special_multi_tool", new SpecialMultiToolItem(plugin));
    }
    
    /**
     * 指定されたキーのアイテムを取得
     * @param key アイテムキー
     * @param clazz 期待するクラス型
     * @return アイテムインスタンス
     */
    @SuppressWarnings("unchecked")
    public <T> T getItem(String key, Class<T> clazz) {
        Object item = items.get(key);
        if (item != null && clazz.isInstance(item)) {
            return (T) item;
        }
        throw new IllegalArgumentException("Item not found or wrong type: " + key);
    }
    
    /**
     * 全てのListenerインターフェースを実装するアイテムのリストを取得
     * @return Listenerのリスト
     */
    public List<Listener> getListeners() {
        List<Listener> listeners = new ArrayList<>();
        for (Object item : items.values()) {
            if (item instanceof Listener) {
                listeners.add((Listener) item);
            }
        }
        return listeners;
    }
    
    // 個別アイテムのgetterメソッド（下位互換性のため）
    public GrappleItem getGrappleItem() {
        return getItem("grapple", GrappleItem.class);
    }
    
    public LuckyBoxItem getLuckyBoxItem() {
        return getItem("lucky_box", LuckyBoxItem.class);
    }
    
    public LoneSwordItem getLoneSwordItem() {
        return getItem("lone_sword", LoneSwordItem.class);
    }
    
    public PhantomBladeItem getPhantomBladeItem() {
        return getItem("phantom_blade", PhantomBladeItem.class);
    }
    
    public ShuffleItem getShuffleItem() {
        return getItem("shuffle", ShuffleItem.class);
    }
    
    public EnhancedPickaxeItem getEnhancedPickaxeItem() {
        return getItem("enhanced_pickaxe", EnhancedPickaxeItem.class);
    }
    
    public PlayerTrackingCompassItem getPlayerTrackingCompassItem() {
        return getItem("player_tracking_compass", PlayerTrackingCompassItem.class);
    }
    
    public SpecialMultiToolItem getSpecialMultiToolItem() {
        return getItem("special_multi_tool", SpecialMultiToolItem.class);
    }
}