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

public class BuildingGodEffect extends LuckyEffectBase {
    
    private final Random random = new Random();

    public BuildingGodEffect(JavaPlugin plugin) {
        super(plugin, "建築の神", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.MAGIC + "aa" + ChatColor.RESET + ChatColor.GOLD + " 建築の神が降臨しました！ " + ChatColor.MAGIC + "aa");
        player.sendMessage(ChatColor.YELLOW + "🏰 神々しい建築が始まります...感動の建造物をお楽しみください！");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 0.5f);
        
        Location startLoc = player.getLocation().clone().add(6, 0, 6);
        
        // 事前整地を段階化して開始
        startStagedGroundPreparation(startLoc, player);
        
        return getDescription();
    }
    
    /**
     * 段階化された地面整地を開始
     */
    private void startStagedGroundPreparation(Location startLoc, Player player) {
        new BukkitRunnable() {
            int currentX = 0;
            int currentZ = 0;
            int blocksProcessedThisTick = 0;
            final int MAX_BLOCKS_PER_TICK = 20; // 1ティックあたり最大20ブロック
            
            @Override
            public void run() {
                blocksProcessedThisTick = 0;
                
                while (currentX < 15 && blocksProcessedThisTick < MAX_BLOCKS_PER_TICK) {
                    // 地面ブロックの設置
                    Block groundBlock = startLoc.getWorld().getBlockAt(
                        startLoc.getBlockX() + currentX, 
                        startLoc.getBlockY() - 1, 
                        startLoc.getBlockZ() + currentZ
                    );
                    if (groundBlock.getType() == Material.AIR) {
                        groundBlock.setType(Material.GRASS_BLOCK);
                    }
                    
                    // 上の空間をクリア（必要な場合のみ）
                    for (int y = 0; y < 16 && blocksProcessedThisTick < MAX_BLOCKS_PER_TICK; y++) {
                        Block airBlock = startLoc.getWorld().getBlockAt(
                            startLoc.getBlockX() + currentX, 
                            startLoc.getBlockY() + y, 
                            startLoc.getBlockZ() + currentZ
                        );
                        // AIRでない場合のみ変更（パフォーマンス最適化）
                        if (!airBlock.getType().equals(Material.AIR)) {
                            airBlock.setType(Material.AIR);
                            blocksProcessedThisTick++;
                        }
                    }
                    
                    blocksProcessedThisTick++; // 地面ブロックもカウント
                    
                    // 次の位置へ
                    currentZ++;
                    if (currentZ >= 15) {
                        currentZ = 0;
                        currentX++;
                    }
                }
                
                // 整地完了チェック
                if (currentX >= 15) {
                    player.sendMessage(ChatColor.GREEN + "🌱 地面の整地が完了しました！建築を開始します...");
                    this.cancel();
                    startMainBuilding(startLoc, player);
                }
            }
        }.runTaskTimer(plugin, 0L, 2L); // 2ティック間隔で整地
    }
    
    /**
     * メイン建築プロセスを開始
     */
    private void startMainBuilding(Location startLoc, Player player) {
        new BukkitRunnable() {
            int step = 0;
            final int maxSteps = 80;
            
            @Override
            public void run() {
                if (step >= maxSteps) {
                    // 必須アイテムの配置
                    placeRequiredItems(startLoc);
                    
                    player.sendMessage(ChatColor.MAGIC + "bb" + ChatColor.RESET + 
                            ChatColor.GOLD + " 神の建築が完成しました！ " + ChatColor.MAGIC + "bb");
                    player.sendMessage(ChatColor.AQUA + "✨ エンチャントテーブル、本棚、作業台が設置されています！");
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.5f);
                    
                    // 完成時の豪華エフェクト
                    for (int i = 0; i < 50; i++) {
                        startLoc.getWorld().spawnParticle(
                            Particle.FIREWORK,
                            startLoc.clone().add(random.nextInt(12), random.nextInt(16), random.nextInt(12)),
                            5, 2, 2, 2, 0.3
                        );
                    }
                    
                    this.cancel();
                    return;
                }
                
                // 神の建築を段階的に構築（制限付き）
                buildDivineMansionLimited(startLoc, step);
                
                // 建築中の神々しいエフェクト（軽量化）
                if (step % 6 == 0) { // エフェクト頻度を半減
                    startLoc.getWorld().spawnParticle(
                        Particle.ENCHANT,
                        startLoc.clone().add(random.nextInt(12), random.nextInt(8), random.nextInt(12)),
                        2, 0.5, 0.5, 0.5, 0.1 // パーティクル数を削減
                    );
                }
                
                if (step % 4 == 0) { // エフェクト頻度を半減
                    startLoc.getWorld().spawnParticle(
                        Particle.TOTEM_OF_UNDYING,
                        startLoc.clone().add(6, 8, 6),
                        1, 3, 3, 3, 0.05
                    );
                }
                
                // 定期的な神聖音
                if (step % 10 == 0) {
                    player.playSound(startLoc, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.2f);
                }
                if (step % 8 == 0) {
                    player.playSound(startLoc, Sound.BLOCK_WOOD_PLACE, 0.4f, 1.0f + random.nextFloat() * 0.3f);
                }
                
                step++;
            }
        }.runTaskTimer(plugin, 0L, 12L); // 6ティックから12ティックに変更
    }
    
    /**
     * 制限付きの神の建築メソッド（パフォーマンス最適化版）
     */
    private void buildDivineMansionLimited(Location center, int step) {
        // 1ティックあたりの建築ブロック数を制限
        int blocksBuilt = 0;
        final int MAX_BLOCKS_PER_BUILD_TICK = 15; // 建築時の制限
        
        if (step < 15) {
            // 基礎と床の建設（制限付き）
            blocksBuilt += buildFoundationAndFloorLimited(center, step, MAX_BLOCKS_PER_BUILD_TICK);
        } else if (step < 50) {
            // メイン建物の建設（制限付き）
            int buildStep = step - 15;
            blocksBuilt += buildMainBuildingLimited(center, buildStep, MAX_BLOCKS_PER_BUILD_TICK);
        } else if (step < 65) {
            // サブ建物の建設（制限付き）
            int subStep = step - 50;
            blocksBuilt += buildSubBuildingsLimited(center, subStep, MAX_BLOCKS_PER_BUILD_TICK);
        } else {
            // 内装と仕上げ（制限付き）
            int decorStep = step - 65;
            blocksBuilt += addInteriorFittingsLimited(center, decorStep, MAX_BLOCKS_PER_BUILD_TICK);
        }
    }
    
    /**
     * 制限付き基礎建設
     */
    private int buildFoundationAndFloorLimited(Location center, int step, int maxBlocks) {
        int blocksBuilt = 0;
        for (int x = 0; x < 15 && blocksBuilt < maxBlocks; x++) {
            for (int z = 0; z < 15 && blocksBuilt < maxBlocks; z++) {
                // エリア分けされた床材
                Material floorMaterial = getAreaFloorMaterial(x, z);
                
                Block floorBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
                // 既に同じ材質の場合はスキップ（最適化）
                if (!floorBlock.getType().equals(floorMaterial)) {
                    floorBlock.setType(floorMaterial);
                    blocksBuilt++;
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付きメイン建物建設
     */
    private int buildMainBuildingLimited(Location center, int buildStep, int maxBlocks) {
        int blocksBuilt = 0;
        if (buildStep < 15) {
            // メインホールの壁（1階）
            blocksBuilt += buildMainHallWallsLimited(center, 1, maxBlocks);
        } else if (buildStep < 25) {
            // メインホールの壁（2階）
            blocksBuilt += buildMainHallWallsLimited(center, 2, maxBlocks);
        } else if (buildStep < 35) {
            // 各専用室の建設
            blocksBuilt += buildSpecializedRoomsLimited(center, buildStep - 25, maxBlocks);
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付きサブ建物建設
     */
    private int buildSubBuildingsLimited(Location center, int subStep, int maxBlocks) {
        int blocksBuilt = 0;
        if (subStep < 8) {
            // 屋根の建設
            blocksBuilt += buildRoofsLimited(center, maxBlocks);
        } else {
            // 通路とアクセントの建設
            blocksBuilt += buildConnectingPathsLimited(center, maxBlocks);
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付き内装追加
     */
    private int addInteriorFittingsLimited(Location center, int decorStep, int maxBlocks) {
        int blocksBuilt = 0;
        if (decorStep < 8) {
            // 照明と装飾
            blocksBuilt += addLightingAndDecorLimited(center, maxBlocks);
        } else {
            // 家具と実用品
            blocksBuilt += addFurnitureAndUtilitiesLimited(center, maxBlocks);
        }
        return blocksBuilt;
    }
    
    
    /**
     * エリアに応じた床材を取得
     */
    private Material getAreaFloorMaterial(int x, int z) {
        // メインホール（中央）
        if (x >= 4 && x <= 10 && z >= 4 && z <= 10) {
            return (x + z) % 2 == 0 ? Material.POLISHED_GRANITE : Material.POLISHED_DIORITE;
        }
        // エンチャント室（左上）
        else if (x <= 4 && z <= 4) {
            return Material.POLISHED_DEEPSLATE;
        }
        // 作業室（右上）
        else if (x >= 10 && z <= 4) {
            return Material.POLISHED_ANDESITE;
        }
        // 倉庫エリア（下部）
        else if (z >= 10) {
            return Material.STONE_BRICKS;
        }
        // 通路
        else {
            return Material.STONE;
        }
    }
    
    
    /**
     * 制限付きメインホールの壁を建設
     */
    private int buildMainHallWallsLimited(Location center, int floor, int maxBlocks) {
        int y = floor * 4 - 3; // 1階=1, 2階=5
        int blocksBuilt = 0;
        
        for (int x = 4; x <= 10 && blocksBuilt < maxBlocks; x++) {
            for (int z = 4; z <= 10 && blocksBuilt < maxBlocks; z++) {
                boolean isWall = (x == 4 || x == 10 || z == 4 || z == 10);
                
                if (isWall) {
                    for (int wallY = 0; wallY < 4 && blocksBuilt < maxBlocks; wallY++) {
                        Block wallBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y + wallY, center.getBlockZ() + z);
                        
                        // 入口（1階正面）
                        if (x == 7 && z == 4 && wallY <= 1 && floor == 1) {
                            // AIRでない場合のみ変更
                            if (!wallBlock.getType().equals(Material.AIR)) {
                                wallBlock.setType(Material.AIR);
                                blocksBuilt++;
                            }
                            continue;
                        }
                        
                        // 窓の配置
                        if (wallY == 2 && isMainHallWindow(x, z, floor)) {
                            // ガラスでない場合のみ変更
                            if (!wallBlock.getType().equals(Material.GLASS)) {
                                wallBlock.setType(Material.GLASS);
                                blocksBuilt++;
                            }
                            continue;
                        }
                        
                        // 壁の材質
                        Material wallMaterial = getMainHallWallMaterial(floor, wallY);
                        if (!wallBlock.getType().equals(wallMaterial)) {
                            wallBlock.setType(wallMaterial);
                            blocksBuilt++;
                        }
                    }
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付き専用室を建設
     */
    private int buildSpecializedRoomsLimited(Location center, int roomStep, int maxBlocks) {
        if (roomStep < 3) {
            // エンチャント室
            return buildEnchantmentRoomLimited(center, maxBlocks);
        } else if (roomStep < 6) {
            // 作業室
            return buildWorkshopRoomLimited(center, maxBlocks);
        } else {
            // 倉庫エリア
            return buildStorageAreaLimited(center, maxBlocks);
        }
    }
    
    /**
     * 制限付きエンチャント室を建設
     */
    private int buildEnchantmentRoomLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        for (int x = 0; x <= 4 && blocksBuilt < maxBlocks; x++) {
            for (int z = 0; z <= 4 && blocksBuilt < maxBlocks; z++) {
                boolean isWall = (x == 0 || x == 4 || z == 0 || z == 4);
                
                if (isWall) {
                    for (int y = 1; y <= 4 && blocksBuilt < maxBlocks; y++) {
                        Block wallBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        
                        // 入口
                        if (x == 4 && z == 2 && y <= 2) {
                            if (!wallBlock.getType().equals(Material.AIR)) {
                                wallBlock.setType(Material.AIR);
                                blocksBuilt++;
                            }
                            continue;
                        }
                        
                        if (!wallBlock.getType().equals(Material.DEEPSLATE_BRICKS)) {
                            wallBlock.setType(Material.DEEPSLATE_BRICKS);
                            blocksBuilt++;
                        }
                    }
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付き作業室を建設
     */
    private int buildWorkshopRoomLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        for (int x = 10; x <= 14 && blocksBuilt < maxBlocks; x++) {
            for (int z = 0; z <= 4 && blocksBuilt < maxBlocks; z++) {
                boolean isWall = (x == 10 || x == 14 || z == 0 || z == 4);
                
                if (isWall) {
                    for (int y = 1; y <= 4 && blocksBuilt < maxBlocks; y++) {
                        Block wallBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        
                        // 入口
                        if (x == 10 && z == 2 && y <= 2) {
                            if (!wallBlock.getType().equals(Material.AIR)) {
                                wallBlock.setType(Material.AIR);
                                blocksBuilt++;
                            }
                            continue;
                        }
                        
                        if (!wallBlock.getType().equals(Material.POLISHED_ANDESITE)) {
                            wallBlock.setType(Material.POLISHED_ANDESITE);
                            blocksBuilt++;
                        }
                    }
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付き倉庫エリアを建設
     */
    private int buildStorageAreaLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        for (int x = 2; x <= 12 && blocksBuilt < maxBlocks; x++) {
            for (int z = 10; z <= 14 && blocksBuilt < maxBlocks; z++) {
                boolean isWall = (x == 2 || x == 12 || z == 10 || z == 14);
                
                if (isWall) {
                    for (int y = 1; y <= 3 && blocksBuilt < maxBlocks; y++) {
                        Block wallBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + y, center.getBlockZ() + z);
                        
                        // 入口
                        if (x == 7 && z == 10 && y <= 2) {
                            if (!wallBlock.getType().equals(Material.AIR)) {
                                wallBlock.setType(Material.AIR);
                                blocksBuilt++;
                            }
                            continue;
                        }
                        
                        if (!wallBlock.getType().equals(Material.BRICKS)) {
                            wallBlock.setType(Material.BRICKS);
                            blocksBuilt++;
                        }
                    }
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * メインホールの窓位置を判定
     */
    private boolean isMainHallWindow(int x, int z, int floor) {
        if (floor == 1) {
            return (x == 4 && (z == 6 || z == 8)) || 
                   (x == 10 && (z == 6 || z == 8)) ||
                   (z == 10 && (x == 6 || x == 8));
        } else if (floor == 2) {
            return (x == 4 && z == 7) || 
                   (x == 10 && z == 7) ||
                   (z == 4 && x == 7) ||
                   (z == 10 && x == 7);
        }
        return false;
    }
    
    /**
     * メインホールの壁材質を取得
     */
    private Material getMainHallWallMaterial(int floor, int wallY) {
        if (floor == 1) {
            return wallY == 0 ? Material.GRANITE : 
                   wallY == 3 ? Material.POLISHED_GRANITE : Material.DIORITE;
        } else {
            return wallY == 0 ? Material.POLISHED_DIORITE : 
                   wallY == 3 ? Material.GRANITE : Material.DIORITE;
        }
    }
    
    
    /**
     * 制限付き屋根を建設
     */
    private int buildRoofsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        // メインホールの屋根
        blocksBuilt += buildMainHallRoofLimited(center, maxBlocks - blocksBuilt);
        if (blocksBuilt < maxBlocks) {
            // 各部屋の屋根
            blocksBuilt += buildRoomRoofsLimited(center, maxBlocks - blocksBuilt);
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付きメインホールの屋根
     */
    private int buildMainHallRoofLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        for (int x = 4; x <= 10 && blocksBuilt < maxBlocks; x++) {
            for (int z = 4; z <= 10 && blocksBuilt < maxBlocks; z++) {
                Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 9, center.getBlockZ() + z);
                Material targetMaterial = (x == 4 || x == 10 || z == 4 || z == 10) ? 
                    Material.DARK_OAK_PLANKS : Material.OAK_PLANKS;
                if (!roofBlock.getType().equals(targetMaterial)) {
                    roofBlock.setType(targetMaterial);
                    blocksBuilt++;
                }
            }
        }
        return blocksBuilt;
    }
    
    /**
     * 制限付き各部屋の屋根
     */
    private int buildRoomRoofsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // エンチャント室の屋根
        for (int x = 0; x <= 4 && blocksBuilt < maxBlocks; x++) {
            for (int z = 0; z <= 4 && blocksBuilt < maxBlocks; z++) {
                Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 5, center.getBlockZ() + z);
                if (!roofBlock.getType().equals(Material.DEEPSLATE_TILES)) {
                    roofBlock.setType(Material.DEEPSLATE_TILES);
                    blocksBuilt++;
                }
            }
        }
        
        // 作業室の屋根
        for (int x = 10; x <= 14 && blocksBuilt < maxBlocks; x++) {
            for (int z = 0; z <= 4 && blocksBuilt < maxBlocks; z++) {
                Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 5, center.getBlockZ() + z);
                if (!roofBlock.getType().equals(Material.POLISHED_ANDESITE)) {
                    roofBlock.setType(Material.POLISHED_ANDESITE);
                    blocksBuilt++;
                }
            }
        }
        
        // 倉庫の屋根
        for (int x = 2; x <= 12 && blocksBuilt < maxBlocks; x++) {
            for (int z = 10; z <= 14 && blocksBuilt < maxBlocks; z++) {
                Block roofBlock = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 4, center.getBlockZ() + z);
                if (!roofBlock.getType().equals(Material.BRICK_STAIRS)) {
                    roofBlock.setType(Material.BRICK_STAIRS);
                    blocksBuilt++;
                }
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き通路を建設
     */
    private int buildConnectingPathsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        // 中央通路の装飾柱
        for (int x = 5; x <= 9 && blocksBuilt < maxBlocks; x += 2) {
            Block pillar = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() + 3);
            if (!pillar.getType().equals(Material.STONE_BRICK_WALL)) {
                pillar.setType(Material.STONE_BRICK_WALL);
                blocksBuilt++;
            }
            if (blocksBuilt < maxBlocks) {
                Block pillarTop = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 2, center.getBlockZ() + 3);
                if (!pillarTop.getType().equals(Material.STONE_BRICK_WALL)) {
                    pillarTop.setType(Material.STONE_BRICK_WALL);
                    blocksBuilt++;
                }
            }
        }
        return blocksBuilt;
    }
    
    
    /**
     * 制限付き照明と装飾を追加
     */
    private int addLightingAndDecorLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // メインホールの照明
        Block centerLight = center.getWorld().getBlockAt(center.getBlockX() + 7, center.getBlockY() + 6, center.getBlockZ() + 7);
        if (!centerLight.getType().equals(Material.GLOWSTONE)) {
            centerLight.setType(Material.GLOWSTONE);
            blocksBuilt++;
        }
        
        // 各部屋の照明
        if (blocksBuilt < maxBlocks) {
            blocksBuilt += addRoomLightingLimited(center, maxBlocks - blocksBuilt);
        }
        
        // 装飾要素
        if (blocksBuilt < maxBlocks) {
            blocksBuilt += addDecorativeElementsLimited(center, maxBlocks - blocksBuilt);
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き各部屋の照明
     */
    private int addRoomLightingLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // エンチャント室
        if (blocksBuilt < maxBlocks) {
            Block enchantLight = center.getWorld().getBlockAt(center.getBlockX() + 2, center.getBlockY() + 3, center.getBlockZ() + 2);
            if (!enchantLight.getType().equals(Material.GLOWSTONE)) {
                enchantLight.setType(Material.GLOWSTONE);
                blocksBuilt++;
            }
        }
        
        // 作業室
        if (blocksBuilt < maxBlocks) {
            Block workshopLight = center.getWorld().getBlockAt(center.getBlockX() + 12, center.getBlockY() + 3, center.getBlockZ() + 2);
            if (!workshopLight.getType().equals(Material.GLOWSTONE)) {
                workshopLight.setType(Material.GLOWSTONE);
                blocksBuilt++;
            }
        }
        
        // 倉庫
        if (blocksBuilt < maxBlocks) {
            Block storageLight = center.getWorld().getBlockAt(center.getBlockX() + 7, center.getBlockY() + 2, center.getBlockZ() + 12);
            if (!storageLight.getType().equals(Material.GLOWSTONE)) {
                storageLight.setType(Material.GLOWSTONE);
                blocksBuilt++;
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き装飾要素を追加
     */
    private int addDecorativeElementsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // メインホールのカーペット
        for (int x = 5; x <= 9 && blocksBuilt < maxBlocks; x++) {
            for (int z = 5; z <= 9 && blocksBuilt < maxBlocks; z++) {
                if ((x + z) % 2 == 0) {
                    Block carpet = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() + z);
                    Material[] carpets = {Material.RED_CARPET, Material.BLUE_CARPET, Material.GREEN_CARPET};
                    Material targetCarpet = carpets[random.nextInt(carpets.length)];
                    if (!carpet.getType().equals(targetCarpet)) {
                        carpet.setType(targetCarpet);
                        blocksBuilt++;
                    }
                }
            }
        }
        
        // 植物の装飾
        if (blocksBuilt < maxBlocks) {
            Block plant1 = center.getWorld().getBlockAt(center.getBlockX() + 5, center.getBlockY() + 1, center.getBlockZ() + 5);
            if (!plant1.getType().equals(Material.POTTED_FERN)) {
                plant1.setType(Material.POTTED_FERN);
                blocksBuilt++;
            }
        }
        
        if (blocksBuilt < maxBlocks) {
            Block plant2 = center.getWorld().getBlockAt(center.getBlockX() + 9, center.getBlockY() + 1, center.getBlockZ() + 9);
            if (!plant2.getType().equals(Material.POTTED_AZALEA_BUSH)) {
                plant2.setType(Material.POTTED_AZALEA_BUSH);
                blocksBuilt++;
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き家具と実用品を追加
     */
    private int addFurnitureAndUtilitiesLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // 各部屋にテーブルとチェア
        blocksBuilt += addRoomFurnitureLimited(center, maxBlocks - blocksBuilt);
        
        // 倉庫にチェスト
        if (blocksBuilt < maxBlocks) {
            blocksBuilt += addStorageChestsLimited(center, maxBlocks - blocksBuilt);
        }
        
        // その他の実用品
        if (blocksBuilt < maxBlocks) {
            blocksBuilt += addUtilityItemsLimited(center, maxBlocks - blocksBuilt);
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き各部屋の家具
     */
    private int addRoomFurnitureLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // エンチャント室のテーブル
        if (blocksBuilt < maxBlocks) {
            Block enchantDesk = center.getWorld().getBlockAt(center.getBlockX() + 1, center.getBlockY() + 1, center.getBlockZ() + 3);
            if (!enchantDesk.getType().equals(Material.OAK_SLAB)) {
                enchantDesk.setType(Material.OAK_SLAB);
                blocksBuilt++;
            }
        }
        
        // 作業室のテーブル
        if (blocksBuilt < maxBlocks) {
            Block workDesk = center.getWorld().getBlockAt(center.getBlockX() + 13, center.getBlockY() + 1, center.getBlockZ() + 1);
            if (!workDesk.getType().equals(Material.SPRUCE_SLAB)) {
                workDesk.setType(Material.SPRUCE_SLAB);
                blocksBuilt++;
            }
        }
        
        // チェア（階段ブロック）
        if (blocksBuilt < maxBlocks) {
            Block chair1 = center.getWorld().getBlockAt(center.getBlockX() + 1, center.getBlockY() + 1, center.getBlockZ() + 2);
            if (!chair1.getType().equals(Material.OAK_STAIRS)) {
                chair1.setType(Material.OAK_STAIRS);
                blocksBuilt++;
            }
        }
        
        if (blocksBuilt < maxBlocks) {
            Block chair2 = center.getWorld().getBlockAt(center.getBlockX() + 13, center.getBlockY() + 1, center.getBlockZ() + 3);
            if (!chair2.getType().equals(Material.SPRUCE_STAIRS)) {
                chair2.setType(Material.SPRUCE_STAIRS);
                blocksBuilt++;
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付き倉庫のチェスト
     */
    private int addStorageChestsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // 複数のチェスト
        if (blocksBuilt < maxBlocks) {
            Block chest1 = center.getWorld().getBlockAt(center.getBlockX() + 3, center.getBlockY() + 1, center.getBlockZ() + 11);
            if (!chest1.getType().equals(Material.CHEST)) {
                chest1.setType(Material.CHEST);
                blocksBuilt++;
            }
        }
        
        if (blocksBuilt < maxBlocks) {
            Block chest2 = center.getWorld().getBlockAt(center.getBlockX() + 11, center.getBlockY() + 1, center.getBlockZ() + 11);
            if (!chest2.getType().equals(Material.CHEST)) {
                chest2.setType(Material.CHEST);
                blocksBuilt++;
            }
        }
        
        if (blocksBuilt < maxBlocks) {
            Block chest3 = center.getWorld().getBlockAt(center.getBlockX() + 7, center.getBlockY() + 1, center.getBlockZ() + 13);
            if (!chest3.getType().equals(Material.BARREL)) {
                chest3.setType(Material.BARREL);
                blocksBuilt++;
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 制限付きその他の実用品
     */
    private int addUtilityItemsLimited(Location center, int maxBlocks) {
        int blocksBuilt = 0;
        
        // 各部屋にランタン
        if (blocksBuilt < maxBlocks) {
            Block lantern1 = center.getWorld().getBlockAt(center.getBlockX() + 3, center.getBlockY() + 1, center.getBlockZ() + 1);
            if (!lantern1.getType().equals(Material.LANTERN)) {
                lantern1.setType(Material.LANTERN);
                blocksBuilt++;
            }
        }
        
        if (blocksBuilt < maxBlocks) {
            Block lantern2 = center.getWorld().getBlockAt(center.getBlockX() + 11, center.getBlockY() + 1, center.getBlockZ() + 3);
            if (!lantern2.getType().equals(Material.SOUL_LANTERN)) {
                lantern2.setType(Material.SOUL_LANTERN);
                blocksBuilt++;
            }
        }
        
        // 本立て
        if (blocksBuilt < maxBlocks) {
            Block lectern = center.getWorld().getBlockAt(center.getBlockX() + 3, center.getBlockY() + 1, center.getBlockZ() + 4);
            if (!lectern.getType().equals(Material.LECTERN)) {
                lectern.setType(Material.LECTERN);
                blocksBuilt++;
            }
        }
        
        return blocksBuilt;
    }
    
    /**
     * 必須アイテムを配置（エンチャントテーブル×1、本棚×2、作業台×1、ラピスラズリブロック×1）
     */
    private void placeRequiredItems(Location center) {
        // エンチャントテーブル（エンチャント室）
        Block enchantTable = center.getWorld().getBlockAt(center.getBlockX() + 2, center.getBlockY() + 1, center.getBlockZ() + 2);
        enchantTable.setType(Material.ENCHANTING_TABLE);
        
        // 本棚×2（エンチャントテーブル周辺）
        Block bookshelf1 = center.getWorld().getBlockAt(center.getBlockX() + 1, center.getBlockY() + 1, center.getBlockZ() + 2);
        Block bookshelf2 = center.getWorld().getBlockAt(center.getBlockX() + 3, center.getBlockY() + 1, center.getBlockZ() + 2);
        
        bookshelf1.setType(Material.BOOKSHELF);
        bookshelf2.setType(Material.BOOKSHELF);
        
        // ラピスラズリブロック×1（エンチャントテーブル付近）
        Block lapisBlock = center.getWorld().getBlockAt(center.getBlockX() + 2, center.getBlockY() + 1, center.getBlockZ() + 1);
        lapisBlock.setType(Material.LAPIS_BLOCK);
        
        // 作業台（作業室）
        Block craftingTable = center.getWorld().getBlockAt(center.getBlockX() + 12, center.getBlockY() + 1, center.getBlockZ() + 2);
        craftingTable.setType(Material.CRAFTING_TABLE);
    }
}