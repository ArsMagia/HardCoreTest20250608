package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectRegistry {
    private final JavaPlugin plugin;
    private final Map<String, LuckyEffect> allEffects = new HashMap<>();
    private final WeightedEffectSelector luckySelector = new WeightedEffectSelector();
    private final WeightedEffectSelector unluckySelector = new WeightedEffectSelector();

    public EffectRegistry(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerEffect(String id, LuckyEffect effect) {
        allEffects.put(id, effect);
        
        if (effect.getType() == EffectType.LUCKY) {
            luckySelector.addEffect(effect);
        } else {
            unluckySelector.addEffect(effect);
        }
    }

    public LuckyEffect getRandomLucky() {
        return luckySelector.selectRandom();
    }

    public LuckyEffect getRandomUnlucky() {
        return unluckySelector.selectRandom();
    }

    public LuckyEffect getEffect(String id) {
        return allEffects.get(id);
    }

    public int getLuckyEffectCount() {
        return luckySelector.size();
    }

    public int getUnluckyEffectCount() {
        return unluckySelector.size();
    }
    
    // 新しいメソッド追加
    public int getLuckyEffectsCount() {
        return luckySelector.size();
    }
    
    public int getUnluckyEffectsCount() {
        return unluckySelector.size();
    }
    
    public int getTotalEffectsCount() {
        return allEffects.size();
    }
    
    public Map<String, LuckyEffect> getAllEffects() {
        return new HashMap<>(allEffects);
    }

    public List<LuckyEffect> getAllLuckyEffects() {
        List<LuckyEffect> luckyEffects = new ArrayList<>();
        for (LuckyEffect effect : allEffects.values()) {
            if (effect.getType() == EffectType.LUCKY) {
                luckyEffects.add(effect);
            }
        }
        // レアリティ順でソート（LEGENDARY -> EPIC -> RARE -> UNCOMMON -> COMMON）
        luckyEffects.sort((a, b) -> b.getRarity().ordinal() - a.getRarity().ordinal());
        return luckyEffects;
    }

    public List<LuckyEffect> getAllUnluckyEffects() {
        List<LuckyEffect> unluckyEffects = new ArrayList<>();
        for (LuckyEffect effect : allEffects.values()) {
            if (effect.getType() == EffectType.UNLUCKY) {
                unluckyEffects.add(effect);
            }
        }
        // レアリティ順でソート（LEGENDARY -> EPIC -> RARE -> UNCOMMON -> COMMON）
        unluckyEffects.sort((a, b) -> b.getRarity().ordinal() - a.getRarity().ordinal());
        return unluckyEffects;
    }

    public void clear() {
        allEffects.clear();
        luckySelector.clear();
        unluckySelector.clear();
    }
}