package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "reflex_delay",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class ReflexDelayEffect extends UnluckyEffectBase implements Listener {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private static final int DELAY_TICKS = 20; // 1秒遅延

    public ReflexDelayEffect(JavaPlugin plugin) {
        super(plugin, "反射神経遅延", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "🕰️ 既に反射神経遅延の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        // リスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        player.sendMessage(ChatColor.RED + "🐌 反射神経が鈍くなりました！全ての操作が1秒遅れて実行されます...");
        player.playSound(player.getLocation(), Sound.ENTITY_SLIME_SQUISH, 1.0f, 0.5f);
        
        // 遅延エフェクトの視覚表現
        spawnDelayParticles(player);
        
        // 30秒後に効果を解除
        new BukkitRunnable() {
            @Override
            public void run() {
                removeEffect(player);
            }
        }.runTaskLater(plugin, EFFECT_DURATION);
        
        // 定期的に遅延の視覚表現を表示
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 15) {
                    this.cancel();
                    return;
                }
                
                spawnDelayParticles(player);
                count++;
            }
        }.runTaskTimer(plugin, 40L, 40L); // 2秒間隔
        
        return getDescription();
    }
    
    private void removeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        affectedPlayers.remove(playerId);
        
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "⚡ 反射神経が正常に戻りました！");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                10, 0.5, 0.5, 0.5, 0.1
            );
        }
        
        // 影響を受けているプレイヤーがいなくなったらリスナーを解除
        if (affectedPlayers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }
    
    private void spawnDelayParticles(Player player) {
        if (!player.isOnline()) return;
        
        // 遅延を表現するパーティクル
        player.getWorld().spawnParticle(
            Particle.NAUTILUS,
            player.getLocation().add(0, 1, 0),
            3, 0.3, 0.3, 0.3, 0.05
        );
        
        player.getWorld().spawnParticle(
            Particle.SMOKE,
            player.getLocation().add(0, 0.5, 0),
            5, 0.2, 0.2, 0.2, 0.02
        );
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // イベントをキャンセルして遅延実行
        event.setCancelled(true);
        
        final Material blockType = event.getBlock().getType();
        final org.bukkit.Location blockLocation = event.getBlock().getLocation();
        
        player.sendMessage(ChatColor.GRAY + "🕰️ 操作が遅延しています...");
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 0.8f);
        
        // 1秒後に実際のブロック破壊を実行
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && blockLocation.getBlock().getType() == blockType) {
                    // プレイヤーがまだその場所にいて、ブロックが変更されていない場合のみ破壊
                    if (player.getLocation().distance(blockLocation) <= 6) {
                        blockLocation.getBlock().breakNaturally(player.getInventory().getItemInMainHand());
                        player.playSound(blockLocation, Sound.BLOCK_STONE_BREAK, 1.0f, 1.0f);
                        
                        // 遅延破壊の視覚効果
                        player.getWorld().spawnParticle(
                            Particle.EXPLOSION,
                            blockLocation.add(0.5, 0.5, 0.5),
                            3, 0.2, 0.2, 0.2, 0.1
                        );
                    }
                }
            }
        }.runTaskLater(plugin, DELAY_TICKS);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // アイテム使用に関する操作のみ遅延（移動は除く）
        if (event.getAction().toString().contains("RIGHT_CLICK") && 
            event.getItem() != null && 
            event.getItem().getType() != Material.AIR) {
            
            event.setCancelled(true);
            
            player.sendMessage(ChatColor.GRAY + "🕰️ アイテム使用が遅延しています...");
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 0.8f);
            
            // 元のイベントデータを保存
            final org.bukkit.event.block.Action action = event.getAction();
            final org.bukkit.inventory.ItemStack item = event.getItem();
            final org.bukkit.block.Block clickedBlock = event.getClickedBlock();
            
            // 1秒後に実際のアクションを実行
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline() && player.getInventory().getItemInMainHand().isSimilar(item)) {
                        // 遅延実行の通知
                        player.sendMessage(ChatColor.YELLOW + "✨ 遅延アクションが実行されました");
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
                        
                        // プログラム的にアイテム使用をシミュレート
                        if (clickedBlock != null) {
                            // ブロックに対するアクションの場合
                            PlayerInteractEvent delayedEvent = new PlayerInteractEvent(
                                player, action, item, clickedBlock, event.getBlockFace()
                            );
                            plugin.getServer().getPluginManager().callEvent(delayedEvent);
                        }
                    }
                }
            }.runTaskLater(plugin, DELAY_TICKS);
        }
    }
}