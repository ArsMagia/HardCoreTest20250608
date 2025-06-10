package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DimensionRiftEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public DimensionRiftEffect(JavaPlugin plugin) {
        super(plugin, "次元の裂け目", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.DARK_PURPLE + "⚠ 次元の裂け目が開きました！");
        player.sendMessage(ChatColor.RED + "🌀 足元に危険なポータルが出現します！");
        player.sendMessage(ChatColor.YELLOW + "⚡ 20秒間、異次元エネルギーが放出されます...");
        
        Location center = player.getLocation();
        List<Block> portalBlocks = new ArrayList<>();
        
        // 初期警告エフェクト
        player.playSound(center, Sound.ENTITY_WITHER_SPAWN, 0.8f, 1.5f);
        center.getWorld().spawnParticle(
            Particle.EXPLOSION,
            center.clone().add(0, 0.5, 0),
            5, 1, 0.5, 1, 0.1
        );
        
        // 足元に小さなポータルを作成
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z);
                if (block.getType() != Material.BEDROCK) {
                    portalBlocks.add(block);
                    block.setType(Material.NETHER_PORTAL);
                }
            }
        }
        
        // ポータル生成音
        player.playSound(center, Sound.BLOCK_PORTAL_AMBIENT, 1.0f, 0.7f);
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 20 || !player.isOnline()) {
                    finishDimensionRift(player, center, portalBlocks);
                    this.cancel();
                    return;
                }
                
                // 継続的な次元エフェクト
                createDimensionEffects(player, center);
                
                // 警告音（5秒ごと）
                if (counter % 5 == 0) {
                    player.playSound(center, Sound.BLOCK_PORTAL_TRIGGER, 0.6f, 1.2f);
                }
                
                // カウントダウン表示（最後5秒）
                if (counter >= 15) {
                    int remaining = 20 - counter;
                    player.sendTitle(
                        "",
                        ChatColor.DARK_PURPLE + "🌀 次元閉鎖まで " + remaining + " 秒",
                        0, 25, 5
                    );
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
    
    /**
     * 次元エフェクトを作成
     */
    private void createDimensionEffects(Player player, Location center) {
        // ポータルパーティクル
        center.getWorld().spawnParticle(
            Particle.PORTAL,
            center,
            15, 2, 1, 2, 0.2
        );
        
        // 異次元エネルギー
        center.getWorld().spawnParticle(
            Particle.END_ROD,
            center.clone().add(0, 2, 0),
            5, 1, 1, 1, 0.1
        );
        
        // ランダムな不気味音
        if (random.nextInt(100) < 15) { // 15%の確率
            Sound[] dimensionSounds = {
                Sound.AMBIENT_CAVE,
                Sound.ENTITY_ENDERMAN_AMBIENT,
                Sound.BLOCK_PORTAL_TRAVEL
            };
            Sound randomSound = dimensionSounds[random.nextInt(dimensionSounds.length)];
            player.playSound(center, randomSound, 0.4f, 0.8f);
        }
        
        // エネルギー放出リング（3秒ごと）
        if (random.nextInt(100) < 33) {
            for (int i = 0; i < 12; i++) {
                double angle = i * Math.PI * 2 / 12;
                Location ringLoc = center.clone().add(
                    Math.cos(angle) * 2,
                    0.5,
                    Math.sin(angle) * 2
                );
                
                center.getWorld().spawnParticle(
                    Particle.WITCH,
                    ringLoc,
                    1, 0, 0, 0, 0
                );
            }
        }
    }
    
    /**
     * 次元の裂け目終了処理
     */
    private void finishDimensionRift(Player player, Location center, List<Block> portalBlocks) {
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "✨ 次元の裂け目が閉じました。");
            player.sendMessage(ChatColor.GRAY + "異次元のエネルギーが消散していきます...");
        }
        
        // 終了エフェクト
        center.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.5f);
        
        // 閉鎖エフェクト
        center.getWorld().spawnParticle(
            Particle.FLASH,
            center.clone().add(0, 1, 0),
            1, 0, 0, 0, 0
        );
        
        center.getWorld().spawnParticle(
            Particle.SMOKE,
            center,
            20, 2, 1, 2, 0.2
        );
        
        // ポータルを削除
        for (Block block : portalBlocks) {
            if (block.getType() == Material.NETHER_PORTAL) {
                block.setType(Material.AIR);
            }
        }
    }
}