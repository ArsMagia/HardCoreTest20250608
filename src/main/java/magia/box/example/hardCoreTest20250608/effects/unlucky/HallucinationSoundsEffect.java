package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class HallucinationSoundsEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public HallucinationSoundsEffect(JavaPlugin plugin) {
        super(plugin, "幻聴", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.LIGHT_PURPLE + "幻聴が聞こえます...30秒間、ランダムな音が聞こえてきます。");
        
        Sound[] sounds = {
            Sound.ENTITY_CREEPER_PRIMED, Sound.ENTITY_ZOMBIE_AMBIENT,
            Sound.ENTITY_SKELETON_AMBIENT, Sound.ENTITY_SPIDER_AMBIENT,
            Sound.ENTITY_ENDERMAN_AMBIENT, Sound.ENTITY_GHAST_AMBIENT,
            Sound.BLOCK_STONE_BREAK, Sound.ENTITY_ITEM_PICKUP
        };
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 15 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "幻聴が止まりました。");
                    this.cancel();
                    return;
                }
                
                Sound randomSound = sounds[random.nextInt(sounds.length)];
                float volume = 0.3f + random.nextFloat() * 0.4f;
                float pitch = 0.5f + random.nextFloat();
                
                player.playSound(player.getLocation(), randomSound, volume, pitch);
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 40L);
        
        return getDescription();
    }
}