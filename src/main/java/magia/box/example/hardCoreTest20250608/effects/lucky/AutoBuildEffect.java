package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AutoBuildEffect extends LuckyEffectBase {
    
    private final Random random = new Random();

    public AutoBuildEffect(JavaPlugin plugin) {
        super(plugin, "自動建築", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.GOLD + "自動建築が開始されます！簡単な建物が建設されます。");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        
        // ランダムな建物タイプを選択
        String[] buildingTypes = {"小さな家", "橋", "塔", "井戸"};
        String selectedType = buildingTypes[random.nextInt(buildingTypes.length)];
        
        Location startLoc = player.getLocation().clone().add(5, 0, 5);
        
        new BukkitRunnable() {
            int step = 0;
            
            @Override
            public void run() {
                if (step >= 20) {
                    player.sendMessage(ChatColor.GREEN + selectedType + "の建設が完了しました！");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
                    this.cancel();
                    return;
                }
                
                // 建物の種類に応じてブロックを配置
                buildStructure(startLoc, selectedType, step);
                
                startLoc.getWorld().spawnParticle(
                    org.bukkit.Particle.CLOUD,
                    startLoc.clone().add(random.nextInt(6), random.nextInt(4), random.nextInt(6)),
                    5, 0.5, 0.5, 0.5, 0.02
                );
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, 10L);
        
        return getDescription() + "（" + selectedType + "）";
    }
    
    private void buildStructure(Location center, String type, int step) {
        switch (type) {
            case "小さな家":
                buildHouse(center, step);
                break;
            case "橋":
                buildBridge(center, step);
                break;
            case "塔":
                buildTower(center, step);
                break;
            case "井戸":
                buildWell(center, step);
                break;
        }
    }
    
    private void buildHouse(Location center, int step) {
        if (step < 5) {
            // 床を作成
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                    if (block.getType() == Material.AIR) {
                        block.setType(Material.OAK_PLANKS);
                    }
                }
            }
        } else if (step < 15) {
            // 壁を作成
            int y = step - 5;
            if (y < 3) {
                for (int x = 0; x < 5; x++) {
                    for (int z = 0; z < 5; z++) {
                        if (x == 0 || x == 4 || z == 0 || z == 4) {
                            Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y + 1, center.getBlockZ() + z);
                            if (x == 2 && z == 0 && y == 0) {
                                block.setType(Material.OAK_DOOR); // ドア
                            } else {
                                block.setType(Material.COBBLESTONE);
                            }
                        }
                    }
                }
            }
        } else {
            // 屋根を作成
            for (int x = 0; x < 5; x++) {
                for (int z = 0; z < 5; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 4, center.getBlockZ() + z);
                    block.setType(Material.OAK_STAIRS);
                }
            }
        }
    }
    
    private void buildBridge(Location center, int step) {
        int length = Math.min(step, 10);
        for (int x = 0; x <= length; x++) {
            Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ());
            block.setType(Material.OAK_PLANKS);
            
            // 手すり
            if (x > 0 && x < length) {
                Block rail1 = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() + 1);
                Block rail2 = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() - 1);
                rail1.setType(Material.OAK_FENCE);
                rail2.setType(Material.OAK_FENCE);
            }
        }
    }
    
    private void buildTower(Location center, int step) {
        int height = Math.min(step, 8);
        for (int y = 0; y <= height; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && z == 0) continue; // 中央は空洞
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                    block.setType(Material.STONE_BRICKS);
                }
            }
        }
    }
    
    private void buildWell(Location center, int step) {
        if (step < 3) {
            // 底を作成
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - 2, center.getBlockZ() + z);
                    block.setType(Material.COBBLESTONE);
                }
            }
        } else if (step < 8) {
            // 壁を作成
            int y = step - 3;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == -1 || x == 1 || z == -1 || z == 1) {
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - 2 + y, center.getBlockZ() + z);
                        block.setType(Material.COBBLESTONE);
                    }
                }
            }
        } else {
            // 水を追加
            Block water = center.getWorld().getBlockAt(center.getBlockX(), center.getBlockY() - 1, center.getBlockZ());
            water.setType(Material.WATER);
        }
    }
}