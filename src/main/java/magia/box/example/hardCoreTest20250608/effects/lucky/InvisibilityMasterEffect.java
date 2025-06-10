package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InvisibilityMasterEffect extends LuckyEffectBase implements Listener {
    
    private static final Set<UUID> silentPlayers = new HashSet<>();

    public InvisibilityMasterEffect(JavaPlugin plugin) {
        super(plugin, "透明化マスター", EffectRarity.RARE);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false));
        silentPlayers.add(player.getUniqueId());
        
        player.sendMessage(ChatColor.GRAY + "完全な透明化が発動！3秒間、姿・音・足跡が消えます。");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        
        player.getWorld().spawnParticle(
            Particle.SMOKE,
            player.getLocation().add(0, 1, 0),
            30, 0.5, 1, 0.5, 0.1
        );
        
        new BukkitRunnable() {
            @Override
            public void run() {
                silentPlayers.remove(player.getUniqueId());
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "透明化マスターの効果が終了しました。");
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                }
            }
        }.runTaskLater(plugin, 60L); // 3秒に変更
        
        return getDescription();
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (silentPlayers.contains(player.getUniqueId())) {
            if (event.getFrom().getBlock() != event.getTo().getBlock()) {
                event.setCancelled(true);
                player.teleport(event.getTo());
            }
        }
    }
}