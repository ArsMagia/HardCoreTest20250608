package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlameFieldEffect extends UnluckyEffectBase {

    private final Random random = new Random();

    public FlameFieldEffect(JavaPlugin plugin) {
        super(plugin, "炎上地帯", EffectRarity.UNCOMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_RED + "周囲が炎に包まれます！10秒間、ブロックが燃え続けます！");
        player.playSound(player.getLocation(), Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);

        Location center = player.getLocation();
        List<Location> fireLocations = new ArrayList<>();

        // 初期の炎を設置
        createInitialFires(center, fireLocations);

        // 10秒間、炎を維持・拡散
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 200 || !player.isOnline()) { // 10秒間
                    // 炎を消去
                    extinguishFires(fireLocations);
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GRAY + "炎が消えていきました...");
                    }
                    this.cancel();
                    return;
                }

                // 2秒ごとに新しい炎を追加（緩やかな拡散）
                if (ticks % 40 == 0) {
                    addRandomFires(center, fireLocations);
                }

                // パーティクルエフェクト
                if (ticks % 10 == 0) {
                    for (Location loc : fireLocations) {
                        if (loc.getWorld() != null) {
                            loc.getWorld().spawnParticle(
                                Particle.FLAME,
                                loc.clone().add(0.5, 1, 0.5),
                                3, 0.3, 0.3, 0.3, 0.02
                            );
                        }
                    }
                }

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        return getDescription();
    }

    private void createInitialFires(Location center, List<Location> fireLocations) {
        World world = center.getWorld();
        if (world == null) return;

        // 中心から半径5ブロック以内にランダムに炎を配置
        for (int i = 0; i < 15; i++) {
            int x = random.nextInt(11) - 5; // -5 to 5
            int z = random.nextInt(11) - 5; // -5 to 5
            
            Location fireLocation = center.clone().add(x, 0, z);
            
            // 地面を探す
            fireLocation.setY(world.getHighestBlockYAt(fireLocation) + 1);
            
            // 足元は避ける（中心から2ブロック以内は確率を下げる）
            double distance = fireLocation.distance(center);
            if (distance < 2.0 && random.nextDouble() < 0.7) {
                continue; // 70%の確率でスキップ
            }
            
            Block block = world.getBlockAt(fireLocation);
            if (block.getType() == Material.AIR) {
                block.setType(Material.FIRE);
                fireLocations.add(fireLocation);
            }
        }

        // 音響効果
        world.playSound(center, Sound.ITEM_FLINTANDSTEEL_USE, 1.0f, 1.0f);
    }

    private void addRandomFires(Location center, List<Location> fireLocations) {
        World world = center.getWorld();
        if (world == null) return;

        // 既存の炎の周りに新しい炎を追加（緩やかな拡散）
        if (!fireLocations.isEmpty() && fireLocations.size() < 30) {
            Location existingFire = fireLocations.get(random.nextInt(fireLocations.size()));
            
            for (int i = 0; i < 3; i++) {
                int x = random.nextInt(3) - 1; // -1 to 1
                int z = random.nextInt(3) - 1; // -1 to 1
                
                if (x == 0 && z == 0) continue;
                
                Location newFireLocation = existingFire.clone().add(x, 0, z);
                newFireLocation.setY(world.getHighestBlockYAt(newFireLocation) + 1);
                
                // 中心から離れすぎないように制限
                if (newFireLocation.distance(center) > 8) continue;
                
                Block block = world.getBlockAt(newFireLocation);
                if (block.getType() == Material.AIR) {
                    block.setType(Material.FIRE);
                    fireLocations.add(newFireLocation);
                    
                    // 炎の音
                    world.playSound(newFireLocation, Sound.BLOCK_FIRE_AMBIENT, 0.5f, 1.0f);
                    break; // 1つだけ追加
                }
            }
        }
    }

    private void extinguishFires(List<Location> fireLocations) {
        for (Location loc : fireLocations) {
            World world = loc.getWorld();
            if (world != null) {
                Block block = world.getBlockAt(loc);
                if (block.getType() == Material.FIRE) {
                    block.setType(Material.AIR);
                    
                    // 消火エフェクト
                    world.spawnParticle(
                        Particle.SMOKE,
                        loc.clone().add(0.5, 0.5, 0.5),
                        5, 0.2, 0.2, 0.2, 0.02
                    );
                }
            }
        }
        
        if (!fireLocations.isEmpty() && fireLocations.get(0).getWorld() != null) {
            fireLocations.get(0).getWorld().playSound(
                fireLocations.get(0), Sound.BLOCK_FIRE_EXTINGUISH, 1.0f, 1.0f
            );
        }
    }
}