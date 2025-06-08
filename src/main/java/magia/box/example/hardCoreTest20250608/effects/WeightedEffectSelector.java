package magia.box.example.hardCoreTest20250608.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedEffectSelector {
    private final List<LuckyEffect> effects = new ArrayList<>();
    private final List<Integer> weights = new ArrayList<>();
    private final Random random = new Random();
    private int totalWeight = 0;

    public void addEffect(LuckyEffect effect) {
        effects.add(effect);
        weights.add(effect.getWeight());
        totalWeight += effect.getWeight();
    }

    public LuckyEffect selectRandom() {
        if (effects.isEmpty()) {
            return null;
        }

        int randomValue = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (int i = 0; i < effects.size(); i++) {
            currentWeight += weights.get(i);
            if (randomValue < currentWeight) {
                return effects.get(i);
            }
        }

        return effects.get(effects.size() - 1);
    }

    public void clear() {
        effects.clear();
        weights.clear();
        totalWeight = 0;
    }

    public int size() {
        return effects.size();
    }
}