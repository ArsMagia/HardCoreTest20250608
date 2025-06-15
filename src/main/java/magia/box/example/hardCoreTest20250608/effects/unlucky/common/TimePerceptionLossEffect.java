package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "time_perception_loss",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class TimePerceptionLossEffect extends UnluckyEffectBase {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();
    
    private static final List<String> TIME_CONFUSION_MESSAGES = Arrays.asList(
        "⏰ あれ？もう1時間経ったの？",
        "⏱️ まだ5秒しか経ってない気がする...",
        "🕐 時計が壊れているようだ",
        "⏳ 時間が逆流している？",
        "🕘 永遠に感じられる瞬間",
        "⌚ 時が止まったかのよう...",
        "🕰️ あっという間に過ぎた気がする",
        "⏲️ 同じ瞬間を何度も経験している",
        "🕓 時間の概念が曖昧になってきた",
        "⏰ もう朝？それとも夜？"
    );
    
    private static final List<String> FAKE_TIME_MESSAGES = Arrays.asList(
        "§6現在時刻: §e午前25:99",
        "§6現在時刻: §e午後-3:77",
        "§6現在時刻: §e昨日の明日 10:XX",
        "§6現在時刻: §e来週の昨日 ??:??",
        "§6現在時刻: §e無限:永遠",
        "§6現在時刻: §e時間外労働中",
        "§6現在時刻: §e時空の狭間",
        "§6現在時刻: §e存在しない時刻"
    );
    
    private static final List<String> COUNTDOWN_MESSAGES = Arrays.asList(
        "⏰ 効果終了まで...∞秒",
        "⏰ 効果終了まで...マイナス10秒",
        "⏰ 効果終了まで...昨日",
        "⏰ 効果終了まで...来世",
        "⏰ 効果終了まで...時間外",
        "⏰ 効果終了まで...計算不可能",
        "⏰ 効果終了まで...既に終了している",
        "⏰ 効果終了まで...まだ始まっていない"
    );

    public TimePerceptionLossEffect(JavaPlugin plugin) {
        super(plugin, "時間感覚麻痺", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "⏰ 既に時間感覚麻痺の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        player.sendMessage(ChatColor.RED + "⏱️ 時間感覚麻痺症候群が発症しました！時間の感覚が狂います...");
        player.sendMessage(ChatColor.GRAY + "時が歪み、現実感が失われていきます...");
        player.playSound(player.getLocation(), Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.5f);
        
        // 軽い混乱効果を追加（視覚的な時間感覚の混乱を演出）
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 60, 0)); // 最初の3秒間
        
        // 時間混乱メッセージを不規則な間隔で表示
        scheduleTimeConfusionMessages(player, playerId);
        
        // 偽の時間表示を定期的に表示
        scheduleFakeTimeDisplay(player, playerId);
        
        // 偽のカウントダウンメッセージ
        scheduleFakeCountdown(player, playerId);
        
        // 時空歪曲のパーティクル効果
        scheduleTimeDistortionEffects(player, playerId);
        
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
            player.sendMessage(ChatColor.GREEN + "⏰ 時間感覚麻痺が治癒しました。時の流れが正常に戻りました！");
            player.sendMessage(ChatColor.AQUA + "現在時刻が正確に認識できるようになりました。");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                15, 0.5, 0.5, 0.5, 0.1
            );
            
            // 実際の時間を表示（回復の実感を与えるため）
            long currentTime = System.currentTimeMillis();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
            String realTime = sdf.format(new java.util.Date(currentTime));
            player.sendMessage(ChatColor.GREEN + "§l正確な現在時刻: " + realTime);
        }
    }
    
    private void scheduleTimeConfusionMessages(Player player, UUID playerId) {
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 15) {
                    this.cancel();
                    return;
                }
                
                String message = TIME_CONFUSION_MESSAGES.get(random.nextInt(TIME_CONFUSION_MESSAGES.size()));
                player.sendMessage(ChatColor.YELLOW + message);
                
                // ランダムな効果音
                if (random.nextInt(3) == 0) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.3f, 0.5f + random.nextFloat() * 1.0f);
                }
                
                count++;
            }
        }.runTaskTimer(plugin, 40L + random.nextInt(40), 40L + random.nextInt(80)); // 不規則な間隔
    }
    
    private void scheduleFakeTimeDisplay(Player player, UUID playerId) {
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 10) {
                    this.cancel();
                    return;
                }
                
                String fakeTime = FAKE_TIME_MESSAGES.get(random.nextInt(FAKE_TIME_MESSAGES.size()));
                player.sendMessage(fakeTime);
                
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.5f);
                
                count++;
            }
        }.runTaskTimer(plugin, 80L, 60L + random.nextInt(60)); // 3-6秒間隔
    }
    
    private void scheduleFakeCountdown(Player player, UUID playerId) {
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 8) {
                    this.cancel();
                    return;
                }
                
                String countdown = COUNTDOWN_MESSAGES.get(random.nextInt(COUNTDOWN_MESSAGES.size()));
                player.sendMessage(ChatColor.GOLD + countdown);
                
                count++;
            }
        }.runTaskTimer(plugin, 100L, 75L + random.nextInt(50)); // 4-6秒間隔
    }
    
    private void scheduleTimeDistortionEffects(Player player, UUID playerId) {
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 30) {
                    this.cancel();
                    return;
                }
                
                spawnTimeDistortionParticles(player);
                
                // 10秒ごとに時空歪曲の強いエフェクト
                if (count % 10 == 0) {
                    createTimeWarpEffect(player);
                }
                
                count++;
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1秒間隔
    }
    
    private void spawnTimeDistortionParticles(Player player) {
        if (!player.isOnline()) return;
        
        // 時空歪曲を表現するパーティクル
        org.bukkit.Location loc = player.getLocation().add(0, 1, 0);
        
        // 30%の確率でパーティクル表示
        if (random.nextInt(10) < 3) {
            player.getWorld().spawnParticle(
                Particle.PORTAL,
                loc,
                5, 0.5, 0.5, 0.5, 0.1
            );
        }
        
        // 20%の確率でエンドロッドパーティクル（時間の流れを表現）
        if (random.nextInt(5) == 0) {
            player.getWorld().spawnParticle(
                Particle.END_ROD,
                loc,
                3, 0.3, 0.8, 0.3, 0.05
            );
        }
        
        // 15%の確率でエンチャントパーティクル
        if (random.nextInt(7) == 0) {
            player.getWorld().spawnParticle(
                Particle.ENCHANT,
                loc,
                8, 0.8, 0.8, 0.8, 0.3
            );
        }
    }
    
    private void createTimeWarpEffect(Player player) {
        if (!player.isOnline()) return;
        
        player.sendMessage(ChatColor.DARK_PURPLE + "§l⚡ 時空が歪んでいます... ⚡");
        
        // 時空歪曲の音効果
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.3f);
        
        // 強い視覚効果
        org.bukkit.Location loc = player.getLocation().add(0, 1, 0);
        
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            loc,
            20, 1.0, 1.0, 1.0, 0.5
        );
        
        player.getWorld().spawnParticle(
            Particle.END_ROD,
            loc,
            10, 0.5, 1.5, 0.5, 0.1
        );
        
        // 軽い混乱効果（時空歪曲の実感）
        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 40, 0)); // 2秒間
        
        // 時空歪曲メッセージ
        String[] warpMessages = {
            "§5時が止まった瞬間...",
            "§5過去と未来が交錯している...",
            "§5時間軸が不安定です...",
            "§5現在・過去・未来が混在...",
            "§5時の流れが逆転中...",
            "§5時空連続体に異常発生..."
        };
        
        String message = warpMessages[random.nextInt(warpMessages.length)];
        
        // 遅延してメッセージ表示（時空歪曲の演出）
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + message);
            }
        }, 10L);
    }
}