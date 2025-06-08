package magia.box.example.hardCoreTest20250608.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 定期実行タスクを管理するマネージャークラス
 */
public class SchedulerManager {
    private final JavaPlugin plugin;
    private final ItemRegistry itemRegistry;
    
    // ラッキーボックス配布の設定
    private static final long DISTRIBUTION_DELAY_TICKS = 20L * 60; // 1分（1200ティック）
    private static final long DISTRIBUTION_INTERVAL_TICKS = 20L * 60; // 1分間隔
    
    public SchedulerManager(JavaPlugin plugin, ItemRegistry itemRegistry) {
        this.plugin = plugin;
        this.itemRegistry = itemRegistry;
    }
    
    /**
     * 全ての定期タスクを開始
     */
    public void startAllTasks() {
        startLuckyBoxDistribution();
    }
    
    /**
     * ラッキーボックスの定期配布を開始
     */
    private void startLuckyBoxDistribution() {
        new BukkitRunnable() {
            @Override
            public void run() {
                distributeLuckyBoxes();
            }
        }.runTaskTimer(plugin, DISTRIBUTION_DELAY_TICKS, DISTRIBUTION_INTERVAL_TICKS);
    }
    
    /**
     * オンラインプレイヤーにラッキーボックスを配布
     */
    private void distributeLuckyBoxes() {
        // オンラインの全プレイヤーにラッキーボックスを配布
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack luckyBox = itemRegistry.getLuckyBoxItem().createItem();
            player.getInventory().addItem(luckyBox);
        }
        
        // サーバー全体に通知
        broadcastDistributionMessage();
    }
    
    /**
     * 配布通知メッセージをブロードキャスト
     */
    private void broadcastDistributionMessage() {
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GOLD + "⭐ " + ChatColor.YELLOW + "定期配布！" + 
                ChatColor.GRAY + " 全プレイヤーに " + ChatColor.LIGHT_PURPLE + "ラッキーボックス" + 
                ChatColor.GRAY + " が配布されました！");
        Bukkit.broadcastMessage(ChatColor.GRAY + "右クリックで運試しをしてみましょう...");
        Bukkit.broadcastMessage("");
    }
}