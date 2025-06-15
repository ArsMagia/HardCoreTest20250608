package magia.box.example.hardCoreTest20250608.effects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * WeightedEffectSelectorのテストクラス
 * 重み付きランダム選択の正確性を統計的にテストします
 */
@DisplayName("🎲 WeightedEffectSelector Tests")
class WeightedEffectSelectorTest {
    
    private WeightedEffectSelector selector;
    
    @Mock
    private LuckyEffect commonEffect;
    
    @Mock
    private LuckyEffect rareEffect;
    
    @Mock
    private LuckyEffect legendaryEffect;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        selector = new WeightedEffectSelector();
        
        // モックの設定（lenientモード）
        lenient().when(commonEffect.getWeight()).thenReturn(70);
        lenient().when(commonEffect.getDescription()).thenReturn("Common Effect");
        lenient().when(commonEffect.getRarity()).thenReturn(EffectRarity.COMMON);
        
        lenient().when(rareEffect.getWeight()).thenReturn(20);
        lenient().when(rareEffect.getDescription()).thenReturn("Rare Effect");
        lenient().when(rareEffect.getRarity()).thenReturn(EffectRarity.RARE);
        
        lenient().when(legendaryEffect.getWeight()).thenReturn(5);
        lenient().when(legendaryEffect.getDescription()).thenReturn("Legendary Effect");
        lenient().when(legendaryEffect.getRarity()).thenReturn(EffectRarity.LEGENDARY);
    }
    
    @Test
    @DisplayName("エフェクトの追加が正常に動作する")
    void addEffect_shouldAddEffectSuccessfully() {
        // When
        selector.addEffect(commonEffect);
        
        // Then
        assertThat(selector.getTotalWeight()).isEqualTo(70);
        assertThat(selector.getEffectCount()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("複数エフェクトの総重みが正しく計算される")
    void addMultipleEffects_shouldCalculateTotalWeightCorrectly() {
        // When
        selector.addEffect(commonEffect);
        selector.addEffect(rareEffect);
        selector.addEffect(legendaryEffect);
        
        // Then
        assertThat(selector.getTotalWeight()).isEqualTo(95); // 70 + 20 + 5
        assertThat(selector.getEffectCount()).isEqualTo(3);
    }
    
    @Test
    @DisplayName("重み付きランダム選択が統計的に正しい分布を生成する")
    void selectRandom_shouldRespectWeightDistribution() {
        // Given
        selector.addEffect(commonEffect);
        selector.addEffect(rareEffect);
        selector.addEffect(legendaryEffect);
        
        Map<LuckyEffect, Integer> results = new HashMap<>();
        int iterations = 100000; // 統計的信頼性のため大量実行
        
        // When - 大量実行して分布をチェック
        for (int i = 0; i < iterations; i++) {
            LuckyEffect selected = selector.selectRandom();
            results.merge(selected, 1, Integer::sum);
        }
        
        // Then - 理論値との差が許容範囲内かチェック
        double commonRatio = (double) results.get(commonEffect) / iterations;
        double rareRatio = (double) results.get(rareEffect) / iterations;
        double legendaryRatio = (double) results.get(legendaryEffect) / iterations;
        
        // 理論値: Common=73.7%, Rare=21.1%, Legendary=5.3%
        assertThat(commonRatio).isBetween(0.72, 0.75);  // ±1.5%の誤差許容
        assertThat(rareRatio).isBetween(0.20, 0.22);    // ±1%の誤差許容  
        assertThat(legendaryRatio).isBetween(0.04, 0.06); // ±1%の誤差許容
    }
    
    @Test
    @DisplayName("エフェクトが0個の場合は例外をスローする")
    void selectRandom_withNoEffects_shouldThrowException() {
        // When & Then
        assertThatThrownBy(() -> selector.selectRandom())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("エフェクトが登録されていません");
    }
    
    @Test
    @DisplayName("重みが0のエフェクトは選択されない")
    void selectRandom_withZeroWeight_shouldNeverBeSelected() {
        // Given
        LuckyEffect zeroWeightEffect = mock(LuckyEffect.class);
        lenient().when(zeroWeightEffect.getWeight()).thenReturn(0);
        lenient().when(zeroWeightEffect.getDescription()).thenReturn("Zero Weight Effect");
        
        selector.addEffect(commonEffect);
        selector.addEffect(zeroWeightEffect);
        
        // When
        Map<LuckyEffect, Integer> results = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            LuckyEffect selected = selector.selectRandom();
            results.merge(selected, 1, Integer::sum);
        }
        
        // Then
        assertThat(results.get(zeroWeightEffect)).isNull();
        assertThat(results.get(commonEffect)).isEqualTo(1000);
    }
    
    @Test
    @DisplayName("同じ重みのエフェクトは均等に選択される")
    void selectRandom_withEqualWeights_shouldSelectEqually() {
        // Given
        LuckyEffect effect1 = mock(LuckyEffect.class);
        LuckyEffect effect2 = mock(LuckyEffect.class);
        
        lenient().when(effect1.getWeight()).thenReturn(50);
        lenient().when(effect1.getDescription()).thenReturn("Effect 1");
        lenient().when(effect2.getWeight()).thenReturn(50);  
        lenient().when(effect2.getDescription()).thenReturn("Effect 2");
        
        selector.addEffect(effect1);
        selector.addEffect(effect2);
        
        Map<LuckyEffect, Integer> results = new HashMap<>();
        int iterations = 10000;
        
        // When
        for (int i = 0; i < iterations; i++) {
            LuckyEffect selected = selector.selectRandom();
            results.merge(selected, 1, Integer::sum);
        }
        
        // Then - 各50%に近い分布
        double ratio1 = (double) results.get(effect1) / iterations;
        double ratio2 = (double) results.get(effect2) / iterations;
        
        assertThat(ratio1).isBetween(0.48, 0.52); // 50% ± 2%
        assertThat(ratio2).isBetween(0.48, 0.52); // 50% ± 2%
    }
    
    @Test
    @DisplayName("パフォーマンステスト: 大量選択が1秒以内に完了する")
    void selectRandom_performanceTest() {
        // Given
        selector.addEffect(commonEffect);
        selector.addEffect(rareEffect);
        selector.addEffect(legendaryEffect);
        
        int iterations = 1000000; // 100万回
        
        // When
        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            selector.selectRandom();
        }
        long endTime = System.nanoTime();
        
        // Then - 1秒以内で完了すること
        long durationMs = (endTime - startTime) / 1_000_000;
        assertThat(durationMs).isLessThan(1000);
    }
}