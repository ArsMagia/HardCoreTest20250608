package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class MultiBuffCombinationEffect extends LuckyEffectBase {
    
    private final Random random = new Random();

    public MultiBuffCombinationEffect(JavaPlugin plugin) {
        super(plugin, "バフコンビネーション", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        // 戦闘特化、採掘特化、移動特化、防御特化のコンビネーション
        String[] combinations = {"戦闘特化", "採掘特化", "移動特化", "防御特化", "万能型"};
        String selectedCombo = combinations[random.nextInt(combinations.length)];
        
        switch (selectedCombo) {
            case "戦闘特化":
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 0));
                break;
                
            case "採掘特化":
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 1200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 1));
                break;
                
            case "移動特化":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 1200, 0));
                break;
                
            case "防御特化":
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1200, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1200, 1));
                break;
                
            case "万能型":
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1200, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 1200, 0));
                break;
        }
        
        player.sendMessage(ChatColor.GOLD + selectedCombo + "バフコンビネーションが発動！1分間、特化型強化を受けます。");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
        
        player.getWorld().spawnParticle(
            org.bukkit.Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            50, 1, 2, 1, 0.3
        );
        
        return getDescription() + "（" + selectedCombo + "）";
    }
}