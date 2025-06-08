package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomPotionUnluckyEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();
    
    private final List<PotionEffectType> unluckyPotions = Arrays.asList(
        PotionEffectType.SLOWNESS,
        PotionEffectType.MINING_FATIGUE,
        PotionEffectType.INSTANT_DAMAGE,
        PotionEffectType.NAUSEA,
        PotionEffectType.BLINDNESS,
        PotionEffectType.HUNGER,
        PotionEffectType.WEAKNESS,
        PotionEffectType.POISON,
        PotionEffectType.WITHER,
        PotionEffectType.LEVITATION,
        PotionEffectType.UNLUCK,
        PotionEffectType.DARKNESS,
        PotionEffectType.BAD_OMEN
    );

    public RandomPotionUnluckyEffect(JavaPlugin plugin) {
        super(plugin, "ランダム悪性ポーション効果", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        PotionEffectType selectedEffect = unluckyPotions.get(random.nextInt(unluckyPotions.size()));
        
        int duration = 300 + random.nextInt(600); // 15-45秒
        int amplifier = random.nextInt(2); // レベル1-2（控えめ）
        
        // 危険な効果は特別処理
        if (selectedEffect.equals(PotionEffectType.INSTANT_DAMAGE)) {
            duration = 1;
            amplifier = 0; // レベル1のみ
        } else if (selectedEffect.equals(PotionEffectType.WITHER)) {
            duration = Math.min(duration, 200); // 10秒まで
            amplifier = 0;
        } else if (selectedEffect.equals(PotionEffectType.POISON)) {
            duration = Math.min(duration, 300); // 15秒まで
            amplifier = 0;
        } else if (selectedEffect.equals(PotionEffectType.LEVITATION)) {
            duration = Math.min(duration, 100); // 5秒まで
            amplifier = Math.min(amplifier, 1);
        } else if (selectedEffect.equals(PotionEffectType.BAD_OMEN)) {
            duration = 6000; // 5分固定
            amplifier = 0;
        }
        
        player.addPotionEffect(new PotionEffect(selectedEffect, duration, amplifier));
        
        String effectName = getEffectDisplayName(selectedEffect);
        String levelText = amplifier > 0 ? " Lv." + (amplifier + 1) : "";
        
        player.sendMessage(ChatColor.RED + "悪性ポーション効果: " + ChatColor.DARK_RED + effectName + levelText + 
                          ChatColor.RED + " を受けました...");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
        
        return effectName + levelText;
    }
    
    private String getEffectDisplayName(PotionEffectType effect) {
        if (effect.equals(PotionEffectType.SLOWNESS)) return "移動速度低下";
        if (effect.equals(PotionEffectType.MINING_FATIGUE)) return "採掘速度低下";
        if (effect.equals(PotionEffectType.INSTANT_DAMAGE)) return "即座ダメージ";
        if (effect.equals(PotionEffectType.NAUSEA)) return "吐き気";
        if (effect.equals(PotionEffectType.BLINDNESS)) return "盲目";
        if (effect.equals(PotionEffectType.HUNGER)) return "空腹";
        if (effect.equals(PotionEffectType.WEAKNESS)) return "弱体化";
        if (effect.equals(PotionEffectType.POISON)) return "毒";
        if (effect.equals(PotionEffectType.WITHER)) return "ウィザー";
        if (effect.equals(PotionEffectType.LEVITATION)) return "浮遊";
        if (effect.equals(PotionEffectType.UNLUCK)) return "不運";
        if (effect.equals(PotionEffectType.DARKNESS)) return "暗闇";
        if (effect.equals(PotionEffectType.BAD_OMEN)) return "不吉な予感";
        return effect.getName();
    }
}