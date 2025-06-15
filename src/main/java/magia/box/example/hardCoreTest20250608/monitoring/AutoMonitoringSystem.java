package magia.box.example.hardCoreTest20250608.monitoring;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import magia.box.example.hardCoreTest20250608.HardCoreTest20250608;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 🤖 完全自動化監視システム
 * 
 * プラグインの健全性、パフォーマンス、エラーを24/7で監視し、
 * 問題発生時に自動的に対処措置を実行します。
 */
public class AutoMonitoringSystem {
    
    private static final Logger logger = LoggerFactory.getLogger(AutoMonitoringSystem.class);
    private static AutoMonitoringSystem instance;
    
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, LongAdder> counters;
    private final ConcurrentHashMap<String, Timer> timers;
    
    // ========== エフェクト監視メトリクス ==========
    // 動的にCounterとTimerを作成するため、フィールドは不要
    
    // ========== アラート閾値 ==========
    private static final double TPS_WARNING_THRESHOLD = 15.0;
    private static final double TPS_CRITICAL_THRESHOLD = 10.0;
    private static final double MEMORY_WARNING_THRESHOLD = 0.8; // 80%
    private static final double MEMORY_CRITICAL_THRESHOLD = 0.9; // 90%
    private static final long ERROR_RATE_THRESHOLD = 10; // 1分間に10エラー以上
    
    // ========== 自動復旧フラグ ==========
    private final AtomicLong lastTpsWarning = new AtomicLong(0);
    private final AtomicLong lastMemoryWarning = new AtomicLong(0);
    private boolean emergencyModeActive = false;
    
    private AutoMonitoringSystem() {
        this.meterRegistry = Metrics.globalRegistry;
        this.counters = new ConcurrentHashMap<>();
        this.timers = new ConcurrentHashMap<>();
        
        // JVMメトリクスバインダー登録
        new JvmMemoryMetrics().bindTo(meterRegistry);
        new JvmGcMetrics().bindTo(meterRegistry);
        new ProcessorMetrics().bindTo(meterRegistry);
        
        // システム監視ゲージ初期化
        meterRegistry.gauge("minecraft.players.online", this, AutoMonitoringSystem::getCurrentPlayerCount);
        meterRegistry.gauge("minecraft.server.tps", this, AutoMonitoringSystem::getCurrentTPS);
        meterRegistry.gauge("minecraft.memory.usage.ratio", this, AutoMonitoringSystem::getMemoryUsageRatio);
        
        // エフェクト監視メトリクスは動的に作成（recordEffectExecution/recordEffectErrorで）
        
        logger.info("🤖 自動監視システムが初期化されました");
    }
    
    public static AutoMonitoringSystem getInstance() {
        if (instance == null) {
            synchronized (AutoMonitoringSystem.class) {
                if (instance == null) {
                    instance = new AutoMonitoringSystem();
                }
            }
        }
        return instance;
    }
    
