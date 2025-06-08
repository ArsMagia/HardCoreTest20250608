package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FutureVisionEffect extends LuckyEffectBase implements Listener {
    
    private static final Set<UUID> protectedPlayers = new HashSet<>();

    public FutureVisionEffect(JavaPlugin plugin) {
        super(plugin, "未来視（次回ダメージ無効）", EffectRarity.LEGENDARY);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String apply(Player player) {
        protectedPlayers.add(player.getUniqueId());
        
        player.sendMessage(ChatColor.GOLD + "未来視が発動！次に受けるダメージを無効化します。");
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.5f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (!player.isOnline() || !protectedPlayers.contains(player.getUniqueId())) {
                    this.cancel();
                    return;
                }
                
                if (counter >= 60) {
                    protectedPlayers.remove(player.getUniqueId());
                    player.sendMessage(ChatColor.GRAY + "未来視の効果が時間切れになりました。");
                    this.cancel();
                    return;
                }
                
                player.getWorld().spawnParticle(
                    Particle.END_ROD,
                    player.getLocation().add(0, 2.5, 0),
                    5, 0.3, 0.3, 0.3, 0.02
                );
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            
            if (protectedPlayers.contains(player.getUniqueId())) {
                event.setCancelled(true);
                protectedPlayers.remove(player.getUniqueId());
                
                player.sendMessage(ChatColor.YELLOW + "未来視により " + ChatColor.RED + 
                    String.format("%.1f", event.getDamage()) + " ダメージ" + ChatColor.YELLOW + " を回避しました！");
                
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 2.0f);
                player.getWorld().spawnParticle(
                    Particle.CRIT,
                    player.getLocation().add(0, 1, 0),
                    30, 1, 1, 1, 0.1
                );
            }
        }
    }
}