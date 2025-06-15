package magia.box.example.hardCoreTest20250608.performance;

import magia.box.example.hardCoreTest20250608.effects.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

/**
 * エフェクトシステムのパフォーマンステスト
 * MockBukkitが無効化されているため、基本的なパフォーマンステストのみ実行
 */
@DisplayName("⚡ Effect Performance Tests")
@EnabledIfSystemProperty(named = "test.profile", matches = "performance")
class EffectPerformanceTest {
    
    private WeightedEffectSelector selector;
    
    @BeforeEach
    void setUp() {
        selector = new WeightedEffectSelector();
        
        // モックエフェクトを追加（Bukkit環境なしでテスト可能）
        addMockEffects();
    }
    
    private void addMockEffects() {
        // MockBukkitが無効化されているため、実際のエフェクトではなく
        // 基本的な重み付けテスト用のモックエフェクトを作成
        
        // テスト用の基本エフェクトを作成（実際のBukkit機能は使用しない）
        LuckyEffect commonEffect = new TestEffect("Common", EffectRarity.COMMON);
        LuckyEffect rareEffect = new TestEffect("Rare", EffectRarity.RARE);
        LuckyEffect legendaryEffect = new TestEffect("Legendary", EffectRarity.LEGENDARY);
        
        selector.addEffect(commonEffect);
        selector.addEffect(rareEffect);
        selector.addEffect(legendaryEffect);
    }
    
    @Test
    @DisplayName("エフェクト選択: 100万回実行が3秒以内で完了する")
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void effectSelection_millionIterations_shouldCompleteWithinThreeSeconds() {
        // Given
        int iterations = 1_000_000;
        
        // When
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LuckyEffect effect = selector.selectRandom();
            assertThat(effect).isNotNull();
        }
        long endTime = System.nanoTime();
        
        // Then
        Duration duration = Duration.ofNanos(endTime - startTime);
        System.out.printf("エフェクト選択 %d回: %dms%n", iterations, duration.toMillis());
        
        // 3秒以内での完了を検証
        assertThat(duration.toSeconds()).isLessThan(3);
    }
    
    @Disabled("MockBukkit dependency is disabled")
    @Test
    @DisplayName("エフェクト実行: 1000回実行が5秒以内で完了する")
    void effectExecution_thousandIterations_shouldCompleteWithinFiveSeconds() {
        // MockBukkitが無効化されているため無効化
    }
    
    @Test
    @DisplayName("並行エフェクト選択: 10スレッドで100万回選択が5秒以内で完了する")
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void concurrentEffectSelection_shouldHandleMultipleThreads() throws InterruptedException {
        // Given
        int threadCount = 10;
        int iterationsPerThread = 100000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // When
        long startTime = System.nanoTime();
        
        for (int t = 0; t < threadCount; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        LuckyEffect effect = selector.selectRandom();
                        assertThat(effect).isNotNull();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        long endTime = System.nanoTime();
        
        // Then
        assertThat(completed).isTrue();
        
        Duration duration = Duration.ofNanos(endTime - startTime);
        System.out.printf("並行エフェクト選択 %dスレッド×%d回: %dms%n", 
                threadCount, iterationsPerThread, duration.toMillis());
        
        executor.shutdown();
    }
    
    @Test
    @DisplayName("メモリ使用量: 100万回エフェクト選択後のメモリ増加が50MB以下")
    void memoryUsage_shouldNotExceedLimit() {
        // Given
        System.gc(); // 初期状態でGC実行
        long initialMemory = getUsedMemory();
        
        int iterations = 1000000;
        
        // When
        for (int i = 0; i < iterations; i++) {
            LuckyEffect effect = selector.selectRandom();
            // 選択のみでエフェクトは実行しない（Bukkit環境不要）
            
            // 10000回毎にGCの機会を与える
            if (i % 10000 == 0) {
                System.gc();
                Thread.yield();
            }
        }
        
        System.gc(); // 最終GC
        long finalMemory = getUsedMemory();
        
        // Then
        long memoryIncrease = finalMemory - initialMemory;
        long memoryIncreaseMB = memoryIncrease / (1024 * 1024);
        
        System.out.printf("メモリ使用量増加: %dMB%n", memoryIncreaseMB);
        
        // 50MB以下の増加であること
        assertThat(memoryIncreaseMB).isLessThan(50);
    }
    
    @Test
    @DisplayName("エフェクト分布: 大量実行時も理論値に近い分布を維持する")
    void effectDistribution_shouldMaintainCorrectDistribution() {
        // Given
        int iterations = 100000;
        java.util.Map<EffectRarity, Integer> rarityCount = new java.util.HashMap<>();
        
        // When
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            LuckyEffect effect = selector.selectRandom();
            EffectRarity rarity = effect.getRarity();
            rarityCount.merge(rarity, 1, Integer::sum);
        }
        long endTime = System.nanoTime();
        
        // Then
        Duration duration = Duration.ofNanos(endTime - startTime);
        System.out.printf("分布テスト %d回: %dms%n", iterations, duration.toMillis());
        
        // 各レアリティの分布をチェック
        double commonRatio = (double) rarityCount.getOrDefault(EffectRarity.COMMON, 0) / iterations;
        double rareRatio = (double) rarityCount.getOrDefault(EffectRarity.RARE, 0) / iterations;
        double legendaryRatio = (double) rarityCount.getOrDefault(EffectRarity.LEGENDARY, 0) / iterations;
        
        System.out.printf("分布 - COMMON: %.2f%%, RARE: %.2f%%, LEGENDARY: %.2f%%%n",
                commonRatio * 100, rareRatio * 100, legendaryRatio * 100);
        
        // 理論値との差が許容範囲内であること
        assertThat(commonRatio).isBetween(0.65, 0.75); // COMMON=70重み
        assertThat(rareRatio).isBetween(0.15, 0.25);   // RARE=20重み  
        assertThat(legendaryRatio).isBetween(0.02, 0.08); // LEGENDARY=5重み
    }
    
    /**
     * 現在のメモリ使用量を取得
     */
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    /**
     * テスト用のダミーエフェクト実装
     */
    private static class TestEffect implements LuckyEffect {
        private final String name;
        private final EffectRarity rarity;
        
        public TestEffect(String name, EffectRarity rarity) {
            this.name = name;
            this.rarity = rarity;
        }
        
        @Override
        public String apply(org.bukkit.entity.Player player) {
            // Bukkit環境が不要なダミー実装
            return "Test effect: " + name;
        }
        
        @Override
        public String getDescription() {
            return "Test " + name + " Effect";
        }
        
        @Override
        public EffectType getType() {
            return EffectType.LUCKY;
        }
        
        @Override
        public EffectRarity getRarity() {
            return rarity;
        }
        
        @Override
        public int getWeight() {
            return rarity.getWeight();
        }
    }
}