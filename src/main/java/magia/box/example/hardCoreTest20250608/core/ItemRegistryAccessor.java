package magia.box.example.hardCoreTest20250608.core;

import magia.box.example.hardCoreTest20250608.HardCoreTest20250608;
import magia.box.example.hardCoreTest20250608.items.custom.*;

/**
 * ItemRegistryへの静的アクセスを提供するユーティリティクラス
 * 下位互換性を保ちつつ、メインクラスから責任を分離
 */
public final class ItemRegistryAccessor {
    
    private ItemRegistryAccessor() {
        // ユーティリティクラスのためのprivateコンストラクタ
    }
    
    /**
     * プラグインインスタンスからItemRegistryを取得
     * @return ItemRegistry
     */
    private static ItemRegistry getRegistry() {
        HardCoreTest20250608 plugin = HardCoreTest20250608.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("Plugin instance is not initialized");
        }
        return plugin.getPluginManager().getItemRegistry();
    }
    
    // 各アイテムへの静的アクセサ
    public static GrappleItem getGrappleItem() {
        return getRegistry().getGrappleItem();
    }
    
    public static LuckyBoxItem getLuckyBoxItem() {
        return getRegistry().getLuckyBoxItem();
    }
    
    public static LoneSwordItem getLoneSwordItem() {
        return getRegistry().getLoneSwordItem();
    }
    
    public static PhantomBladeItem getPhantomBladeItem() {
        return getRegistry().getPhantomBladeItem();
    }
    
    public static ShuffleItem getShuffleItem() {
        return getRegistry().getShuffleItem();
    }
    
    public static EnhancedPickaxeItem getEnhancedPickaxeItem() {
        return getRegistry().getEnhancedPickaxeItem();
    }
    
    public static PlayerTrackingCompassItem getPlayerTrackingCompassItem() {
        return getRegistry().getPlayerTrackingCompassItem();
    }
    
    public static SpecialMultiToolItem getSpecialMultiToolItem() {
        return getRegistry().getSpecialMultiToolItem();
    }
    
    public static HealKitItem getHealKitItem() {
        return getRegistry().getHealKitItem();
    }
}