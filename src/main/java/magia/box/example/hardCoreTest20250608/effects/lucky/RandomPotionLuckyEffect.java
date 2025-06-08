package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomPotionLuckyEffect extends LuckyEffectBase {
    
    private final Random random = new Random();
    
    private final List<PotionEffectType> luckyPotions = Arrays.asList(
        PotionEffectType.SPEED,
        PotionEffectType.HASTE,
        PotionEffectType.STRENGTH,
        PotionEffectType.INSTANT_HEALTH,
        PotionEffectType.JUMP_BOOST,
        PotionEffectType.REGENERATION,
        PotionEffectType.RESISTANCE,
        PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.WATER_BREATHING,
        PotionEffectType.INVISIBILITY,
        PotionEffectType.NIGHT_VISION,
        PotionEffectType.HEALTH_BOOST,
        PotionEffectType.ABSORPTION,
        PotionEffectType.SATURATION,
        PotionEffectType.LUCK,
        PotionEffectType.SLOW_FALLING,
        PotionEffectType.CONDUIT_POWER,
        PotionEffectType.DOLPHINS_GRACE,
        PotionEffectType.HERO_OF_THE_VILLAGE
    );

    public RandomPotionLuckyEffect(JavaPlugin plugin) {
        super(plugin, "ランダムポーション効果", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        PotionEffectType selectedEffect = luckyPotions.get(random.nextInt(luckyPotions.size()));
        
        int duration = 600 + random.nextInt(600); // 30-60秒
        int amplifier = random.nextInt(3); // レベル1-3
        
        // 即座効果は特別処理
        if (selectedEffect.equals(PotionEffectType.INSTANT_HEALTH)) {
            duration = 1;
            amplifier = random.nextInt(2); // レベル1-2
        }
        
        player.addPotionEffect(new PotionEffect(selectedEffect, duration, amplifier));
        
        String effectName = getEffectDisplayName(selectedEffect);
        String levelText = amplifier > 0 ? " Lv." + (amplifier + 1) : "";
        
        player.sendMessage(ChatColor.GREEN + "ポーション効果: " + ChatColor.YELLOW + effectName + levelText + 
                          ChatColor.GREEN + " を獲得しました！");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        
        return effectName + levelText;
    }
    
    private String getEffectDisplayName(PotionEffectType effect) {
        if (effect.equals(PotionEffectType.SPEED)) return "移動速度上昇";
        if (effect.equals(PotionEffectType.HASTE)) return "採掘速度上昇";
        if (effect.equals(PotionEffectType.STRENGTH)) return "攻撃力上昇";
        if (effect.equals(PotionEffectType.INSTANT_HEALTH)) return "即座回復";
        if (effect.equals(PotionEffectType.JUMP_BOOST)) return "跳躍力強化";
        if (effect.equals(PotionEffectType.REGENERATION)) return "再生";
        if (effect.equals(PotionEffectType.RESISTANCE)) return "耐性";
        if (effect.equals(PotionEffectType.FIRE_RESISTANCE)) return "火炎耐性";
        if (effect.equals(PotionEffectType.WATER_BREATHING)) return "水中呼吸";
        if (effect.equals(PotionEffectType.INVISIBILITY)) return "透明化";
        if (effect.equals(PotionEffectType.NIGHT_VISION)) return "暗視";
        if (effect.equals(PotionEffectType.HEALTH_BOOST)) return "体力増強";
        if (effect.equals(PotionEffectType.ABSORPTION)) return "衝撃吸収";
        if (effect.equals(PotionEffectType.SATURATION)) return "満腹度回復";
        if (effect.equals(PotionEffectType.LUCK)) return "幸運";
        if (effect.equals(PotionEffectType.SLOW_FALLING)) return "低速落下";
        if (effect.equals(PotionEffectType.CONDUIT_POWER)) return "コンジットパワー";
        if (effect.equals(PotionEffectType.DOLPHINS_GRACE)) return "イルカの恵み";
        if (effect.equals(PotionEffectType.HERO_OF_THE_VILLAGE)) return "村の英雄";
        return effect.getName();
    }
}