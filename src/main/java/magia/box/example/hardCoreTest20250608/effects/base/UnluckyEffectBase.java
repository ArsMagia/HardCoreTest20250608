package magia.box.example.hardCoreTest20250608.effects.base;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.LuckyEffect;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class UnluckyEffectBase implements LuckyEffect {
    protected final JavaPlugin plugin;
    protected final String description;
    protected final EffectRarity rarity;

    public UnluckyEffectBase(JavaPlugin plugin, String description, EffectRarity rarity) {
        this.plugin = plugin;
        this.description = description;
        this.rarity = rarity;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public EffectType getType() {
        return EffectType.UNLUCKY;
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