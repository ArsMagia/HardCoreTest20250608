package magia.box.example.hardCoreTest20250608.core;

import magia.box.example.hardCoreTest20250608.commands.CustomItemsCMD;
import magia.box.example.hardCoreTest20250608.commands.LuckyCMD;
import magia.box.example.hardCoreTest20250608.commands.PloCMD;
import magia.box.example.hardCoreTest20250608.listener.BasicListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * プラグインの機能管理を行うマネージャークラス
 */
public class PluginManager {
    private final JavaPlugin plugin;
    private final ItemRegistry itemRegistry;
    
    public PluginManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.itemRegistry = new ItemRegistry(plugin);
    }
    
    /**
     * プラグインの初期化を実行
     */
    public void initialize() {
        // アイテム初期化
        itemRegistry.initializeItems();
        
        // コマンド登録
        registerCommands();
        
        // イベントリスナー登録
        registerEvents();
    }
    
    /**
     * アイテムレジストリを取得
     * @return ItemRegistry
     */
    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }
    
    /**
     * コマンドを登録
     */
    private void registerCommands() {
        Map<String, CommandExecutor> commands = new HashMap<>();
        commands.put("plo", new PloCMD());
        commands.put("lucky", new LuckyCMD(plugin));
        commands.put("custom", new CustomItemsCMD(plugin));
        
        registerCommandExecutors(commands);
    }
    
    /**
     * イベントリスナーを登録
     */
    private void registerEvents() {
        // 基本リスナー
        registerListener(new BasicListener());
        
        // カスタムアイテムのリスナー
        for (Listener listener : itemRegistry.getListeners()) {
            registerListener(listener);
        }
    }
    
    /**
     * コマンド実行者を登録
     * @param commands コマンドマップ
     */
    private void registerCommandExecutors(Map<String, CommandExecutor> commands) {
        for (Map.Entry<String, CommandExecutor> entry : commands.entrySet()) {
            plugin.getCommand(entry.getKey()).setExecutor(entry.getValue());
            
            // TabCompleterもセット（該当する場合）
            if (entry.getValue() instanceof TabCompleter) {
                plugin.getCommand(entry.getKey()).setTabCompleter((TabCompleter) entry.getValue());
            }
        }
    }
    
    /**
     * 単一のリスナーを登録
     * @param listener 登録するリスナー
     */
    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }
}