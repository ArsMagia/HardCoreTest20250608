package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CloudBridgeEffect extends LuckyEffectBase {

    /** 雲橋の長さ */
    private static final int BRIDGE_LENGTH = 50;
    
    /** 雲橋の高度（プレイヤーより上） */
    private static final int BRIDGE_HEIGHT = 15;
    
    /** 持続時間（5分） */
    private static final int DURATION_SECONDS = 300;

    public CloudBridgeEffect(JavaPlugin plugin) {
        super(plugin, "スカイロード", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location playerLoc = player.getLocation();
        Location start = playerLoc.clone().add(0, BRIDGE_HEIGHT, 0);
        List<Block> cloudBlocks = new ArrayList<>();
        
        Vector direction = playerLoc.getDirection().normalize();
        direction.setY(0); // 水平方向のみ
        
        // 雲橋を生成（自動カーブ機能付き）
        Location bridgeStart = null;
        for (int i = 0; i <= BRIDGE_LENGTH; i++) {
            // 緩やかなカーブを作成（プレイヤーの視線方向に基づく）
            double curve = Math.sin(i * Math.PI / BRIDGE_LENGTH) * 0.1;
            Vector perpendicular = direction.clone().rotateAroundY(Math.PI / 2);
            
            Location bridgeLoc = start.clone()
                .add(direction.clone().multiply(i))
                .add(perpendicular.multiply(curve * i));
            
            // 5x5の雲プラットフォームを生成
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    Block block = bridgeLoc.clone().add(x, 0, z).getBlock();
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.WHITE_WOOL);
                        cloudBlocks.add(block);
                        
                        // 雲エフェクト
                        block.getLocation().getWorld().spawnParticle(
                            Particle.CLOUD,
                            block.getLocation().add(0.5, 1, 0.5),
                            3, 0.2, 0.2, 0.2, 0.02
                        );
                    }
                }
            }
            
            // 最初の安全な地点を記録
            if (i == 0 && bridgeStart == null) {
                bridgeStart = bridgeLoc.clone();
            }
        }
        
        // プレイヤーを安全な雲橋開始地点にTPする
        if (bridgeStart != null && isSafeLocation(bridgeStart.add(0, 1, 0))) {
            player.teleport(bridgeStart.add(0, 1, 0));
            player.sendMessage(ChatColor.AQUA + "🌤 スカイロードの始点にテレポートしました！");
            
            // テレポートエフェクト
            player.getWorld().spawnParticle(
                Particle.PORTAL,
                player.getLocation(),
                20, 0.5, 0.5, 0.5, 0.3
            );
        }
        
        // プレイヤーに移動速度上昇効果のみ付与
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, DURATION_SECONDS * 20, 0));
        
        player.sendMessage(ChatColor.WHITE + "✨ スカイロードが空に現れました！" + (DURATION_SECONDS / 60) + "分間持続します。");
        player.sendMessage(ChatColor.GRAY + "⚡ 移動速度上昇効果付与！");
        player.playSound(start, Sound.ENTITY_GHAST_AMBIENT, 1.0f, 1.5f);
        player.playSound(start, Sound.BLOCK_PORTAL_AMBIENT, 0.5f, 1.8f);
        
        // 上空に壮大なエフェクト
        start.getWorld().spawnParticle(
            Particle.END_ROD,
            start.add(0, 5, 0),
            50, BRIDGE_LENGTH / 4, 3, 5, 0.1
        );
        
        // 5分後に雲橋を消去
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : cloudBlocks) {
                    if (block.getType() == Material.WHITE_WOOL) {
                        block.setType(Material.AIR);
                        block.getLocation().getWorld().spawnParticle(
                            Particle.CLOUD,
                            block.getLocation().add(0.5, 0.5, 0.5),
                            8, 0.3, 0.3, 0.3, 0.05
                        );
                    }
                }
                
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "🌫 スカイロードが消散しました。");
                    player.playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.5f, 1.2f);
                }
            }
        }.runTaskLater(plugin, DURATION_SECONDS * 20L);
        
        return getDescription() + " (" + BRIDGE_LENGTH + "ブロック)";
    }
    
    /**
     * 安全な場所かどうかをチェック
     * @param location チェックする場所
     * @return 安全かどうか
     */
    private boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        Block head = location.clone().add(0, 1, 0).getBlock();
        
        // 足元と頭上が空気ブロックで、その下に雲ブロックがある
        return feet.getType() == Material.AIR && 
               head.getType() == Material.AIR && 
               feet.getRelative(0, -1, 0).getType() == Material.WHITE_WOOL;
    }
}