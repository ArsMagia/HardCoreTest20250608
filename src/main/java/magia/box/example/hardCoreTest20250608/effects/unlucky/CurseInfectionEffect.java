package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * ウイルス型連鎖感染システム
 * フェーズ1: 初回感染（8ブロック範囲）
 * フェーズ2: 感染者が新たな感染源となる（連鎖反応）
 * フェーズ3: 感染レベルに応じて効果強化
 * 治療: 牛乳バケツまたは治癒ポーションで回復可能
 */
public class CurseInfectionEffect extends UnluckyEffectBase {

    // 感染状態管理
    private final Map<UUID, Integer> infectionLevels = new HashMap<>();
    private final Set<UUID> quarantined = new HashSet<>();
    
    public CurseInfectionEffect(JavaPlugin plugin) {
        super(plugin, "呪いの伝染", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player patient0) {
        infectionLevels.clear();
        quarantined.clear();
        
        patient0.sendMessage(ChatColor.DARK_RED + "⚠ 呪いのウイルスが発動しました！");
        patient0.sendMessage(ChatColor.YELLOW + "🦠 連鎖感染システムが始動します...");
        patient0.sendMessage(ChatColor.GOLD + "🥛 治療法: 牛乳バケツまたは治癒ポーションで回復可能！");
        
        // 初期エフェクト
        patient0.playSound(patient0.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 1.0f, 0.5f);
        patient0.getWorld().spawnParticle(
            Particle.DUST,
            patient0.getLocation().add(0, 2, 0),
            20, 1, 1, 1, 0.1,
            new Particle.DustOptions(Color.fromRGB(128, 0, 128), 1.5f)
        );
        
        // 患者零号をレベル1で感染登録
        infectionLevels.put(patient0.getUniqueId(), 1);
        
        // 初回感染を実行
        performInitialInfection(patient0);
        
        // 連鎖感染システム開始
        startInfectionChain();
        
        return getDescription();
    }
    
    /**
     * 初回感染を実行
     */
    private void performInitialInfection(Player patient0) {
        Location center = patient0.getLocation();
        List<Player> newlyInfected = new ArrayList<>();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(patient0) && 
                player.getLocation().distance(center) <= 8 &&
                !infectionLevels.containsKey(player.getUniqueId())) {
                
                // レベル1で感染
                infectPlayer(player, 1);
                newlyInfected.add(player);
            }
        }
        
