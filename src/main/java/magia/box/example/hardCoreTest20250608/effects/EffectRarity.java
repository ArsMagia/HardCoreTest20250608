package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.ChatColor;

public enum EffectRarity {
    COMMON(70, "コモン", ChatColor.WHITE),
    UNCOMMON(50, "アンコモン", ChatColor.GREEN),
    RARE(20, "レア", ChatColor.BLUE),
    EPIC(10, "エピック", ChatColor.DARK_PURPLE),
    LEGENDARY(5, "レジェンダリー", ChatColor.GOLD);

    private final int weight;
    private final String displayName;
    private final ChatColor color;

    EffectRarity(int weight, String displayName, ChatColor color) {
        this.weight = weight;
        this.displayName = displayName;
        this.color = color;
    }

    public int getWeight() {
        return weight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getColoredName() {
        return color + displayName;
    }
}