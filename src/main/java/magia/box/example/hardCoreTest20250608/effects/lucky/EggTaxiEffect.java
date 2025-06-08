package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class EggTaxiEffect extends LuckyEffectBase {

    private final Random random = new Random();

    public EggTaxiEffect(JavaPlugin plugin) {
        super(plugin, "卵タクシー", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // ランダムな方向を決定（東西南北）
        Vector[] directions = {
            new Vector(1, 0, 0),   // 東
            new Vector(-1, 0, 0),  // 西
            new Vector(0, 0, 1),   // 南
            new Vector(0, 0, -1)   // 北
        };
        
        Vector direction = directions[random.nextInt(directions.length)];
        String directionName = getDirectionName(direction);
        
        Location spawnLoc = player.getLocation().clone().add(direction.multiply(3)).add(0, 10, 0);
        
        // 卵を生成
        Egg egg = player.getWorld().spawn(spawnLoc, Egg.class);
        egg.setShooter(player);
        egg.setVelocity(new Vector(0, -0.3, 0)); // 下向きに落下
        
        player.sendMessage(ChatColor.YELLOW + "卵タクシーが" + directionName + "から到着！乗車をお楽しみください！");
        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0f, 1.2f);
        
        // 卵が着地するまで待機してから乗せる
        new BukkitRunnable() {
            int attempts = 0;
            
            @Override
            public void run() {
                attempts++;
                
                if (attempts > 100 || !egg.isValid()) { // 5秒でタイムアウト
                    this.cancel();
                    return;
                }
                
                // 卵が地面に近づいたら乗せる
                if (egg.getLocation().getY() <= player.getLocation().getY() + 2) {
                    Location eggLoc = egg.getLocation();
                    eggLoc.setY(eggLoc.getWorld().getHighestBlockYAt(eggLoc) + 2);
                    
                    player.teleport(eggLoc);
                    
                    // 乗車エフェクト
                    player.getWorld().spawnParticle(
                        Particle.HAPPY_VILLAGER,
                        player.getLocation(),
                        20, 1, 1, 1, 0.1
                    );
                    
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
                    
                    // 卵タクシーの移動（15秒間）
                    startEggTaxiRide(player, eggLoc, direction);
                    
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
        
        return getDescription();
    }
    
    private void startEggTaxiRide(Player player, Location startLoc, Vector baseDirection) {
        new BukkitRunnable() {
            int ticks = 0;
            Location currentLoc = startLoc.clone();
            
            @Override
            public void run() {
                if (ticks >= 300 || !player.isOnline()) { // 15秒間
                    player.sendMessage(ChatColor.GRAY + "卵タクシーの旅が終了しました。");
                    player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 1.0f, 0.8f);
                    this.cancel();
                    return;
                }
                
                // ゆっくりと移動
                Vector movement = baseDirection.clone().multiply(0.1);
                // 少しランダム性を追加
                movement.add(new Vector(
                    (random.nextDouble() - 0.5) * 0.05,
                    (random.nextDouble() - 0.5) * 0.02,
                    (random.nextDouble() - 0.5) * 0.05
                ));
                
                currentLoc.add(movement);
                currentLoc.setY(currentLoc.getWorld().getHighestBlockYAt(currentLoc) + 2);
                
                player.teleport(currentLoc);
                
                // 移動中のパーティクル
                if (ticks % 10 == 0) {
                    player.getWorld().spawnParticle(
                        Particle.CLOUD,
                        currentLoc.add(0, -1, 0),
                        5, 0.5, 0.2, 0.5, 0.02
                    );
                }
                
                ticks++;
            }
        }.runTaskTimer(plugin, 20L, 1L); // 1秒後開始
    }
    
    private String getDirectionName(Vector direction) {
        if (direction.getX() > 0) return "東";
        if (direction.getX() < 0) return "西";
        if (direction.getZ() > 0) return "南";
        if (direction.getZ() < 0) return "北";
        return "上空";
    }
}