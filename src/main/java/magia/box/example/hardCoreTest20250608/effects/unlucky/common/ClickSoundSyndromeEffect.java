package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "click_sound_syndrome",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class ClickSoundSyndromeEffect extends UnluckyEffectBase implements Listener {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();

    public ClickSoundSyndromeEffect(JavaPlugin plugin) {
        super(plugin, "クリック音症候群", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "🔊 既にクリック音症候群の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        // リスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        player.sendMessage(ChatColor.RED + "🖱️ クリック音症候群が発症しました！あらゆる動作にクリック音が付いて回ります...");
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        
        // 定期的にランダムクリック音を再生（さらなる混乱のため）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                // ランダムでクリック音を再生（30%の確率）
                if (random.nextInt(10) < 3) {
                    playRandomClickSound(player);
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
        
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "🔇 クリック音症候群が治癒しました。静寂が戻ってきました...");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.5f);
        }
        
        // 影響を受けているプレイヤーがいなくなったらリスナーを解除
        if (affectedPlayers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }
    
    private void playRandomClickSound(Player player) {
        if (!player.isOnline()) return;
        
        Sound[] clickSounds = {
            Sound.UI_BUTTON_CLICK,
            Sound.BLOCK_WOODEN_BUTTON_CLICK_ON,
            Sound.BLOCK_STONE_BUTTON_CLICK_ON,
            Sound.BLOCK_LEVER_CLICK,
            Sound.ITEM_FLINTANDSTEEL_USE
        };
        
        Sound randomSound = clickSounds[random.nextInt(clickSounds.length)];
        float pitch = 0.8f + random.nextFloat() * 0.4f; // 0.8 ~ 1.2の範囲
        
        player.playSound(player.getLocation(), randomSound, 0.7f, pitch);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // 移動した場合のみ（向きだけの変更は除く）
        if (event.getTo() != null && event.getFrom().distance(event.getTo()) > 0.1) {
            // ジャンプを検出（Y座標が上昇している場合）
            if (event.getTo().getY() > event.getFrom().getY() + 0.1) {
                // ジャンプには追加で2つのクリック音
                playRandomClickSound(player);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), 1L);
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), 3L);
            } else {
                // 通常の移動：50%の確率でクリック音を再生（移動音が多すぎないように）
                if (random.nextInt(2) == 0) {
                    playRandomClickSound(player);
                }
            }
        }
    }
    
    // PlayerJumpEventは存在しないため、代わりにPlayerMoveEventでジャンプを検出
    // ジャンプ検出は移動イベント内で Y座標の変化から判定
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        playRandomClickSound(player);
        // インタラクションには追加のクリック音
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), 2L);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        playRandomClickSound(player);
        // アイテム変更には特別な連続クリック音
        for (int i = 1; i <= 3; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), i);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        playRandomClickSound(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        playRandomClickSound(player);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // オフハンド切り替えには大量のクリック音
        for (int i = 1; i <= 5; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), i);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        playRandomClickSound(player);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> playRandomClickSound(player), 1L);
    }
}