package magia.box.example.hardCoreTest20250608;

import magia.box.example.hardCoreTest20250608.config.WorldSetting;
import magia.box.example.hardCoreTest20250608.core.PluginManager;
import magia.box.example.hardCoreTest20250608.core.SchedulerManager;
import magia.box.example.hardCoreTest20250608.items.custom.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class HardCoreTest20250608 extends JavaPlugin {
    private static HardCoreTest20250608 instance;
    private PluginManager pluginManager;
    private SchedulerManager schedulerManager;

    public static HardCoreTest20250608 getInstance() {
        return instance;
    }
    
    /**
     * PluginManagerへのアクセスを提供（必要に応じて）
     * @return PluginManager
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public void onEnable() {
        // インスタンス設定
        instance = this;
        
        // 設定ファイル保存
        this.saveDefaultConfig();
        
        // プラグインマネージャー初期化
        pluginManager = new PluginManager(this);
        pluginManager.initialize();
        
        // スケジューラーマネージャー初期化
        schedulerManager = new SchedulerManager(this, pluginManager.getItemRegistry());
        schedulerManager.startAllTasks();
        
        // ワールド設定
        WorldSetting.setUp();
    }



    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

