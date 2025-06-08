package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DebuffCleanseEffect extends LuckyEffectBase {

    /** 削除対象の不利なポーション効果 */
    private static final Set<PotionEffectType> DEBUFF_EFFECTS = Set.of(
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
        PotionEffectType.SLOW_FALLING, // 状況によっては不利
        PotionEffectType.BAD_OMEN,
        PotionEffectType.DARKNESS
    );

    public DebuffCleanseEffect(JavaPlugin plugin) {
        super(plugin, "デバフ浄化", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        List<PotionEffectType> removedEffects = new ArrayList<>();
        
        // プレイヤーの現在のポーション効果を取得
        for (PotionEffect effect : player.getActivePotionEffects()) {
            PotionEffectType effectType = effect.getType();
            
            // 不利な効果の場合は削除
            if (DEBUFF_EFFECTS.contains(effectType)) {
                player.removePotionEffect(effectType);
                removedEffects.add(effectType);
            }
        }
        
        // 結果に応じてメッセージとエフェクト
        if (removedEffects.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "✨ 浄化を試みましたが、削除すべき不利な効果はありませんでした。");
            
            // 軽いエフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                    EffectConstants.STANDARD_VOLUME, 1.5f);
            
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                10, 0.5, 0.5, 0.5, 0.1
            );
            
            return getDescription() + " (効果なし)";
        } else {
            player.sendMessage(ChatColor.GREEN + "✨ 不利な効果が浄化されました！");
            player.sendMessage(ChatColor.GRAY + "削除された効果: " + removedEffects.size() + "種類");
            
            // 削除された効果の詳細表示
            StringBuilder effectList = new StringBuilder();
            for (int i = 0; i < removedEffects.size(); i++) {
                if (i > 0) effectList.append(", ");
                effectList.append(getEffectDisplayName(removedEffects.get(i)));
            }
            player.sendMessage(ChatColor.DARK_GRAY + "(" + effectList + ")");
            
            // 豪華なエフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                    EffectConstants.STANDARD_VOLUME, 1.8f);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 
                    EffectConstants.STANDARD_VOLUME, 2.0f);
            
            // パーティクルエフェクト
            player.getWorld().spawnParticle(
                Particle.TOTEM_OF_UNDYING,
                player.getLocation().add(0, 1, 0),
                20, 1, 1, 1, 0.1
            );
            
            player.getWorld().spawnParticle(
                Particle.ENCHANT,
                player.getLocation().add(0, 2, 0),
                50, 1, 1, 1, 0.5
            );
            
            // 治癒エフェクト
            player.getWorld().spawnParticle(
                Particle.HEART,
                player.getLocation().add(0, 2.5, 0),
                15, 1, 0.5, 1, 0.1
            );
            
            return getDescription() + " (" + removedEffects.size() + "種類削除)";
        }
    }
    
    /**
     * ポーション効果の表示名を取得
     * @param effectType ポーション効果タイプ
     * @return 日本語表示名
     */
    private String getEffectDisplayName(PotionEffectType effectType) {
        // よく使われる効果の日本語名
        if (effectType.equals(PotionEffectType.SLOWNESS)) return "移動速度低下";
        if (effectType.equals(PotionEffectType.MINING_FATIGUE)) return "採掘速度低下";
        if (effectType.equals(PotionEffectType.INSTANT_DAMAGE)) return "即座ダメージ";
        if (effectType.equals(PotionEffectType.NAUSEA)) return "吐き気";
        if (effectType.equals(PotionEffectType.BLINDNESS)) return "盲目";
        if (effectType.equals(PotionEffectType.HUNGER)) return "空腹";
        if (effectType.equals(PotionEffectType.WEAKNESS)) return "弱体化";
        if (effectType.equals(PotionEffectType.POISON)) return "毒";
        if (effectType.equals(PotionEffectType.WITHER)) return "ウィザー";
        if (effectType.equals(PotionEffectType.LEVITATION)) return "浮遊";
        if (effectType.equals(PotionEffectType.UNLUCK)) return "不運";
        if (effectType.equals(PotionEffectType.SLOW_FALLING)) return "低速落下";
        if (effectType.equals(PotionEffectType.BAD_OMEN)) return "不吉な予感";
        if (effectType.equals(PotionEffectType.DARKNESS)) return "暗闇";
        
        // デフォルトは英語名
        return effectType.getName();
    }
}