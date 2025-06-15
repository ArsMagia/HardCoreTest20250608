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
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "view_lock_syndrome",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class ViewLockSyndromeEffect extends UnluckyEffectBase {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();

    public ViewLockSyndromeEffect(JavaPlugin plugin) {
        super(plugin, "視点固定症", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "👁️ 既に視点固定症の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        player.sendMessage(ChatColor.RED + "👁️‍🗨️ 視点固定症が発症しました！視線が勝手に動き回ります...");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.5f);
        
        // 視点固定の視覚効果
        spawnViewLockParticles(player);
        
        // 視点を定期的に強制変更（3秒間隔で10回）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 10) {
                    this.cancel();
                    return;
                }
                
                forceViewDirection(player);
                count++;
            }
        }.runTaskTimer(plugin, 60L, 60L); // 3秒間隔
        
        // ランダムな小刻みな視点移動（0.5秒間隔で60回）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 60) {
                    this.cancel();
                    return;
                }
                
                // 50%の確率で小刻みな視点移動
                if (random.nextInt(2) == 0) {
                    addViewJitter(player);
                }
                count++;
            }
        }.runTaskTimer(plugin, 10L, 10L); // 0.5秒間隔
        
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
            player.sendMessage(ChatColor.GREEN + "👁️ 視点固定症が治癒しました。視線が自由になりました！");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 2, 0),
                10, 0.5, 0.5, 0.5, 0.1
            );
        }
    }
    
    private void spawnViewLockParticles(Player player) {
        if (!player.isOnline()) return;
        
        // 目の周りの視覚効果
        Location eyeLocation = player.getEyeLocation();
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            eyeLocation.add(0.3, 0, 0),
            5, 0.1, 0.1, 0.1, 0.1
        );
        
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            eyeLocation.add(-0.6, 0, 0),
            5, 0.1, 0.1, 0.1, 0.1
        );
        
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            eyeLocation,
            3, 0.2, 0.2, 0.2, 0.05
        );
    }
    
    private void forceViewDirection(Player player) {
        if (!player.isOnline()) return;
        
        Location currentLocation = player.getLocation();
        
        // ランダムな方向を生成
        float randomYaw = random.nextFloat() * 360f; // 0-360度
        float randomPitch = (random.nextFloat() - 0.5f) * 60f; // -30~+30度
        
        // 視点を強制変更
        currentLocation.setYaw(randomYaw);
        currentLocation.setPitch(randomPitch);
        player.teleport(currentLocation);
        
        // 視点変更の通知と効果
        player.sendMessage(ChatColor.GRAY + "👁️ 視線が勝手に動きました！");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.5f, 1.2f);
        
        // 視点変更時のパーティクル
        spawnViewLockParticles(player);
        
        // 混乱のメッセージをランダムに表示
        if (random.nextInt(3) == 0) {
            String[] confusionMessages = {
                "§7あれ？どこを見ていたんだっけ？",
                "§7視線が...コントロールできない！",
                "§7目が勝手に動く！",
                "§7どこを見ているの？",
                "§7視点が固定されない！"
            };
            String message = confusionMessages[random.nextInt(confusionMessages.length)];
            player.sendMessage(message);
        }
    }
    
    private void addViewJitter(Player player) {
        if (!player.isOnline()) return;
        
        Location currentLocation = player.getLocation();
        
        // 小刻みなランダム移動（現在の視点から±10度以内）
        float yawJitter = (random.nextFloat() - 0.5f) * 20f; // ±10度
        float pitchJitter = (random.nextFloat() - 0.5f) * 10f; // ±5度
        
        currentLocation.setYaw(currentLocation.getYaw() + yawJitter);
        currentLocation.setPitch(Math.max(-90, Math.min(90, currentLocation.getPitch() + pitchJitter)));
        player.teleport(currentLocation);
        
        // 20%の確率で小さな音を再生
        if (random.nextInt(5) == 0) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.1f, 1.8f);
        }
    }
}