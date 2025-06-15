package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "reversed_control",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class ReversedControlEffect extends UnluckyEffectBase implements Listener {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final Map<UUID, Location> lastLocations = new HashMap<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間

    public ReversedControlEffect(JavaPlugin plugin) {
        super(plugin, "逆操作症候群", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "🔄 既に逆操作症候群の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        lastLocations.put(playerId, player.getLocation().clone());
        
        // リスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        player.sendMessage(ChatColor.RED + "🔄 逆操作症候群が発症しました！操作が逆転します...");
        player.sendMessage(ChatColor.GRAY + "（前進→後退、左→右、右→左に逆転）");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        
        // 逆操作の視覚効果を定期的に表示
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                spawnReversedParticles(player);
                
                // 10秒ごとに注意メッセージ
                if (count % 10 == 0) {
                    player.sendMessage(ChatColor.YELLOW + "🔄 操作が逆転中...");
                }
                
                count++;
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1秒間隔
        
        // 30秒後に効果を解除
        new BukkitRunnable() {
            @Override
            public void run() {
                removeEffect(player);
            }
        }.runTaskLater(plugin, EFFECT_DURATION);
        
        return getDescription();
    }
    
    private void removeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        affectedPlayers.remove(playerId);
        lastLocations.remove(playerId);
        
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "🎯 逆操作症候群が治癒しました。操作が正常に戻りました！");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                15, 0.5, 0.5, 0.5, 0.1
            );
        }
        
        // 影響を受けているプレイヤーがいなくなったらリスナーを解除
        if (affectedPlayers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }
    
    private void spawnReversedParticles(Player player) {
        if (!player.isOnline()) return;
        
        // 逆転を表現するパーティクル（回転するような効果）
        Location loc = player.getLocation().add(0, 1, 0);
        
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            loc,
            8, 0.5, 0.5, 0.5, 0.1
        );
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            loc,
            5, 0.3, 0.3, 0.3, 0.1
        );
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        if (!affectedPlayers.contains(playerId)) {
            return;
        }
        
        Location from = event.getFrom();
        Location to = event.getTo();
        
        // 位置変更がない場合（向きだけの変更）は無視
        if (from.getX() == to.getX() && from.getZ() == to.getZ() && from.getY() == to.getY()) {
            return;
        }
        
        // 移動ベクトルを計算
        Vector movement = to.toVector().subtract(from.toVector());
        
        // 移動が非常に小さい場合は無視（振動を防ぐ）
        if (movement.length() < 0.01) {
            return;
        }
        
        // Y軸の移動（ジャンプ/落下）は通常通り
        double originalY = movement.getY();
        
        // X, Z軸の移動を逆転
        movement.setX(-movement.getX());
        movement.setZ(-movement.getZ());
        movement.setY(originalY);
        
        // 新しい目的地を計算
        Location reversedDestination = from.clone().add(movement);
        
        // 安全チェック：地面以下や高すぎる場所への移動を防ぐ
        if (reversedDestination.getY() < 0) {
            reversedDestination.setY(from.getY());
        }
        if (reversedDestination.getY() > 320) {
            reversedDestination.setY(from.getY());
        }
        
        // 視点の向きは保持
        reversedDestination.setYaw(to.getYaw());
        reversedDestination.setPitch(to.getPitch());
        
        // イベントをキャンセルして逆転移動を適用
        event.setCancelled(true);
        
        // 少し遅延させて移動（移動ループを防ぐ）
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline() && affectedPlayers.contains(playerId)) {
                player.teleport(reversedDestination);
                
                // 10%の確率で逆転音を再生
                if (Math.random() < 0.1) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.2f, 1.5f);
                }
            }
        }, 1L);
        
        // 最後の位置を更新
        lastLocations.put(playerId, reversedDestination.clone());
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // スニーク操作を逆転（スニークしようとするとジャンプ、スニーク解除しようとするとスニーク強制）
        if (event.isSneaking()) {
            // スニークしようとした → ジャンプさせる
            event.setCancelled(true);
            
            Vector jumpVelocity = player.getVelocity();
            jumpVelocity.setY(0.5); // ジャンプ力
            player.setVelocity(jumpVelocity);
            
            player.sendMessage(ChatColor.GRAY + "🔄 スニーク→ジャンプに逆転！");
            player.playSound(player.getLocation(), Sound.ENTITY_SLIME_JUMP, 0.5f, 1.2f);
            
        } else {
            // スニーク解除しようとした → 強制的にスニークさせる
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3)); // 3秒間
            
            player.sendMessage(ChatColor.GRAY + "🔄 スニーク解除→強制スニークに逆転！");
            player.playSound(player.getLocation(), Sound.BLOCK_GRASS_STEP, 0.5f, 0.8f);
        }
    }
}