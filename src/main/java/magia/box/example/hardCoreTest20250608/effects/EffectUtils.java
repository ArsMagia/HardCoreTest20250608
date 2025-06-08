package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 効果システム全体で使用されるユーティリティメソッド集
 * 
 * @author HardCoreTest20250608
 * @since 1.0
 */
public final class EffectUtils {
    
    /**
     * 効果発動のブロードキャストメッセージを送信
     * 
     * @param player 効果を発動したプレイヤー
     * @param effectName 効果名
     * @param rarity レアリティ
     * @param isLucky ラッキー効果かどうか
     */
    public static void broadcastEffectMessage(Player player, String effectName, EffectRarity rarity, boolean isLucky) {
        String template = isLucky ? EffectConstants.LUCKY_BROADCAST_TEMPLATE : EffectConstants.UNLUCKY_BROADCAST_TEMPLATE;
        String message = String.format(template, player.getName(), rarity.getColoredName(), effectName);
        Bukkit.broadcastMessage(message);
    }
    
    /**
     * クールダウンメッセージを送信
     * 
     * @param player メッセージ送信対象
     * @param itemName アイテム名
     * @param remainingTimeMs 残り時間（ミリ秒）
     */
    public static void sendCooldownMessage(Player player, String itemName, long remainingTimeMs) {
        double remainingSeconds = remainingTimeMs / 1000.0;
        String message = String.format(EffectConstants.COOLDOWN_MESSAGE_TEMPLATE, itemName, remainingSeconds);
        player.sendMessage(message);
    }
    
    /**
     * 体力値が有効範囲内かチェック
     * 
     * @param health チェック対象の体力値
     * @return 有効範囲内の場合true
     */
    public static boolean isValidHealth(double health) {
        return health >= EffectConstants.MIN_HEALTH_VALUE && health <= EffectConstants.MAX_HEALTH_VALUE;
    }
    
    /**
     * プレイヤーの現在の最大体力値を取得
     * 
     * @param player 対象プレイヤー
     * @return 最大体力値、取得できない場合は-1
     */
    public static double getMaxHealth(Player player) {
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        return healthAttr != null ? healthAttr.getBaseValue() : -1;
    }
    
    /**
     * プレイヤーの最大体力を安全に設定
     * 
     * @param player 対象プレイヤー
     * @param newMaxHealth 新しい最大体力値
     * @return 設定に成功した場合true
     */
    public static boolean setMaxHealthSafely(Player player, double newMaxHealth) {
        if (!isValidHealth(newMaxHealth)) {
            return false;
        }
        
        AttributeInstance healthAttr = player.getAttribute(Attribute.MAX_HEALTH);
        if (healthAttr == null) {
            return false;
        }
        
        healthAttr.setBaseValue(newMaxHealth);
        
        // 現在体力が新しい上限を超えている場合は調整
        if (player.getHealth() > newMaxHealth) {
            player.setHealth(newMaxHealth);
        }
        
        return true;
    }
    
    /**
     * アイテムを安全にプレイヤーのメインハンドに設定
     * インベントリが満杯の場合は足元にドロップ
     * 
     * @param player 対象プレイヤー
     * @param item 設定するアイテム
     */
    public static void safeSetItemInMainHand(Player player, ItemStack item) {
        try {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().setItemInMainHand(item);
            }
        } catch (Exception e) {
            // フォールバック: 足元にドロップ
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
    
    /**
     * アイテムを安全にプレイヤーのインベントリに追加
     * インベントリが満杯の場合は足元にドロップ
     * 
     * @param player 対象プレイヤー
     * @param item 追加するアイテム
     * @return インベントリに追加された場合true、ドロップされた場合false
     */
    public static boolean safeGiveItem(Player player, ItemStack item) {
        try {
            if (player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
                return false;
            } else {
                player.getInventory().addItem(item);
                return true;
            }
        } catch (Exception e) {
            // フォールバック: 足元にドロップ
            player.getWorld().dropItem(player.getLocation(), item);
            return false;
        }
    }
    
    /**
     * プレイヤーがオンラインかつ有効な状態かチェック
     * 
     * @param player チェック対象プレイヤー
     * @return 有効な場合true
     */
    public static boolean isPlayerValid(Player player) {
        return player != null && player.isOnline() && player.isValid();
    }
    
    /**
     * アイテムの表示名を取得（カスタム名またはマテリアル名）
     * 
     * @param item 対象アイテム
     * @return 表示名
     */
    public static String getItemDisplayName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "空気";
        }
        
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        
        String materialName = item.getType().name().toLowerCase().replace('_', ' ');
        return materialName.substring(0, 1).toUpperCase() + materialName.substring(1);
    }
    
    /**
     * クールダウンをチェックし、必要に応じてメッセージを送信
     * 
     * @param player 対象プレイヤー
     * @param lastActivationTime 最後の実行時間
     * @param cooldownMs クールダウン時間（ミリ秒）
     * @param itemName アイテム名
     * @return クールダウン中の場合true
     */
    public static boolean checkCooldown(Player player, Long lastActivationTime, long cooldownMs, String itemName) {
        if (lastActivationTime == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - lastActivationTime;
        
        if (elapsed < cooldownMs) {
            sendCooldownMessage(player, itemName, cooldownMs - elapsed);
            return true;
        }
        
        return false;
    }
    
    /**
     * 耐久度を安全に消費し、必要に応じてアイテムを破壊
     * 
     * @param player 対象プレイヤー
     * @param item 対象アイテム
     * @param durabilityCost 消費耐久度
     * @param itemName アイテム名（メッセージ用）
     * @return アイテムが破壊された場合true
     */
    public static boolean consumeDurabilityOrBreak(Player player, ItemStack item, int durabilityCost, String itemName) {
        if (!(item.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable)) {
            return false;
        }
        
        org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
        int currentDamage = damageable.getDamage();
        int maxDurability = item.getType().getMaxDurability();
        int remainingDurability = maxDurability - currentDamage;
        
        if (remainingDurability >= durabilityCost) {
            // 通常の耐久度消費
            damageable.setDamage(currentDamage + durabilityCost);
            item.setItemMeta(damageable);
            return false;
        } else {
            // アイテム破壊
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            player.sendMessage(ChatColor.YELLOW + itemName + "が壊れました！最後の力で効果を発動しました。");
            return true;
        }
    }
    
    /**
     * プライベートコンストラクタ（インスタンス化を防ぐ）
     */
    private EffectUtils() {
        throw new AssertionError("EffectUtils should not be instantiated");
    }
}