package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class CursedSoundsEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public CursedSoundsEffect(JavaPlugin plugin) {
        super(plugin, "呪われた音響", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "呪われた音に包まれました...");
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 10 || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                Sound[] cursedSounds = {
                    Sound.ENTITY_GHAST_SCREAM, Sound.ENTITY_ENDERMAN_SCREAM,
                    Sound.ENTITY_WITCH_CELEBRATE, Sound.AMBIENT_CAVE,
                    Sound.ENTITY_ZOMBIE_AMBIENT, Sound.ENTITY_SKELETON_AMBIENT
                };
                
                Sound randomSound = cursedSounds[random.nextInt(cursedSounds.length)];
                float pitch = 0.5f + random.nextFloat();
                
                player.playSound(player.getLocation(), randomSound, 1.0f, pitch);
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 40L);
        
        return getDescription();
    }
}