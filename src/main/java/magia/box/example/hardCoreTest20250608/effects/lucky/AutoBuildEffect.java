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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class AutoBuildEffect extends LuckyEffectBase {
    
    private final Random random = new Random();
    
    /** 機能ブロックの種類 */
    private static final Material[] FUNCTIONAL_BLOCKS = {
        Material.CRAFTING_TABLE,    // 60% 確率
        Material.ANVIL,            // 20% 確率
        Material.BREWING_STAND,    // 15% 確率
        Material.CHEST,            // 20% 確率（重複可能）
        Material.ENDER_CHEST       // 5% 確率
    };

    public AutoBuildEffect(JavaPlugin plugin) {
        super(plugin, "自動建築", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.GOLD + "🏗 大規模自動建築が開始されます！本格的な建物が建設されます。");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        
        // ランダムな建物タイプを選択
        String[] buildingTypes = {"豪華な邸宅", "長い橋", "高い塔", "深い井戸"};
        String selectedType = buildingTypes[random.nextInt(buildingTypes.length)];
        
        Location startLoc = player.getLocation().clone().add(8, 0, 8);
        
        new BukkitRunnable() {
            int step = 0;
            final int maxSteps = 40; // 建設時間を2倍に
            
            @Override
            public void run() {
                if (step >= maxSteps) {
                    // 機能ブロックを配置
                    placeFunctionalBlocks(startLoc, selectedType);
                    
                    player.sendMessage(ChatColor.GREEN + "🎉 " + selectedType + "の建設が完了しました！");
                    player.sendMessage(ChatColor.AQUA + "✨ 便利な設備も一緒に設置されました！");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
                    
                    // 完成エフェクト
                    startLoc.getWorld().spawnParticle(
                        Particle.FIREWORK,
                        startLoc.clone().add(5, 5, 5),
                        30, 5, 5, 5, 0.2
                    );
                    
                    this.cancel();
                    return;
                }
                
                // 建物の種類に応じてブロックを配置
                buildStructure(startLoc, selectedType, step);
                
                // 建設エフェクト（より豪華に）
                startLoc.getWorld().spawnParticle(
                    Particle.CLOUD,
                    startLoc.clone().add(random.nextInt(12), random.nextInt(8), random.nextInt(12)),
                    8, 1.0, 1.0, 1.0, 0.05
                );
                
                // 定期的な建設音
                if (step % 5 == 0) {
                    player.playSound(startLoc, Sound.BLOCK_WOOD_PLACE, 0.5f, 1.0f + random.nextFloat() * 0.5f);
                }
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, 8L); // 少し早めの間隔
        
        return getDescription() + "（" + selectedType + "）";
    }
    
    private void buildStructure(Location center, String type, int step) {
        switch (type) {
            case "豪華な邸宅":
                buildLargeMansion(center, step);
                break;
            case "長い橋":
                buildLongBridge(center, step);
                break;
            case "高い塔":
                buildHighTower(center, step);
                break;
            case "深い井戸":
                buildDeepWell(center, step);
                break;
        }
    }
    
    /**
     * 豪華な邸宅を建設（10x10の大きな家）
     */
    private void buildLargeMansion(Location center, int step) {
        if (step < 10) {
            // 床を作成（10x10）
            for (int x = 0; x < 10; x++) {
                for (int z = 0; z < 10; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                    if (block.getType() == Material.AIR) {
                        // チェック模様の床
                        Material floorMaterial = ((x + z) % 2 == 0) ? Material.QUARTZ_BLOCK : Material.POLISHED_BLACKSTONE;
                        block.setType(floorMaterial);
                    }
                }
            }
        } else if (step < 30) {
            // 壁を作成（高さ5）
            int y = (step - 10) / 4;
            if (y < 5) {
                for (int x = 0; x < 10; x++) {
                    for (int z = 0; z < 10; z++) {
                        if (x == 0 || x == 9 || z == 0 || z == 9) {
                            Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y + 1, center.getBlockZ() + z);
                            if (x == 4 && z == 0 && y == 0) {
                                block.setType(Material.OAK_DOOR); // 正面玄関
                            } else if (x == 9 && z == 4 && y == 0) {
                                block.setType(Material.OAK_DOOR); // 側面入口
                            } else if ((x == 2 || x == 7) && z == 0 && y == 2) {
                                block.setType(Material.GLASS); // 窓
                            } else if (x == 0 && (z == 2 || z == 7) && y == 2) {
                                block.setType(Material.GLASS); // 側面窓
                            } else {
                                // 石レンガとコンクリートの組み合わせ
                                Material wallMaterial = (y == 0 || y == 4) ? Material.STONE_BRICKS : Material.WHITE_CONCRETE;
                                block.setType(wallMaterial);
                            }
                        }
                    }
                }
            }
        } else {
            // 屋根を作成（段差のある豪華な屋根）
            int roofStep = step - 30;
            for (int x = 0; x < 10; x++) {
                for (int z = 0; z < 10; z++) {
                    if (roofStep < 5) {
                        // 第一層屋根
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 6, center.getBlockZ() + z);
                        block.setType(Material.DARK_OAK_STAIRS);
                    } else {
                        // 煙突と装飾
                        if (x == 8 && z == 8) {
                            for (int chimney = 7; chimney <= 9; chimney++) {
                                Block chimneyBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + chimney, center.getBlockZ() + z);
                                chimneyBlock.setType(Material.BRICKS);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 長い橋を建設（20ブロックの長さ）
     */
    private void buildLongBridge(Location center, int step) {
        int length = Math.min(step, 20);
        for (int x = 0; x <= length; x++) {
            // メインの橋部分（3ブロック幅）
            for (int z = -1; z <= 1; z++) {
                Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                Material bridgeMaterial = (z == 0) ? Material.STONE_BRICKS : Material.COBBLESTONE;
                block.setType(bridgeMaterial);
            }
            
            // 手すりと装飾柱
            if (x > 0 && x < length) {
                // 手すり
                for (int z = -1; z <= 1; z += 2) {
                    Block rail1 = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() + z);
                    Block rail2 = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 2, center.getBlockZ() + z);
                    rail1.setType(Material.STONE_BRICK_WALL);
                    rail2.setType(Material.STONE_BRICK_WALL);
                }
                
                // 装飾柱（4ブロックごと）
                if (x % 4 == 0) {
                    for (int z = -1; z <= 1; z += 2) {
                        Block pillar = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 3, center.getBlockZ() + z);
                        pillar.setType(Material.LANTERN);
                    }
                }
            }
        }
    }
    
    /**
     * 高い塔を建設（16ブロックの高さ）
     */
    private void buildHighTower(Location center, int step) {
        int height = Math.min(step, 16);
        for (int y = 0; y <= height; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    boolean isWall = (x == -2 || x == 2 || z == -2 || z == 2);
                    boolean isCenter = (x == 0 && z == 0);
                    
                    if (isWall || (y == 0)) { // 壁または床
                        Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        
                        if (y == 0) {
                            block.setType(Material.STONE_BRICKS); // 床
                        } else if (y % 4 == 0 && !isCenter) {
                            block.setType(Material.CHISELED_STONE_BRICKS); // 装飾層
                        } else if (isWall) {
                            block.setType(Material.STONE_BRICKS); // 壁
                        }
                        
                        // 入口（地上レベル）
                        if (x == 0 && z == -2 && y == 1) {
                            block.setType(Material.AIR);
                        }
                        
                        // 窓（4階層ごと）
                        if (y % 8 == 4 && isWall && !isCenter) {
                            if ((x == 0 && (z == -2 || z == 2)) || (z == 0 && (x == -2 || x == 2))) {
                                block.setType(Material.GLASS);
                            }
                        }
                    }
                }
            }
            
            // 最上階に装飾的な屋根
            if (y == height && height >= 15) {
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y + 1, center.getBlockZ() + z);
                        roofBlock.setType(Material.STONE_BRICK_STAIRS);
                    }
                }
            }
        }
    }
    
    /**
     * 深い井戸を建設（深さ6ブロック、周囲の構造も大型化）
     */
    private void buildDeepWell(Location center, int step) {
        if (step < 6) {
            // 底を作成（6ブロック深く、3x3）
            int depth = step + 1;
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - depth, center.getBlockZ() + z);
                    block.setType(Material.MOSSY_COBBLESTONE);
                }
            }
        } else if (step < 24) {
            // 壁を作成（深さ6、高さ3の井戸壁）
            int wallStep = step - 6;
            int y = wallStep / 6;
            if (y < 3) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        boolean isWallPosition = (x == -2 || x == 2 || z == -2 || z == 2);
                        boolean isInnerWall = (x == -1 || x == 1 || z == -1 || z == 1) && 
                                             !(x == 0 || z == 0);
                        
                        if (isWallPosition) {
                            // 外壁
                            Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                            block.setType(Material.STONE_BRICKS);
                        } else if (isInnerWall) {
                            // 井戸の内壁（深さ分）
                            for (int depth = 1; depth <= 6; depth++) {
                                Block wallBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - depth + y, center.getBlockZ() + z);
                                wallBlock.setType(Material.COBBLESTONE);
                            }
                        }
                    }
                }
            }
        } else if (step < 30) {
            // 屋根構造を追加
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (x == -2 || x == 2 || z == -2 || z == 2) {
                        Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 4, center.getBlockZ() + z);
                        roofBlock.setType(Material.DARK_OAK_PLANKS);
                    }
                }
            }
            
            // 支柱
            for (int corner = 0; corner < 4; corner++) {
                int x = (corner % 2 == 0) ? -2 : 2;
                int z = (corner < 2) ? -2 : 2;
                for (int pillarY = 1; pillarY <= 3; pillarY++) {
                    Block pillar = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + pillarY, center.getBlockZ() + z);
                    pillar.setType(Material.DARK_OAK_LOG);
                }
            }
        } else {
            // 水を追加
            for (int depth = 1; depth <= 5; depth++) {
                Block water = center.getWorld().getBlockAt(center.getBlockX(), center.getBlockY() - depth, center.getBlockZ());
                water.setType(Material.WATER);
            }
            
            // バケツとロープの装飾
            Block bucket = center.getWorld().getBlockAt(center.getBlockX() + 1, center.getBlockY() + 1, center.getBlockZ());
            bucket.setType(Material.CAULDRON);
        }
    }
    
    /**
     * 機能ブロックを建物内に配置（常に2つ）
     */
    private void placeFunctionalBlocks(Location center, String buildingType) {
        Material firstBlock = selectFunctionalBlock();
        Material secondBlock = selectFunctionalBlock();
        
        switch (buildingType) {
            case "豪華な邸宅":
                // 邸宅内部に2つの機能ブロックを配置
                Block mansionBlock1 = center.getWorld().getBlockAt(center.getBlockX() + 2, center.getBlockY() + 1, center.getBlockZ() + 2);
                mansionBlock1.setType(firstBlock);
                
                Block mansionBlock2 = center.getWorld().getBlockAt(center.getBlockX() + 7, center.getBlockY() + 1, center.getBlockZ() + 7);
                mansionBlock2.setType(secondBlock);
                break;
                
            case "長い橋":
                // 橋の両端に機能ブロック
                Block bridgeBlock1 = center.getWorld().getBlockAt(center.getBlockX() + 5, center.getBlockY() + 1, center.getBlockZ());
                bridgeBlock1.setType(firstBlock);
                
                Block bridgeBlock2 = center.getWorld().getBlockAt(center.getBlockX() + 15, center.getBlockY() + 1, center.getBlockZ());
                bridgeBlock2.setType(secondBlock);
                break;
                
            case "高い塔":
                // 塔の1階と中階に機能ブロック
                Block towerBlock1 = center.getWorld().getBlockAt(center.getBlockX(), center.getBlockY() + 1, center.getBlockZ() + 1);
                towerBlock1.setType(firstBlock);
                
                Block towerBlock2 = center.getWorld().getBlockAt(center.getBlockX() + 1, center.getBlockY() + 8, center.getBlockZ());
                towerBlock2.setType(secondBlock);
                break;
                
            case "深い井戸":
                // 井戸の両側に機能ブロック
                Block wellBlock1 = center.getWorld().getBlockAt(center.getBlockX() + 3, center.getBlockY() + 1, center.getBlockZ());
                wellBlock1.setType(firstBlock);
                
                Block wellBlock2 = center.getWorld().getBlockAt(center.getBlockX() - 3, center.getBlockY() + 1, center.getBlockZ());
                wellBlock2.setType(secondBlock);
                break;
        }
    }
    
    /**
     * 確率に基づいて機能ブロックを選択
     */
    private Material selectFunctionalBlock() {
        double rand = random.nextDouble();
        
        if (rand < 0.20) {
            return Material.ENDER_CHEST;    // 20%
        } else if (rand < 0.35) {
            return Material.BREWING_STAND;  // 15%
        } else if (rand < 0.55) {
            return Material.ANVIL;          // 20%
        } else if (rand < 0.75) {
            return Material.CHEST;          // 20%
        } else {
            return Material.CRAFTING_TABLE; // 25%
        }
    }
}