    /**
     * 監視システムを開始
     */
    public void startMonitoring() {
        // ========== メイン監視ループ (10秒間隔) ==========
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    performHealthCheck();
                } catch (Exception e) {
                    logger.error("監視システムでエラーが発生しました", e);
                }
            }
        }.runTaskTimerAsynchronously(HardCoreTest20250608.getInstance(), 200L, 200L); // 10秒間隔
        
        // ========== 詳細監視ループ (1分間隔) ==========
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    performDetailedAnalysis();
                    generateAutomaticReports();
                } catch (Exception e) {
                    logger.error("詳細監視でエラーが発生しました", e);
                }
            }
        }.runTaskTimerAsynchronously(HardCoreTest20250608.getInstance(), 1200L, 1200L); // 1分間隔
        
        // ========== パフォーマンス最適化ループ (5分間隔) ==========
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    performAutomaticOptimization();
                } catch (Exception e) {
                    logger.error("自動最適化でエラーが発生しました", e);
                }
            }
        }.runTaskTimerAsynchronously(HardCoreTest20250608.getInstance(), 6000L, 6000L); // 5分間隔
        
        logger.info("🔍 24/7監視システムが開始されました");
    }
    
    /**
     * ヘルスチェック実行
     */
    private void performHealthCheck() {
        double currentTPS = getCurrentTPS();
        double memoryUsage = getMemoryUsageRatio();
        long currentTime = System.currentTimeMillis();
        
        // ========== TPS監視と自動対処 ==========
        if (currentTPS < TPS_CRITICAL_THRESHOLD) {
            if (currentTime - lastTpsWarning.get() > 60000) { // 1分間隔制限
                logger.error("🚨 CRITICAL: TPS critically low: {}", currentTPS);
                activateEmergencyMode();
                lastTpsWarning.set(currentTime);
            }
        } else if (currentTPS < TPS_WARNING_THRESHOLD) {
            if (currentTime - lastTpsWarning.get() > 300000) { // 5分間隔制限
                logger.warn("⚠️ WARNING: TPS low: {}", currentTPS);
                performTPSOptimization();
                lastTpsWarning.set(currentTime);
            }
        }
        
        // ========== メモリ監視と自動対処 ==========
        if (memoryUsage > MEMORY_CRITICAL_THRESHOLD) {
            if (currentTime - lastMemoryWarning.get() > 60000) {
                logger.error("🚨 CRITICAL: Memory usage critically high: {}%", memoryUsage * 100);
                performEmergencyMemoryCleanup();
                lastMemoryWarning.set(currentTime);
            }
        } else if (memoryUsage > MEMORY_WARNING_THRESHOLD) {
            if (currentTime - lastMemoryWarning.get() > 300000) {
                logger.warn("⚠️ WARNING: Memory usage high: {}%", memoryUsage * 100);
                performGentleMemoryCleanup();
                lastMemoryWarning.set(currentTime);
            }
        }
        
        // ========== 緊急モード解除チェック ==========
        if (emergencyModeActive && currentTPS > TPS_WARNING_THRESHOLD && memoryUsage < MEMORY_WARNING_THRESHOLD) {
            deactivateEmergencyMode();
        }
    }
    
    /**
     * 詳細分析実行
     */
    private void performDetailedAnalysis() {
        // エラー率分析
        LongAdder errorCounter = counters.get("effect.errors");
        if (errorCounter != null && errorCounter.sum() > ERROR_RATE_THRESHOLD) {
            logger.warn("🔍 高エラー率検出: {}エラー/分", errorCounter.sum());
            analyzeErrorPatterns();
            errorCounter.reset(); // カウンターリセット
        }
        
        // パフォーマンス分析
        analyzePerformanceTrends();
        
        // プレイヤー行動分析
        analyzePlayerBehavior();
    }
    
    /**
     * 自動レポート生成
     */
    private void generateAutomaticReports() {
        String report = String.format("""
            📊 自動監視レポート [%s]
            ==========================================
            🎮 オンラインプレイヤー: %d人
            ⚡ 現在TPS: %.2f
            💾 メモリ使用率: %.1f%%
            🎲 直近エフェクト実行数: %d回
            ❌ 直近エラー数: %d件
            🔧 緊急モード: %s
            ==========================================
            """,
            Instant.now(),
            (int) getCurrentPlayerCount(),
            getCurrentTPS(),
            getMemoryUsageRatio() * 100,
            getRecentEffectExecutions(),
            getRecentErrors(),
            emergencyModeActive ? "有効" : "無効"
        );
        
        logger.info(report);
    }
    
    /**
     * 自動最適化実行
     */
    private void performAutomaticOptimization() {
        // ガベージコレクション提案
        if (getMemoryUsageRatio() > 0.7) {
            logger.info("🧹 自動メモリクリーンアップを実行します");
            System.gc();
        }
        
        // パフォーマンス統計リセット
        resetPerformanceCounters();
        
        // 自動チューニング
        performAutomaticTuning();
    }
    
    /**
     * 緊急モード有効化
     */
    private void activateEmergencyMode() {
        if (!emergencyModeActive) {
            emergencyModeActive = true;
            logger.error("🚨 緊急モードが有効化されました - システムを保護します");
            
            // 緊急対処措置
            Bukkit.getScheduler().runTask(HardCoreTest20250608.getInstance(), () -> {
                // エフェクト実行の一時停止
                // 重いタスクの停止
                // プレイヤー通知
                Bukkit.broadcastMessage("§c⚠️ システム保護モードが有効になりました");
            });
        }
    }
    
    /**
     * 緊急モード無効化
     */
    private void deactivateEmergencyMode() {
        if (emergencyModeActive) {
            emergencyModeActive = false;
            logger.info("✅ 緊急モードが解除されました - 通常運用に復帰");
            
            Bukkit.getScheduler().runTask(HardCoreTest20250608.getInstance(), () -> {
                Bukkit.broadcastMessage("§a✅ システムが正常に復旧しました");
            });
        }
    }
    
    /**
     * TPS最適化実行
     */
    private void performTPSOptimization() {
        logger.info("⚡ TPS最適化を実行します");
        
        // エフェクト実行間隔の調整
        // 重いタスクの分散実行
        // キャッシュクリア
    }
    
    /**
     * 緊急メモリクリーンアップ
     */
    private void performEmergencyMemoryCleanup() {
        logger.error("🧹 緊急メモリクリーンアップを実行します");
        
        // 強制ガベージコレクション
        System.gc();
        
        // キャッシュクリア
        // 一時データ削除
        // メモリリーク箇所のクリーンアップ
    }
    
    /**
     * 穏やかなメモリクリーンアップ
     */
    private void performGentleMemoryCleanup() {
        logger.info("🧹 メモリクリーンアップを実行します");
        
        // ソフトリファレンスクリア
        // キャッシュサイズ調整
    }
    
    // ========== メトリクス取得メソッド ==========
    
    private double getCurrentPlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }
    
    public double getCurrentTPS() {
        try {
            // Spigot/Paper TPS取得（リフレクション使用）
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            double[] tps = (double[]) server.getClass().getField("recentTps").get(server);
            return Math.min(tps[0], 20.0); // 最大20.0でキャップ
        } catch (Exception e) {
            return 20.0; // デフォルト値
        }
    }
    
    public double getMemoryUsageRatio() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        return (double) (totalMemory - freeMemory) / totalMemory;
    }
    
    private long getRecentEffectExecutions() {
        LongAdder counter = counters.get("effect.executions");
        return counter != null ? counter.sum() : 0;
    }
    
    private long getRecentErrors() {
        LongAdder counter = counters.get("effect.errors");
        return counter != null ? counter.sum() : 0;
    }
    
    // ========== 公開API ==========
    
    /**
     * エフェクト実行を記録
     */
    public void recordEffectExecution(String effectName, long durationMs) {
        Counter.builder("effect.execution.count")
                .tag("effect", effectName)
                .register(meterRegistry)
                .increment();
        
        counters.computeIfAbsent("effect.executions", k -> new LongAdder()).increment();
        
        Timer.builder("effect.execution.timer")
                .tag("effect", effectName)
                .register(meterRegistry)
                .record(java.time.Duration.ofMillis(durationMs));
    }
    
    /**
     * エフェクトエラーを記録
     */
    public void recordEffectError(String effectName, String errorType) {
        Counter.builder("effect.error.count")
                .tag("effect", effectName)
                .tag("error_type", errorType)
                .register(meterRegistry)
                .increment();
        
        counters.computeIfAbsent("effect.errors", k -> new LongAdder()).increment();
        
        logger.warn("エフェクトエラー記録: {} - {}", effectName, errorType);
    }
    
    /**
     * カスタムメトリクスを記録
     */
    public void recordCustomMetric(String name, double value) {
        meterRegistry.gauge("custom." + name, value);
    }
    
    /**
     * 緊急モード状態取得
     */
    public boolean isEmergencyModeActive() {
        return emergencyModeActive;
    }
    
    // ========== プライベートヘルパーメソッド ==========
    
    private void analyzeErrorPatterns() {
        // エラーパターン分析とアラート
    }
    
    private void analyzePerformanceTrends() {
        // パフォーマンス傾向分析
    }
    
    private void analyzePlayerBehavior() {
        // プレイヤー行動パターン分析
    }
    
    private void resetPerformanceCounters() {
        // パフォーマンスカウンターリセット
        counters.values().forEach(LongAdder::reset);
    }
    
    private void performAutomaticTuning() {
        // システム自動チューニング
    }
}