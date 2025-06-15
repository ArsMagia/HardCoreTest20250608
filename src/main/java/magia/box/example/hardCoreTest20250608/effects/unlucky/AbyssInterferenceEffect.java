package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 深淵の干渉
 * 10秒後にラッキーボックスを持たない全プレイヤーに全デバフとダメージを与える恐ろしい効果
 */
public class AbyssInterferenceEffect extends UnluckyEffectBase {

    public AbyssInterferenceEffect(JavaPlugin plugin) {
        super(plugin, "深淵の干渉", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player cursedPlayer) {
        // 深淵の干渉開始の警告
        cursedPlayer.sendMessage(ChatColor.DARK_PURPLE + "💀 あなたは深淵の力を解放してしまいました...");
        
        // 初期エフェクト
        cursedPlayer.playSound(cursedPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 2.0f, 0.3f);
        cursedPlayer.getWorld().spawnParticle(
            Particle.DUST,
            cursedPlayer.getLocation().add(0, 2, 0),
            100, 3, 3, 3, 0.3,
            new Particle.DustOptions(Color.fromRGB(32, 0, 32), 3.0f)
        );
        
        // 全プレイヤーに警告タイトル表示
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(
                ChatColor.DARK_RED + "💀 深淵の干渉 💀",
                ChatColor.RED + "10秒以内にラッキーボックスを手に持て！",
                10, 100, 20
            );
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.5f);
        }
        
