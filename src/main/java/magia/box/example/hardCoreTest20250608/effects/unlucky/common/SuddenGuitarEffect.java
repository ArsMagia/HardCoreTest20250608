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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "sudden_guitar",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class SuddenGuitarEffect extends UnluckyEffectBase implements Listener {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();
    
    // ギター音階（Note Block使用）
    private static final float[] GUITAR_NOTES = {
        0.5f,   // F
        0.53f,  // F#
        0.56f,  // G
        0.6f,   // G#
        0.63f,  // A
        0.67f,  // A#
        0.7f,   // B
        0.75f,  // C
        0.8f,   // C#
        0.85f,  // D
        0.9f,   // D#
        0.95f,  // E
        1.0f,   // F (オクターブ上)
        1.05f,  // F#
        1.1f,   // G
        1.2f,   // G#
        1.26f,  // A
        1.33f,  // A#
        1.4f,   // B
        1.5f,   // C (オクターブ上)
        1.6f,   // C#
        1.7f,   // D
        1.8f,   // D#
        1.9f,   // E
        2.0f    // F (2オクターブ上)
    };

    public SuddenGuitarEffect(JavaPlugin plugin) {
        super(plugin, "突然のギター", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "🎸 既に突然のギターの影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        // リスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        player.sendMessage(ChatColor.RED + "🎸 突然のギター症候群が発症しました！あなたの行動が全てギター音楽になります！");
        player.sendMessage(ChatColor.GRAY + "♪ ♫ ♪ Let's Rock & Roll! ♪ ♫ ♪");
        
        // 開始時のギターイントロ
        playGuitarIntro(player);
        
        // 定期的にランダムギター音（バックグラウンドミュージック的な）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                // 30%の確率でバックグラウンドギター音
                if (random.nextInt(10) < 3) {
                    playRandomGuitarChord(player);
                }
                
                // 10秒ごとにロックメッセージ
                if (count % 10 == 0) {
                    showRockMessage(player);
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
            player.sendMessage(ChatColor.GREEN + "🎵 ギター症候群が治癒しました。静寂が戻ってきました...");
            player.sendMessage(ChatColor.GRAY + "♪ Thank you for listening! ♪");
            
            // 終了時のギターアウトロ
            playGuitarOutro(player);
        }
        
        // 影響を受けているプレイヤーがいなくなったらリスナーを解除
        if (affectedPlayers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }
    
    private void playGuitarIntro(Player player) {
        // 開始時のギターリフ
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[7], 1.0f), 5L); // C
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[9], 1.0f), 10L); // D
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[12], 1.0f), 15L); // F
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[14], 1.0f), 20L); // G
    }
    
    private void playGuitarOutro(Player player) {
        // 終了時のギターアウトロ
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[14], 1.0f), 5L); // G
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[12], 1.0f), 10L); // F
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[9], 1.0f), 15L); // D
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[7], 0.8f), 25L); // C (フェードアウト)
    }
    
    private void playGuitarNote(Player player, float pitch, float volume) {
        if (player.isOnline()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, volume, pitch);
        }
    }
    
    private void playRandomGuitarChord(Player player) {
        // ランダムな和音を演奏
        float rootNote = GUITAR_NOTES[random.nextInt(12)]; // ルート音
        
        playGuitarNote(player, rootNote, 0.6f);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, rootNote * 1.25f, 0.5f), 2L); // 3度
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, rootNote * 1.5f, 0.4f), 4L); // 5度
    }
    
    private void showRockMessage(Player player) {
        String[] rockMessages = {
            "🎸 Rock on! 🤘",
            "🎵 Keep on rockin'! 🎶",
            "🎸 Guitar Hero mode! 🌟",
            "🤘 Heavy Metal! 🤘",
            "🎶 Music to my ears! 🎵",
            "🎸 Shred it! 🔥"
        };
        
        String message = rockMessages[random.nextInt(rockMessages.length)];
        player.sendMessage(ChatColor.GOLD + message);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // 移動に応じてギター音（歩行は低音、走行は高音）
        if (event.getTo() == null) return;
        double distance = event.getFrom().distance(event.getTo());
        
        if (distance > 0.1) { // 実際に移動した場合
            // ジャンプを検出（Y座標が上昇している場合）
            if (event.getTo().getY() > event.getFrom().getY() + 0.1) {
                handleJump(player);
            } else if (player.isSprinting()) {
                // 走行 - 高音で速いテンポ
                if (random.nextInt(3) == 0) { // 33%の確率
                    int noteIndex = Math.min(15 + random.nextInt(8), GUITAR_NOTES.length - 1);
                    playGuitarNote(player, GUITAR_NOTES[noteIndex], 0.7f);
                }
            } else {
                // 歩行 - 低音でゆったり
                if (random.nextInt(5) == 0) { // 20%の確率
                    playGuitarNote(player, GUITAR_NOTES[random.nextInt(10)], 0.5f);
                }
            }
        }
    }
    
    // PlayerJumpEventは存在しないため、移動イベントでジャンプを検出
    private void handleJump(Player player) {
        // ジャンプ - 上昇音階
        playGuitarNote(player, GUITAR_NOTES[7], 0.8f); // C
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[11], 0.7f), 3L); // E
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[14], 0.6f), 6L); // G
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // ブロック破壊 - パワーコード
        float rootNote = GUITAR_NOTES[random.nextInt(8)];
        playGuitarNote(player, rootNote, 1.0f);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, rootNote * 1.5f, 0.8f), 2L);
        
        player.sendMessage(ChatColor.YELLOW + "🎸 破壊のリフ！");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // アイテム使用 - アルペジオ
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            float startNote = GUITAR_NOTES[random.nextInt(8)];
            for (int i = 0; i < 4; i++) {
                final int index = i;
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
                    playGuitarNote(player, startNote * (1 + index * 0.2f), 0.6f - index * 0.1f), i * 2L);
            }
        } else if (event.getAction().toString().contains("LEFT_CLICK")) {
            // 左クリック - ストローク
            playGuitarNote(player, GUITAR_NOTES[12], 0.9f);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        if (event.isSneaking()) {
            // スニーク開始 - 下降音階
            playGuitarNote(player, GUITAR_NOTES[14], 0.7f); // G
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
                playGuitarNote(player, GUITAR_NOTES[11], 0.6f), 3L); // E
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
                playGuitarNote(player, GUITAR_NOTES[7], 0.5f), 6L); // C
        } else {
            // スニーク終了 - 上昇音階
            playGuitarNote(player, GUITAR_NOTES[7], 0.5f); // C
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
                playGuitarNote(player, GUITAR_NOTES[11], 0.6f), 3L); // E
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // アイテム切り替え - ベンド音
        int noteIndex = Math.min(8 + (event.getNewSlot() % 8), GUITAR_NOTES.length - 1);
        float note = GUITAR_NOTES[noteIndex];
        playGuitarNote(player, note * 0.9f, 0.6f);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, note, 0.7f), 3L);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        // アイテムドロップ - 落下音
        int highNote = Math.min(16, GUITAR_NOTES.length - 1);
        playGuitarNote(player, GUITAR_NOTES[highNote], 0.8f); // 高音から
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[12], 0.6f), 4L); // 低音へ
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> 
            playGuitarNote(player, GUITAR_NOTES[7], 0.4f), 8L); // さらに低音へ
    }
}