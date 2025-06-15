package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 将来保証システムの管理クラス
 * LEGENDARY効果による次回効果保証を管理
 */
public class FutureGuaranteeManager {
    
    private static FutureGuaranteeManager instance;
    private final JavaPlugin plugin;
    
    // プレイヤーごとの保証情報
    private final Map<UUID, FutureGuarantee> guarantees = new HashMap<>();
    
    private FutureGuaranteeManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public static void initialize(JavaPlugin plugin) {
        if (instance == null) {
            instance = new FutureGuaranteeManager(plugin);
        }
    }
    
    public static FutureGuaranteeManager getInstance() {
        return instance;
    }
    
    /**
     * プレイヤーに将来保証を設定
     */
    public void setGuarantee(Player player, GuaranteeType type, int remainingUses) {
        guarantees.put(player.getUniqueId(), new FutureGuarantee(type, remainingUses));
        // デバッグ用ログ
        if (plugin != null) {
            plugin.getLogger().info("将来保証設定: " + player.getName() + " - " + type + " (" + remainingUses + "回)");
        }
    }
    
    /**
     * プレイヤーに保証があるかチェック
     */
    public boolean hasGuarantee(Player player) {
        FutureGuarantee guarantee = guarantees.get(player.getUniqueId());
        boolean hasGuarantee = guarantee != null && guarantee.getRemainingUses() > 0;
        // デバッグ用ログ
        if (plugin != null) {
            plugin.getLogger().info("保証チェック: " + player.getName() + " - " + hasGuarantee + 
                (guarantee != null ? " (残り" + guarantee.getRemainingUses() + "回)" : " (保証なし)"));
        }
        return hasGuarantee;
    }
    
    /**
     * 保証タイプを取得
     */
    public GuaranteeType getGuaranteeType(Player player) {
        FutureGuarantee guarantee = guarantees.get(player.getUniqueId());
        return guarantee != null ? guarantee.getType() : null;
    }
    
    /**
     * 保証を消費（使用回数を1減らす）
     */
    public void consumeGuarantee(Player player) {
        FutureGuarantee guarantee = guarantees.get(player.getUniqueId());
        if (guarantee != null) {
            int beforeUses = guarantee.getRemainingUses();
            guarantee.consumeUse();
            int afterUses = guarantee.getRemainingUses();
            
            // デバッグ用ログ
            if (plugin != null) {
                plugin.getLogger().info("保証消費: " + player.getName() + " - " + guarantee.getType() + 
                    " (残り" + beforeUses + "→" + afterUses + "回)");
            }
            
            if (guarantee.getRemainingUses() <= 0) {
                guarantees.remove(player.getUniqueId());
                if (plugin != null) {
                    plugin.getLogger().info("保証削除: " + player.getName() + " - " + guarantee.getType() + " (使用完了)");
                }
            }
        }
    }
    
    /**
     * 保証をクリア
     */
    public void clearGuarantee(Player player) {
        guarantees.remove(player.getUniqueId());
    }
    
    /**
     * 保証タイプ列挙
     */
    public enum GuaranteeType {
        STABLE_FUTURE,          // 安定した将来
        RUSH_ADDICTION,         // 突進中毒  
        TIME_LEAP,              // タイムリープ
        ADRENALINE_RUSH,        // アドレナリンラッシュ
        FUTURE_VISION,          // 未来視
        MALPHITE_ULT,           // マルファイトのULT
        UNLUCKY_CALAMITY        // もたらされた災厄（アンラッキー保証）
    }
    
    /**
     * 将来保証データクラス
     */
    private static class FutureGuarantee {
        private final GuaranteeType type;
        private int remainingUses;
        
        public FutureGuarantee(GuaranteeType type, int remainingUses) {
            this.type = type;
            this.remainingUses = remainingUses;
        }
        
        public GuaranteeType getType() {
            return type;
        }
        
        public int getRemainingUses() {
            return remainingUses;
        }
        
        public void consumeUse() {
            remainingUses--;
        }
    }
}