        // サーバー全体へのアナウンス
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.MAGIC + "aaa" + ChatColor.RESET + ChatColor.DARK_RED + " 深淵の干渉が発動 " + ChatColor.MAGIC + "aaa");
        Bukkit.broadcastMessage(ChatColor.RED + "💀 " + cursedPlayer.getName() + " が深淵の力を解放しました！");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠ 警告: 10秒以内にラッキーボックスをメインハンドに持ってください！");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "📦 持たない場合、全デバフ+即時ダメージを受けます！");
        Bukkit.broadcastMessage("");
        
        // 10秒のカウントダウン開始
        startCountdown();
        
        return getDescription();
    }
    
    /**
     * 10秒カウントダウンを開始
     */
    private void startCountdown() {
        new BukkitRunnable() {
            int countdown = 10;
            
            @Override
            public void run() {
                if (countdown <= 0) {
                    // 深淵の干渉を実行
                    executeAbyssInterference();
                    this.cancel();
                    return;
                }
                
                // カウントダウン表示（最後の5秒間）
                if (countdown <= 5) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendTitle(
                            ChatColor.DARK_RED + "💀 " + countdown + " 💀",
                            ChatColor.RED + "ラッキーボックスを手に持て！",
                            0, 20, 0
                        );
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 2.0f);
                    }
                    
                    Bukkit.broadcastMessage(ChatColor.DARK_RED + "💀 深淵の干渉まで " + countdown + " 秒...");
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 20L, 20L); // 1秒間隔
    }
    
    /**
     * 深淵の干渉を実行
     */
    private void executeAbyssInterference() {
        List<Player> affectedPlayers = new ArrayList<>();
        List<Player> protectedPlayers = new ArrayList<>();
        
        // 全プレイヤーをチェック
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            
            // ラッキーボックス（ネザースター）を持っているかチェック
            if (mainHand != null && mainHand.getType() == Material.NETHER_STAR &&
                mainHand.hasItemMeta() && mainHand.getItemMeta().hasDisplayName() &&
                mainHand.getItemMeta().getDisplayName().contains("ラッキーボックス")) {
                
                // 保護されたプレイヤー
                protectedPlayers.add(player);
                player.sendMessage(ChatColor.GREEN + "✨ ラッキーボックスがあなたを深淵から守りました！");
                player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
                player.getWorld().spawnParticle(
                    Particle.ENCHANT,
                    player.getLocation().add(0, 1, 0),
                    20, 1, 1, 1, 0.1
                );
                
            } else {
                // 深淵の影響を受けるプレイヤー
                affectedPlayers.add(player);
                applyAbyssCurse(player);
            }
        }
        
        // 結果の発表
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💀 深淵の干渉が完了しました");
        Bukkit.broadcastMessage(ChatColor.GREEN + "✨ 保護されたプレイヤー: " + protectedPlayers.size() + "人");
        Bukkit.broadcastMessage(ChatColor.RED + "💀 影響を受けたプレイヤー: " + affectedPlayers.size() + "人");
        
        if (!protectedPlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "保護: " + getPlayerNames(protectedPlayers));
        }
        if (!affectedPlayers.isEmpty()) {
            Bukkit.broadcastMessage(ChatColor.GRAY + "被害: " + getPlayerNames(affectedPlayers));
        }
        Bukkit.broadcastMessage("");
    }
    
    /**
     * プレイヤーに深淵の呪いを適用
     */
    private void applyAbyssCurse(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "💀 深淵の力があなたを飲み込みました...");
        
        // 即時ダメージ（体力が6❤以上の場合）
        if (player.getHealth() >= 12.0) { // 12HP = 6❤
            player.damage(4.0); // 4HP = 2❤
            player.sendMessage(ChatColor.RED + "⚡ 深淵の力で2❤のダメージを受けました！");
        }
        
        // 全デバフ効果を適用（即時ダメージを除く）
        List<PotionEffectType> debuffs = getAllDebuffEffects();
        
        for (PotionEffectType debuff : debuffs) {
            player.addPotionEffect(new PotionEffect(debuff, 200, 0)); // 10秒間、レベル1
        }
        
        player.sendMessage(ChatColor.DARK_PURPLE + "💀 " + debuffs.size() + "種類のデバフを10秒間受けました！");
        
        // ガラスを壊す音を1秒に5回連続
        playGlassBreakSounds(player);
        
        // 深淵のパーティクルエフェクト
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 1, 0),
            50, 1, 1, 1, 0.2,
            new Particle.DustOptions(Color.fromRGB(16, 0, 16), 2.0f)
        );
    }
    
    /**
     * 全デバフ効果のリストを取得（即時ダメージを除く）
     */
    private List<PotionEffectType> getAllDebuffEffects() {
        List<PotionEffectType> debuffs = new ArrayList<>();
        
        // 主要なデバフ効果
        debuffs.add(PotionEffectType.WEAKNESS);           // 弱体化
        debuffs.add(PotionEffectType.SLOWNESS);           // 鈍足
        debuffs.add(PotionEffectType.MINING_FATIGUE);     // 採掘疲労
        debuffs.add(PotionEffectType.NAUSEA);             // 吐き気
        debuffs.add(PotionEffectType.BLINDNESS);          // 盲目
        debuffs.add(PotionEffectType.HUNGER);             // 空腹
        debuffs.add(PotionEffectType.POISON);             // 毒
        debuffs.add(PotionEffectType.WITHER);             // ウィザー
        debuffs.add(PotionEffectType.LEVITATION);         // 浮遊
        debuffs.add(PotionEffectType.UNLUCK);             // 不運
        debuffs.add(PotionEffectType.SLOW_FALLING);       // 低速落下（動きを制限）
        
        return debuffs;
    }
    
    /**
     * ガラスを壊す音を1秒に5回連続で再生
     */
    private void playGlassBreakSounds(Player player) {
        new BukkitRunnable() {
            int soundCount = 0;
            final int maxSounds = 5;
            
            @Override
            public void run() {
                if (soundCount >= maxSounds) {
                    this.cancel();
                    return;
                }
                
                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.8f + (soundCount * 0.1f));
                soundCount++;
            }
        }.runTaskTimer(plugin, 0L, 4L); // 4tick間隔 = 0.2秒間隔で5回 = 1秒
    }
    
    /**
     * プレイヤー名のリストを文字列に変換
     */
    private String getPlayerNames(List<Player> players) {
        if (players.isEmpty()) return "なし";
        
        StringBuilder names = new StringBuilder();
        for (int i = 0; i < players.size(); i++) {
            if (i > 0) names.append(", ");
            names.append(players.get(i).getName());
        }
        return names.toString();
    }
}