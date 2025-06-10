package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimeRewindEffect extends LuckyEffectBase {
    
    private static final Map<UUID, PlayerSnapshot> snapshots = new HashMap<>();

    public TimeRewindEffect(JavaPlugin plugin) {
        super(plugin, "時間巻き戻し", EffectRarity.EPIC);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    snapshots.put(player.getUniqueId(), new PlayerSnapshot(player));
                }
            }
        }.runTaskTimer(plugin, 0L, 140L); // 7秒間隔に変更
    }

    @Override
    public String apply(Player player) {
        PlayerSnapshot snapshot = snapshots.get(player.getUniqueId());
        
        if (snapshot != null) {
            Location currentLoc = player.getLocation();
            
            currentLoc.getWorld().spawnParticle(
                Particle.REVERSE_PORTAL,
                currentLoc.add(0, 1, 0),
                200, 1, 2, 1, 0.5
            );
            
            player.teleport(snapshot.location);
            player.setHealth(Math.min(snapshot.health, player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
            player.setFoodLevel(snapshot.foodLevel);
            player.getInventory().setContents(snapshot.inventory.clone());
            
            player.sendMessage(ChatColor.LIGHT_PURPLE + "7秒前の状態に戻りました！");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 0.8f);
            
            snapshot.location.getWorld().spawnParticle(
                Particle.ENCHANT,
                snapshot.location.add(0, 1, 0),
                100, 1, 2, 1, 0.3
            );
            
            return getDescription();
        } else {
            player.sendMessage(ChatColor.YELLOW + "巻き戻しデータがありません。代わりに完全回復します。");
            player.setHealth(player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
            player.setFoodLevel(20);
            return "完全回復";
        }
    }
    
    private static class PlayerSnapshot {
        final Location location;
        final double health;
        final int foodLevel;
        final ItemStack[] inventory;
        
        PlayerSnapshot(Player player) {
            this.location = player.getLocation().clone();
            this.health = player.getHealth();
            this.foodLevel = player.getFoodLevel();
            this.inventory = player.getInventory().getContents().clone();
        }
    }
}