package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomBuffPotionEffect extends LuckyEffectBase {
    
    private final Random random = new Random();
    
    private final List<PotionEffectType> buffPotions = Arrays.asList(
        PotionEffectType.SPEED,           // 移動速度上昇
        PotionEffectType.HASTE,           // 採掘速度上昇
        PotionEffectType.STRENGTH,        // 攻撃力上昇
        PotionEffectType.JUMP_BOOST,      // 跳躍力強化
        PotionEffectType.REGENERATION,    // 再生
        PotionEffectType.RESISTANCE,      // 耐性
        PotionEffectType.FIRE_RESISTANCE, // 火炎耐性
        PotionEffectType.WATER_BREATHING, // 水中呼吸
        PotionEffectType.INVISIBILITY,    // 透明化
        PotionEffectType.NIGHT_VISION,    // 暗視
        PotionEffectType.HEALTH_BOOST,    // 体力増強
        PotionEffectType.ABSORPTION,      // 衝撃吸収
        PotionEffectType.SATURATION,      // 満腹度回復
        PotionEffectType.LUCK,            // 幸運
        PotionEffectType.SLOW_FALLING,    // 低速落下
        PotionEffectType.CONDUIT_POWER,   // コンジットパワー
        PotionEffectType.DOLPHINS_GRACE,  // イルカの恵み
        PotionEffectType.HERO_OF_THE_VILLAGE // 村の英雄
    );

    public RandomBuffPotionEffect(JavaPlugin plugin) {
        super(plugin, "ランダムバフポーション", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        // 1-3個のランダムなバフ効果を付与
        int effectCount = 1 + random.nextInt(3);
        StringBuilder description = new StringBuilder();
        
        for (int i = 0; i < effectCount; i++) {
            PotionEffectType selectedEffect = buffPotions.get(random.nextInt(buffPotions.size()));
            
            int duration = 400 + random.nextInt(800); // 20-60秒
            int amplifier = random.nextInt(2); // レベル1-2
            
            // 即座効果は特別処理
            if (selectedEffect.equals(PotionEffectType.INSTANT_HEALTH)) {
                duration = 1;
                amplifier = random.nextInt(2);
            } else if (selectedEffect.equals(PotionEffectType.SATURATION)) {
                duration = 200; // 満腹度回復は短時間
            }
            
            player.addPotionEffect(new PotionEffect(selectedEffect, duration, amplifier));
            
            String effectName = getEffectDisplayName(selectedEffect);
            String levelText = amplifier > 0 ? " Lv." + (amplifier + 1) : "";
            
            if (i > 0) description.append(" + ");
            description.append(effectName).append(levelText);
        }
        
        player.sendMessage(ChatColor.GREEN + "ランダムバフ効果: " + ChatColor.AQUA + description.toString() + 
                          ChatColor.GREEN + " を獲得しました！");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.4f);
        
        player.getWorld().spawnParticle(
                Particle.SPLASH,
            player.getLocation().add(0, 1, 0),
            30, 1, 1, 1, 0.2
        );
        
        return description.toString();
    }
    
    private String getEffectDisplayName(PotionEffectType effect) {
        if (effect.equals(PotionEffectType.SPEED)) return "移動速度上昇";
        if (effect.equals(PotionEffectType.HASTE)) return "採掘速度上昇";
        if (effect.equals(PotionEffectType.STRENGTH)) return "攻撃力上昇";
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