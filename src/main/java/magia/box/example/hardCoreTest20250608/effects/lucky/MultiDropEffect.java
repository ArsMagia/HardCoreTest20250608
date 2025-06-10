package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MultiDropEffect extends LuckyEffectBase implements Listener {
    
    private static final Set<UUID> enhancedPlayers = new HashSet<>();

    public MultiDropEffect(JavaPlugin plugin) {
        super(plugin, "マルチドロップ", EffectRarity.EPIC);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String apply(Player player) {
        enhancedPlayers.add(player.getUniqueId());
        
        player.sendMessage(ChatColor.YELLOW + "マルチドロップが発動！5分間、ブロック破壊時に追加アイテムがドロップします。");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
        
        player.getWorld().spawnParticle(
            org.bukkit.Particle.ITEM,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1,
            new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND)
        );
        
        new BukkitRunnable() {
            @Override
            public void run() {
                enhancedPlayers.remove(player.getUniqueId());
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "マルチドロップの効果が終了しました。");
                }
            }
        }.runTaskLater(plugin, 6000L); // 5分後
        
        return getDescription();
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (enhancedPlayers.contains(player.getUniqueId())) {
            // 25%の確率で追加ドロップ
            if (Math.random() < 0.25) {
                event.getBlock().getDrops().forEach(drop -> {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
                });
                
                player.playSound(event.getBlock().getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.2f);
                event.getBlock().getWorld().spawnParticle(
                    org.bukkit.Particle.HAPPY_VILLAGER,
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5),
                    5, 0.3, 0.3, 0.3, 0.02
                );
            }
        }
    }
}