package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CurseProxyEffect extends UnluckyEffectBase implements Listener {
    private static final Set<UUID> cursedPlayers = new HashSet<>();
    private static final Set<BukkitTask> activeTasks = new HashSet<>();

    public CurseProxyEffect(JavaPlugin plugin) {
        super(plugin, "呪いの肩代わり", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        cursedPlayers.add(player.getUniqueId());
        
        // プレイヤーにカラフルなパーティクルで呪いのタグを表示
        showCurseTag(player);
        
        player.sendMessage(ChatColor.DARK_PURPLE + "💀 あなたは呪いのタグを背負いました...");
        player.sendMessage(ChatColor.GRAY + "他のプレイヤーを殴ることで呪いを移すことができます。");
        player.sendMessage(ChatColor.RED + "20秒後に呪いが発動します！");
        
        // 最初のプレイヤーに対して呪い発動タスクを設定
        setupCurseActivationForPlayer(player);
        
        // リスナーを登録
        Bukkit.getPluginManager().registerEvents(this, plugin);
        
        return "呪いの肩代わりが発動しました";
    }

    private void setupCurseActivationForPlayer(Player player) {
        // 20秒後に呪いを発動
        BukkitTask curseTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (cursedPlayers.contains(player.getUniqueId())) {
                    activateCurse(player);
                    cursedPlayers.remove(player.getUniqueId());
                }
                activeTasks.remove(this);
            }
        }.runTaskLater(plugin, 400L); // 20秒 = 400 tick
        
        activeTasks.add(curseTask);
        
        // パーティクル表示タスク
        BukkitTask particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!cursedPlayers.contains(player.getUniqueId()) || !player.isOnline()) {
                    this.cancel();
                    activeTasks.remove(this);
                    return;
                }
                showCurseTag(player);
            }
        }.runTaskTimer(plugin, 0L, 20L); // 1秒ごと
        
        activeTasks.add(particleTask);
    }

    private void showCurseTag(Player player) {
        Location loc = player.getLocation().add(0, 2.5, 0);
        World world = player.getWorld();
        
        // 紫と黒のパーティクルで呪いのオーラを表現
        world.spawnParticle(Particle.DRAGON_BREATH, loc, 10, 0.5, 0.5, 0.5, 0.02);
        world.spawnParticle(Particle.WITCH, loc, 5, 0.3, 0.3, 0.3, 0.5);
        
        // 音効果
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SOUL_SAND_BREAK, 0.5f, 0.5f);
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !(event.getEntity() instanceof Player victim)) {
            return;
        }
        
        if (!cursedPlayers.contains(attacker.getUniqueId())) {
            return;
        }
        
        // 呪いを移す
        cursedPlayers.remove(attacker.getUniqueId());
        cursedPlayers.add(victim.getUniqueId());
        
        attacker.sendMessage(ChatColor.GREEN + "✨ 呪いのタグを " + victim.getName() + " に移しました！");
        victim.sendMessage(ChatColor.DARK_PURPLE + "💀 " + attacker.getName() + " から呪いのタグを受け取りました...");
        victim.sendMessage(ChatColor.RED + "20秒後に呪いが発動します！");
        
        // エフェクト
        attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_GHAST_SCREAM, 0.8f, 0.8f);
        
        // パーティクル移動エフェクト
        showCurseTransfer(attacker.getLocation(), victim.getLocation());
        
        // 新しい被害者に対して20秒後の呪い発動タスクを設定
        setupCurseActivationForPlayer(victim);
    }

    private void showCurseTransfer(Location from, Location to) {
        new BukkitRunnable() {
            double t = 0;
            @Override
            public void run() {
                t += 0.1;
                if (t >= 1.0) {
                    this.cancel();
                    return;
                }
                
                Location mid = from.clone().add(to.clone().subtract(from).multiply(t));
                mid.getWorld().spawnParticle(Particle.DRAGON_BREATH, mid, 3, 0.1, 0.1, 0.1, 0.01);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void activateCurse(Player player) {
        if (!player.isOnline()) return;
        
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        
        // 最大体力が5❤以下の場合は無効
        if (maxHealth <= 10.0) { // 5❤ = 10HP
            player.sendMessage(ChatColor.YELLOW + "⚠ 体力が少なすぎるため、呪いの効果は無効化されました。");
            return;
        }
        
        // 呪いの効果を適用
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1)); // Weakness II 3s
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1)); // Slowness II 3s
        player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1)); // Weakness II 3s (重複を確認)
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 1)); // Slow Falling II 3s
        
        // 最大体力を1❤減少
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth - 2.0);
        
        // 現在の体力も調整
        if (player.getHealth() > maxHealth - 2.0) {
            player.setHealth(maxHealth - 2.0);
        }
        
        player.sendMessage(ChatColor.DARK_RED + "💀 呪いが発動しました！最大体力が1❤減少しました...");
        
        // 呪いの発動エフェクト
        Location loc = player.getLocation();
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 50, 1, 1, 1, 0.1);
        player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, loc, 30, 1, 2, 1, 0.05);
        player.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.8f, 1.2f);
        
        // 周囲のプレイヤーにも通知
        for (Player nearbyPlayer : player.getWorld().getPlayers()) {
            if (nearbyPlayer != player && nearbyPlayer.getLocation().distance(player.getLocation()) <= 30) {
                nearbyPlayer.sendMessage(ChatColor.DARK_PURPLE + "💀 " + player.getName() + " に呪いが発動しました...");
            }
        }
    }

    public static void cleanup() {
        cursedPlayers.clear();
        for (BukkitTask task : activeTasks) {
            if (!task.isCancelled()) {
                task.cancel();
            }
        }
        activeTasks.clear();
    }

    public static boolean isCursed(Player player) {
        return cursedPlayers.contains(player.getUniqueId());
    }
}