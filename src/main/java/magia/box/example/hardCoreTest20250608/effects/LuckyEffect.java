package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.entity.Player;

public interface LuckyEffect {
    String apply(Player player);
    String getDescription();
    EffectType getType();
    EffectRarity getRarity();
    int getWeight();
}