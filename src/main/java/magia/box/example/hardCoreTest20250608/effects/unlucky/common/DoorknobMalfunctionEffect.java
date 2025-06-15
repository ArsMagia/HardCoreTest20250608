package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "doorknob_malfunction",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class DoorknobMalfunctionEffect extends UnluckyEffectBase implements Listener {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();
    
    // 故障対象のブロック
    private static final Set<Material> MALFUNCTION_BLOCKS = Set.of(
        // ドア類
        Material.OAK_DOOR, Material.BIRCH_DOOR, Material.SPRUCE_DOOR,
        Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
        Material.CRIMSON_DOOR, Material.WARPED_DOOR, Material.IRON_DOOR,
        
        // トラップドア
        Material.OAK_TRAPDOOR, Material.BIRCH_TRAPDOOR, Material.SPRUCE_TRAPDOOR,
        Material.JUNGLE_TRAPDOOR, Material.ACACIA_TRAPDOOR, Material.DARK_OAK_TRAPDOOR,
        Material.CRIMSON_TRAPDOOR, Material.WARPED_TRAPDOOR, Material.IRON_TRAPDOOR,
        
        // ゲート
        Material.OAK_FENCE_GATE, Material.BIRCH_FENCE_GATE, Material.SPRUCE_FENCE_GATE,
        Material.JUNGLE_FENCE_GATE, Material.ACACIA_FENCE_GATE, Material.DARK_OAK_FENCE_GATE,
        Material.CRIMSON_FENCE_GATE, Material.WARPED_FENCE_GATE,
        
        // 収納系
        Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST,
        Material.SHULKER_BOX, Material.BARREL,
        
        // 作業台系
        Material.CRAFTING_TABLE, Material.FURNACE, Material.ANVIL,
        Material.ENCHANTING_TABLE, Material.BREWING_STAND,
        
        // レッドストーン系
        Material.LEVER, Material.STONE_BUTTON, Material.OAK_BUTTON,
        Material.BIRCH_BUTTON, Material.SPRUCE_BUTTON, Material.JUNGLE_BUTTON,
        Material.ACACIA_BUTTON, Material.DARK_OAK_BUTTON, Material.CRIMSON_BUTTON,
        Material.WARPED_BUTTON
    );
    
    private static final List<String> MALFUNCTION_MESSAGES = Arrays.asList(
        "🚪 ドアノブが回らない！",
        "🔧 機械が故障しているようだ...",
        "⚙️ ガチャガチャ... 動かない！",
        "🔒 何かが詰まっているみたい",
        "🚪 扉が重くて開かない...",
        "📦 蓋が固くて開けられない",
        "🔨 道具が反応しない！",
        "⚡ 電気系統に異常があるようだ",
        "🧰 メンテナンスが必要かも...",
        "❌ 操作を受け付けません"
    );

    public DoorknobMalfunctionEffect(JavaPlugin plugin) {
        super(plugin, "ドアノブ故障", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "🔧 既にドアノブ故障の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        // リスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        player.sendMessage(ChatColor.RED + "🔧 ドアノブ故障症候群が発症しました！あらゆる機械や扉が故障します...");
        player.sendMessage(ChatColor.GRAY + "操作がうまくいかなくなります...");
        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1.0f, 0.5f);
        
        // 定期的に故障音を再生
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                // 20%の確率で故障音
                if (random.nextInt(5) == 0) {
                    playMalfunctionSound(player);
                }
                
                // 10秒ごとに故障リマインダー
                if (count % 10 == 0) {
                    player.sendMessage(ChatColor.GRAY + "🔧 機械類が故障中...");
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
            player.sendMessage(ChatColor.GREEN + "🔧 ドアノブ故障症候群が治癒しました。全ての機械が正常に動作します！");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.5f);
            
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
    
    private void playMalfunctionSound(Player player) {
        Sound[] malfunctionSounds = {
            Sound.BLOCK_IRON_DOOR_CLOSE,
            Sound.BLOCK_CHEST_LOCKED,
            Sound.ITEM_SHIELD_BLOCK,
            Sound.BLOCK_ANVIL_LAND,
            Sound.UI_BUTTON_CLICK
        };
        
        Sound sound = malfunctionSounds[random.nextInt(malfunctionSounds.length)];
        float pitch = 0.5f + random.nextFloat() * 0.5f; // 0.5-1.0の範囲
        
        player.playSound(player.getLocation(), sound, 0.7f, pitch);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        
        if (!affectedPlayers.contains(player.getUniqueId())) {
            return;
        }
        
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        
        Material blockType = clickedBlock.getType();
        
        // 故障対象のブロックかチェック
        if (!MALFUNCTION_BLOCKS.contains(blockType)) {
            return;
        }
        
        // 70%の確率で故障
        if (random.nextInt(100) < 70) {
            event.setCancelled(true);
            
            // 故障メッセージと効果
            String message = MALFUNCTION_MESSAGES.get(random.nextInt(MALFUNCTION_MESSAGES.size()));
            player.sendMessage(ChatColor.RED + message);
            
            // 故障音
            playMalfunctionSound(player);
            
            // 故障のパーティクル効果
            spawnMalfunctionParticles(clickedBlock.getLocation().add(0.5, 0.5, 0.5));
            
            // ブロックタイプに応じた特別なメッセージ
            showSpecificMalfunctionMessage(player, blockType);
            
            // 稀に一時的な成功（希望を与えてから再び失敗させる演出）
            if (random.nextInt(10) == 0) {
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.YELLOW + "🔧 一瞬直ったかと思ったが... また故障した");
                        playMalfunctionSound(player);
                    }
                }, 10L);
            }
        } else {
            // 30%の確率で正常動作（ただし警告メッセージ付き）
            player.sendMessage(ChatColor.YELLOW + "🔧 何とか動いたが、調子が悪そうだ...");
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 0.5f, 0.8f);
        }
    }
    
    private void spawnMalfunctionParticles(org.bukkit.Location location) {
        // 故障を表現するパーティクル
        location.getWorld().spawnParticle(
            Particle.SMOKE,
            location,
            8, 0.3, 0.3, 0.3, 0.05
        );
        
        location.getWorld().spawnParticle(
            Particle.CRIT,
            location,
            5, 0.4, 0.4, 0.4, 0.1
        );
        
        // 稀に火花のような効果
        if (random.nextInt(4) == 0) {
            location.getWorld().spawnParticle(
                Particle.LAVA,
                location,
                3, 0.2, 0.2, 0.2, 0.1
            );
        }
    }
    
    private void showSpecificMalfunctionMessage(Player player, Material blockType) {
        String specificMessage = "";
        
        if (blockType.name().contains("DOOR")) {
            specificMessage = "扉のヒンジが錆びているようだ...";
        } else if (blockType.name().contains("CHEST")) {
            specificMessage = "鍵穴に何かが詰まっている...";
        } else if (blockType.name().contains("BUTTON")) {
            specificMessage = "ボタンが押し込まれたまま戻らない...";
        } else if (blockType.name().contains("LEVER")) {
            specificMessage = "レバーが固くて動かない...";
        } else if (blockType == Material.FURNACE) {
            specificMessage = "かまどの扉が歪んで開かない...";
        } else if (blockType == Material.CRAFTING_TABLE) {
            specificMessage = "作業台の引き出しが開かない...";
        } else if (blockType == Material.ANVIL) {
            specificMessage = "金床が重すぎて使えない...";
        } else if (blockType.name().contains("GATE")) {
            specificMessage = "ゲートの留め金が故障している...";
        } else {
            specificMessage = "内部機構に問題があるようだ...";
        }
        
        // 1秒後に詳細メッセージを表示
        String finalSpecificMessage = specificMessage;
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.GRAY + "§o" + finalSpecificMessage);
            }
        }, 20L);
    }
}