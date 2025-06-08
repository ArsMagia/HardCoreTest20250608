package magia.box.example.hardCoreTest20250608;

import magia.box.example.hardCoreTest20250608.commands.PloCMD;
import magia.box.example.hardCoreTest20250608.config.WorldSetting;
import magia.box.example.hardCoreTest20250608.items.*;
import magia.box.example.hardCoreTest20250608.listener.BasicListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public final class HardCoreTest20250608 extends JavaPlugin {
    private static HardCoreTest20250608 instance;
    private static GrappleItem grappleItem;
    private static LuckyBoxItem luckyBoxItem;
    private static LoneSwordItem loneSwordItem;
    private static PhantomBladeItem phantomBladeItem;
    private static ShuffleItem shuffleItem;

    public static HardCoreTest20250608 getInstance() {
        return instance;
    }

    public static GrappleItem getGrappleItem() {
        return grappleItem;
    }

    public static LuckyBoxItem getLuckyBoxItem() {
        return luckyBoxItem;
    }

    public static LoneSwordItem getLoneSwordItem() {
        return loneSwordItem;
    }

    public static PhantomBladeItem getPhantomBladeItem() {
        return phantomBladeItem;
    }

    public static ShuffleItem getShuffleItem() {
        return shuffleItem;
    }

    @Override
    public void onEnable() {
        // -- Config --
        instance = this;
        this.saveDefaultConfig();

        // -- Custom Items --
        grappleItem = new GrappleItem(this);
        luckyBoxItem = new LuckyBoxItem(this);
        loneSwordItem = new LoneSwordItem(this);
        phantomBladeItem = new PhantomBladeItem(this);
        shuffleItem = new ShuffleItem(this);

        // -- Commands --
        Map<String, CommandExecutor> commands = new HashMap<>();
        commands.put("plo", new PloCMD());
        registerCommand(commands);

        registerEvents(
                new BasicListener(),
                luckyBoxItem,
                loneSwordItem,
                phantomBladeItem,
                shuffleItem
        );

        // -- Scheduler: 1分間隔でラッキーボックス配布 --
        startLuckyBoxDistribution();
        WorldSetting.setUp();


    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommand(Map<String, CommandExecutor> commands) {
        for (Map.Entry<String, CommandExecutor> entry : commands.entrySet()) {
            getCommand(entry.getKey()).setExecutor(entry.getValue());

            // TabCompleterもセット（該当する場合）
            if (entry.getValue() instanceof TabCompleter) {
                getCommand(entry.getKey()).setTabCompleter((TabCompleter) entry.getValue());
            }
        }
    }

    private void startLuckyBoxDistribution() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // オンラインの全プレイヤーにラッキーボックスを配布
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ItemStack luckyBox = luckyBoxItem.createItem();
                    player.getInventory().addItem(luckyBox);
                }
                
                // サーバー全体に通知
                Bukkit.broadcastMessage("");
                Bukkit.broadcastMessage(ChatColor.GOLD + "⭐ " + ChatColor.YELLOW + "定期配布！" + 
                        ChatColor.GRAY + " 全プレイヤーに " + ChatColor.LIGHT_PURPLE + "ラッキーボックス" + 
                        ChatColor.GRAY + " が配布されました！");
                Bukkit.broadcastMessage(ChatColor.GRAY + "右クリックで運試しをしてみましょう...");
                Bukkit.broadcastMessage("");
            }
        }.runTaskTimer(this, 20L * 60, 20L * 60); // 1分後に開始、1分間隔で実行
    }
}