        // 感染結果の通知
        if (!newlyInfected.isEmpty()) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "🦠 初回感染が発生しました！");
            Bukkit.broadcastMessage(ChatColor.RED + "🐈 感染者: " + newlyInfected.size() + "人");
            
            for (Player infected : newlyInfected) {
                Bukkit.broadcastMessage(ChatColor.GRAY + "- " + infected.getName());
            }
            Bukkit.broadcastMessage("");
        } else {
            patient0.sendMessage(ChatColor.GREEN + "✅ 周囲にプレイヤーがいないため、感染は拡大しませんでした。");
        }
    }
    
    /**
     * 連鎖感染システムを開始
     */
    private void startInfectionChain() {
        new BukkitRunnable() {
            int wave = 1;
            final int maxWaves = 5; // 最大5波まで
            
            @Override
            public void run() {
                if (wave > maxWaves || infectionLevels.isEmpty()) {
                    finishInfection();
                    this.cancel();
                    return;
                }
                
                // 現在の感染者から新たな感染を実行
                performChainInfection(wave);
                
                // 感染レベルに応じて効果を強化
                updateInfectionEffects();
                
                wave++;
            }
        }.runTaskTimer(plugin, 60L, 60L); // 3秒間隔
        
        // 治癒モニタリングタスク
        startHealingMonitor();
    }
    
    /**
     * 連鎖感染を実行
     */
    private void performChainInfection(int wave) {
        List<Player> currentInfected = new ArrayList<>();
        List<Player> newlyInfected = new ArrayList<>();
        
        // 現在の感染者を収集
        for (UUID playerId : infectionLevels.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                currentInfected.add(player);
            }
        }
        
        // 各感染者から新たな感染を拡大
        for (Player infectedPlayer : currentInfected) {
            Location center = infectedPlayer.getLocation();
            int infectionLevel = infectionLevels.get(infectedPlayer.getUniqueId());
            int range = Math.min(6 + infectionLevel, 10); // レベルに応じて範囲拡大
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!infectionLevels.containsKey(player.getUniqueId()) &&
                    player.getLocation().distance(center) <= range &&
                    !quarantined.contains(player.getUniqueId())) {
                    
                    // 50%の確率で感染
                    if (Math.random() < 0.5) {
                        infectPlayer(player, 1);
                        newlyInfected.add(player);
                    }
                }
            }
        }
        
        // 連鎖感染結果の通知
        if (!newlyInfected.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "🔥 第" + wave + "波: 連鎖感染が発生！ " + newlyInfected.size() + "人が新たに感染しました！");
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "🚫 第" + wave + "波: 新たな感染は発生しませんでした。");
        }
    }
    
    /**
     * プレイヤーを感染させる
     */
    private void infectPlayer(Player player, int level) {
        infectionLevels.put(player.getUniqueId(), level);
        
        // レベルに応じた効果を適用
        int duration = 400 + (level * 100); // レベルが高いほど長時間
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, level - 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, level - 1));
        
        // レベル3以上で追加効果
        if (level >= 3) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, duration, 0));
        }
        
        player.sendMessage(ChatColor.DARK_PURPLE + "🦠 呪いに感染しました！レベル" + level);
        player.sendMessage(ChatColor.GOLD + "🥛 治療法: 牛乳バケツを使用してください！");
        
        // 感染エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 0.8f, 1.5f);
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 1, 0),
            10, 0.5, 0.5, 0.5, 0.1,
            new Particle.DustOptions(Color.PURPLE, 1.0f)
        );
    }
    
    /**
     * 感染効果を更新・強化
     */
    private void updateInfectionEffects() {
        for (Map.Entry<UUID, Integer> entry : infectionLevels.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                int currentLevel = entry.getValue();
                int newLevel = Math.min(currentLevel + 1, 5); // 最大5レベルまで
                
                if (newLevel > currentLevel) {
                    infectionLevels.put(entry.getKey(), newLevel);
                    player.sendMessage(ChatColor.RED + "🔥 感染レベルが" + newLevel + "に上昇しました！");
                    
                    // 強化された効果を適用
                    infectPlayer(player, newLevel);
                }
            }
        }
    }
    
    /**
     * 治癒モニタリングタスク
     */
    private void startHealingMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (infectionLevels.isEmpty()) {
                    this.cancel();
                    return;
                }
                
                List<UUID> healedPlayers = new ArrayList<>();
                
                for (UUID playerId : infectionLevels.keySet()) {
                    Player player = Bukkit.getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        // ポーション効果が消えているかチェック（治癒アイテム使用）
                        if (!player.hasPotionEffect(PotionEffectType.WEAKNESS) &&
                            !player.hasPotionEffect(PotionEffectType.SLOWNESS)) {
                            healedPlayers.add(playerId);
                            healPlayer(player);
                        }
                    } else {
                        healedPlayers.add(playerId); // オフラインプレイヤーをクリーンアップ
                    }
                }
                
                // 治癒されたプレイヤーを削除
                for (UUID playerId : healedPlayers) {
                    infectionLevels.remove(playerId);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1秒間隔でチェック
    }
    
    /**
     * プレイヤーを治癒
     */
    private void healPlayer(Player player) {
        player.sendMessage(ChatColor.GREEN + "✨ 呪いが浄化されました！治癒完了です。");
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        
        // 治癒エフェクト
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 2, 0),
            10, 1, 1, 1, 0.1
        );
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "✨ " + player.getName() + "が呪いから治癒されました！");
    }
    
    /**
     * 感染終了処理
     */
    private void finishInfection() {
        if (!infectionLevels.isEmpty()) {
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "😷 呪いのウイルスが自然治癒し始めました...");
            
            // 残っている感染者を強制治癒
            for (UUID playerId : infectionLevels.keySet()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null && player.isOnline()) {
                    player.removePotionEffect(PotionEffectType.WEAKNESS);
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    player.removePotionEffect(PotionEffectType.NAUSEA);
                    healPlayer(player);
                }
            }
            
            infectionLevels.clear();
        }
        
        Bukkit.broadcastMessage(ChatColor.GREEN + "✅ 呪いの感染が完全に終息しました。");
        quarantined.clear();
    }
}