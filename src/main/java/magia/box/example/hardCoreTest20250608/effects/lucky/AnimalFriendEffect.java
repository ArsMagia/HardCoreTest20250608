package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimalFriendEffect extends LuckyEffectBase {

    public AnimalFriendEffect(JavaPlugin plugin) {
        super(plugin, "動物の友", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        int pacifiedCount = 0;
        
        for (Entity entity : center.getWorld().getNearbyEntities(center, 15, 15, 15)) {
            if (entity instanceof Monster) {
                Monster monster = (Monster) entity;
                monster.setTarget(null);
                pacifiedCount++;
                
                entity.getLocation().getWorld().spawnParticle(
                    Particle.HEART,
                    entity.getLocation().add(0, entity.getHeight() + 0.5, 0),
                    3, 0.3, 0.3, 0.3, 0.02
                );
            }
        }
        
        if (pacifiedCount > 0) {
            player.sendMessage(ChatColor.GREEN + "動物の友効果！周囲の " + pacifiedCount + " 体のモンスターが中立化しました。");
        } else {
            player.sendMessage(ChatColor.YELLOW + "周囲にモンスターがいませんでした。代わりに幸運効果を獲得します。");
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.LUCK, 1200, 1));
        }
        
        player.playSound(center, Sound.ENTITY_CAT_PURREOW, 1.0f, 1.2f);
        
        center.getWorld().spawnParticle(
            Particle.HEART,
            center.add(0, 2, 0),
            20, 3, 2, 3, 0.1
        );
        
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GRAY + "動物の友効果が終了しました。");
            }
        }.runTaskLater(plugin, 600L);
        
        return getDescription();
    }
}