package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "shadow_follower",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class ShadowFollowerEffect extends UnluckyEffectBase {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final Map<UUID, List<ArmorStand>> playerShadows = new HashMap<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();

    public ShadowFollowerEffect(JavaPlugin plugin) {
        super(plugin, "影法師症候群", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "👥 既に影法師症候群の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        player.sendMessage(ChatColor.RED + "👤 影法師症候群が発症しました！不気味な影があなたを追跡します...");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_AMBIENT, 1.0f, 0.8f);
        
        // 3つの影を生成
        createShadows(player);
        
        // 影を定期的に移動（1秒間隔で30回）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                updateShadowPositions(player);
                
                // 5秒ごとに不気味な効果
                if (count % 5 == 0) {
                    createSpookyEffects(player);
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
        
        // 影を削除
        removeShadows(playerId);
        
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "🌞 影法師症候群が治癒しました。影が消え去りました！");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                15, 0.5, 0.5, 0.5, 0.1
            );
        }
    }
    
    private void createShadows(Player player) {
        List<ArmorStand> shadows = new ArrayList<>();
        Location playerLoc = player.getLocation();
        
        // 3つの影を異なる位置に配置
        for (int i = 0; i < 3; i++) {
            double angle = (i * 120) * Math.PI / 180; // 120度間隔
            double distance = 3 + random.nextDouble() * 2; // 3-5ブロック距離
            
            Location shadowLoc = playerLoc.clone().add(
                Math.cos(angle) * distance,
                0,
                Math.sin(angle) * distance
            );
            
            // 地面の高さに調整
            shadowLoc.setY(playerLoc.getWorld().getHighestBlockYAt(shadowLoc) + 0.1);
            
            ArmorStand shadow = (ArmorStand) playerLoc.getWorld().spawnEntity(shadowLoc, EntityType.ARMOR_STAND);
            setupShadowAppearance(shadow);
            
            shadows.add(shadow);
        }
        
        playerShadows.put(player.getUniqueId(), shadows);
    }
    
    private void setupShadowAppearance(ArmorStand shadow) {
        // 影の外見設定
        shadow.setVisible(false); // 透明
        shadow.setGravity(false); // 重力無効
        shadow.setCanPickupItems(false);
        shadow.setMarker(true); // 当たり判定なし
        shadow.setSmall(true); // 小さいサイズ
        shadow.setInvulnerable(true); // 無敵
        shadow.setSilent(true); // 音を出さない
        
        // 黒い革防具で影らしさを演出
        shadow.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
        shadow.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        shadow.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        shadow.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        
        // 名前を設定（影らしい名前）
        String[] shadowNames = {"§8§l◯", "§0§l●", "§8§l▲", "§0§l■", "§8§l♦"};
        shadow.setCustomName(shadowNames[new Random().nextInt(shadowNames.length)]);
        shadow.setCustomNameVisible(true);
    }
    
    private void updateShadowPositions(Player player) {
        List<ArmorStand> shadows = playerShadows.get(player.getUniqueId());
        if (shadows == null) return;
        
        Location playerLoc = player.getLocation();
        
        for (int i = 0; i < shadows.size(); i++) {
            ArmorStand shadow = shadows.get(i);
            if (shadow == null || shadow.isDead()) continue;
            
            Location currentShadowLoc = shadow.getLocation();
            
            // プレイヤーとの距離を計算
            double distance = currentShadowLoc.distance(playerLoc);
            
            // 距離に応じた行動
            if (distance > 8) {
                // 遠すぎる場合：プレイヤーの近くにテレポート
                teleportShadowNearPlayer(shadow, playerLoc, i);
            } else if (distance < 2) {
                // 近すぎる場合：少し離れる
                moveShadowAway(shadow, playerLoc);
            } else {
                // 適度な距離：ゆっくりプレイヤーに向かって移動
                moveShadowTowardsPlayer(shadow, playerLoc);
            }
            
            // 影の周りにパーティクル
            spawnShadowParticles(shadow);
        }
    }
    
    private void teleportShadowNearPlayer(ArmorStand shadow, Location playerLoc, int shadowIndex) {
        double angle = (shadowIndex * 120 + random.nextInt(60)) * Math.PI / 180;
        double distance = 3 + random.nextDouble() * 3;
        
        Location newLoc = playerLoc.clone().add(
            Math.cos(angle) * distance,
            0,
            Math.sin(angle) * distance
        );
        
        newLoc.setY(playerLoc.getWorld().getHighestBlockYAt(newLoc) + 0.1);
        shadow.teleport(newLoc);
        
        // テレポート時のエフェクト
        shadow.getWorld().spawnParticle(
            Particle.SMOKE,
            shadow.getLocation().add(0, 1, 0),
            10, 0.3, 0.5, 0.3, 0.05
        );
    }
    
    private void moveShadowAway(ArmorStand shadow, Location playerLoc) {
        Vector direction = shadow.getLocation().toVector().subtract(playerLoc.toVector());
        direction.normalize();
        direction.multiply(0.3); // ゆっくり離れる
        direction.setY(0); // Y軸移動なし
        
        Location newLoc = shadow.getLocation().add(direction);
        newLoc.setY(playerLoc.getWorld().getHighestBlockYAt(newLoc) + 0.1);
        shadow.teleport(newLoc);
    }
    
    private void moveShadowTowardsPlayer(ArmorStand shadow, Location playerLoc) {
        Vector direction = playerLoc.toVector().subtract(shadow.getLocation().toVector());
        direction.normalize();
        direction.multiply(0.1 + random.nextDouble() * 0.2); // ランダムな速度
        direction.setY(0);
        
        Location newLoc = shadow.getLocation().add(direction);
        newLoc.setY(playerLoc.getWorld().getHighestBlockYAt(newLoc) + 0.1);
        shadow.teleport(newLoc);
        
        // プレイヤーの方を向く
        Vector lookDirection = playerLoc.toVector().subtract(newLoc.toVector());
        newLoc.setDirection(lookDirection);
        shadow.teleport(newLoc);
    }
    
    private void spawnShadowParticles(ArmorStand shadow) {
        if (random.nextInt(3) == 0) { // 33%の確率
            shadow.getWorld().spawnParticle(
                Particle.SMOKE,
                shadow.getLocation().add(0, 1, 0),
                2, 0.1, 0.3, 0.1, 0.01
            );
        }
        
        if (random.nextInt(5) == 0) { // 20%の確率
            shadow.getWorld().spawnParticle(
                Particle.ASH,
                shadow.getLocation().add(0, 0.5, 0),
                3, 0.2, 0.2, 0.2, 0.01
            );
        }
    }
    
    private void createSpookyEffects(Player player) {
        // 不気味な音効果
        String[] spookySounds = {
            "ENTITY_ENDERMAN_STARE",
            "ENTITY_WITCH_AMBIENT", 
            "AMBIENT_CAVE",
            "ENTITY_PHANTOM_AMBIENT"
        };
        
        try {
            Sound spookySound = Sound.valueOf(spookySounds[random.nextInt(spookySounds.length)]);
            player.playSound(player.getLocation(), spookySound, 0.3f, 0.8f);
        } catch (IllegalArgumentException e) {
            // サウンドが存在しない場合のフォールバック
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 0.3f, 0.8f);
        }
        
        // 不気味なメッセージ
        if (random.nextInt(3) == 0) {
            String[] spookyMessages = {
                "§8§o...誰かが見ている...",
                "§8§o...影が動いている...",
                "§8§o...後ろに何かいる...",
                "§8§o...逃げても無駄...",
                "§8§o...影は消えない..."
            };
            String message = spookyMessages[random.nextInt(spookyMessages.length)];
            player.sendMessage(message);
        }
    }
    
    private void removeShadows(UUID playerId) {
        List<ArmorStand> shadows = playerShadows.get(playerId);
        if (shadows != null) {
            for (ArmorStand shadow : shadows) {
                if (shadow != null && !shadow.isDead()) {
                    // 消失エフェクト
                    shadow.getWorld().spawnParticle(
                        Particle.LARGE_SMOKE,
                        shadow.getLocation().add(0, 1, 0),
                        15, 0.3, 0.5, 0.3, 0.1
                    );
                    shadow.remove();
                }
            }
            playerShadows.remove(playerId);
        }
    }
}