package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorstBuildingEffect extends UnluckyEffectBase {
    private enum BuildingType {
        BARRIER_FENCE,     // 通行止め用の柵
        GLASS_CAGE,        // ガラス・鉄格子の囲い
        MONSTER_HOUSE,     // ゾンビスポナー付きモンスターハウス
        BURNING_HOUSE      // 燃えるバーニングハウス
    }

    public WorstBuildingEffect(JavaPlugin plugin) {
        super(plugin, "最低の建築", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation().getBlock().getLocation();
        BuildingType buildingType = BuildingType.values()[new Random().nextInt(BuildingType.values().length)];
        
        player.sendMessage(ChatColor.DARK_RED + "💀 最低の建築が開始されます...");
        
        // 建築タイプに応じてメッセージを表示
        String buildingName = switch (buildingType) {
            case BARRIER_FENCE -> "通行止め用の柵";
            case GLASS_CAGE -> "ガラスと鉄格子の囲い";
            case MONSTER_HOUSE -> "ゾンビスポナー付きモンスターハウス";
            case BURNING_HOUSE -> "燃えるバーニングハウス";
        };
        
        Bukkit.broadcastMessage(ChatColor.RED + "⚠ " + player.getName() + " の周囲に「" + buildingName + "」が建築されています！");
        
        // プレイヤーを建築完了まで拘束
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255)); // 5秒間、完全停止（建築時間+余裕）
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 255)); // 採掘も阻止
        player.sendMessage(ChatColor.RED + "建築完了まで動けません...");
        
        // 建築を実行
        new BukkitRunnable() {
            @Override
            public void run() {
                switch (buildingType) {
                    case BARRIER_FENCE -> buildBarrierFence(center);
                    case GLASS_CAGE -> buildGlassCage(center);
                    case MONSTER_HOUSE -> buildMonsterHouse(center);
                    case BURNING_HOUSE -> buildBurningHouse(center);
                }
                
                // 建築完了時に拘束を解除
                player.removePotionEffect(PotionEffectType.SLOWNESS);
                player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
                player.sendMessage(ChatColor.GRAY + "建築が完了しました... お疲れ様でした。");
                
                // 建築完了エフェクト
                center.getWorld().playSound(center, Sound.BLOCK_ANVIL_LAND, 1.0f, 0.8f);
                center.getWorld().spawnParticle(Particle.EXPLOSION, center.clone().add(0, 5, 0), 3, 2, 2, 2, 0);
            }
        }.runTaskLater(plugin, 40L); // 2秒後に建築
        
        return buildingName + "の建築を開始しました";
    }

    private void buildBarrierFence(Location center) {
        Random random = new Random();
        int size = 8 + random.nextInt(5); // 8-12ブロック
        
        // より密集した障壁を作成（十字型から格子型に変更）
        for (int x = -size/2; x <= size/2; x++) {
            for (int z = -size/2; z <= size/2; z++) {
                // 中央4x4エリアは空ける（プレイヤーの脱出経路）
                if (Math.abs(x) <= 1 && Math.abs(z) <= 1) continue;
                
                // 格子パターンで障壁を設置（より密集）
                boolean shouldPlace = false;
                if (x == -size/2 || x == size/2 || z == -size/2 || z == size/2) {
                    // 外周は必ず設置
                    shouldPlace = true;
                } else if ((x + z) % 2 == 0) {
                    // 格子パターン
                    shouldPlace = true;
                } else if (Math.abs(x) == 2 || Math.abs(z) == 2) {
                    // 内側の壁も追加
                    shouldPlace = true;
                }
                
                if (shouldPlace) {
                    // 地面を確認して適切な高さに設置
                    Location groundLoc = center.clone().add(x, 0, z);
                    while (groundLoc.getY() > 0 && groundLoc.getBlock().getType() == Material.AIR) {
                        groundLoc.add(0, -1, 0);
                    }
                    
                    // 下に1ブロック、上に2ブロック拡張（計4ブロック高）
                    for (int y = -1; y <= 2; y++) {
                        Block block = groundLoc.clone().add(0, y + 1, 0).getBlock();
                        if (block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA) {
                            Material material = switch (random.nextInt(4)) {
                                case 0 -> Material.DARK_OAK_FENCE;
                                case 1 -> Material.COBBLESTONE_WALL;
                                case 2 -> Material.IRON_BARS;
                                default -> Material.STONE_BRICK_WALL;
                            };
                            block.setType(material);
                        }
                    }
                }
            }
        }
        
        // 追加の妨害ブロック（ランダム配置）
        for (int i = 0; i < size; i++) {
            int x = random.nextInt(size) - size/2;
            int z = random.nextInt(size) - size/2;
            if (Math.abs(x) > 1 || Math.abs(z) > 1) { // 中央を避ける
                Location extraLoc = center.clone().add(x, 1, z);
                if (extraLoc.getBlock().getType() == Material.AIR) {
                    extraLoc.getBlock().setType(Material.COBWEB); // 蜘蛛の巣で更に妨害
                }
            }
        }
        
        // 「工事中」の看板を設置
        Location signLoc = center.clone().add(0, 2, size/2 + 1);
        if (signLoc.getBlock().getType() == Material.AIR) {
            signLoc.getBlock().setType(Material.OAK_SIGN);
        }
    }

    private void buildGlassCage(Location center) {
        Random random = new Random();
        int size = 6 + random.nextInt(7); // 6-12ブロック
        
        // プレイヤーを囲むガラスと鉄格子の囲い（下1ブロック、上2ブロック拡張）
        for (int x = -size/2; x <= size/2; x++) {
            for (int z = -size/2; z <= size/2; z++) {
                // 地面レベルを見つける
                Location groundLoc = center.clone().add(x, 0, z);
                while (groundLoc.getY() > 0 && groundLoc.getBlock().getType() == Material.AIR) {
                    groundLoc.add(0, -1, 0);
                }
                
                // 下1ブロックから上2ブロックまで（計4ブロック高）
                for (int y = -1; y <= 2; y++) {
                    boolean isEdge = (x == -size/2 || x == size/2 || z == -size/2 || z == size/2);
                    boolean isTop = (y == 2);
                    boolean isBottom = (y == -1);
                    
                    if (isEdge || isTop || isBottom) {
                        Location loc = groundLoc.clone().add(0, y + 1, 0);
                        Block block = loc.getBlock();
                        
                        if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                            if (isBottom) {
                                // 地下は頑丈なブロック
                                block.setType(random.nextBoolean() ? Material.OBSIDIAN : Material.CRYING_OBSIDIAN);
                            } else if (isTop) {
                                // 天井
                                block.setType(Material.IRON_BARS);
                            } else {
                                // 壁
                                if (random.nextDouble() < 0.6) {
                                    block.setType(Material.GLASS);
                                } else {
                                    block.setType(Material.IRON_BARS);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 邪魔なガラスを追加で配置
        for (int i = 0; i < size; i++) {
            int x = random.nextInt(size) - size/2;
            int z = random.nextInt(size) - size/2;
            int y = random.nextInt(3) + 1;
            
            Location loc = center.clone().add(x, y, z);
            if (loc.getBlock().getType() == Material.AIR) {
                loc.getBlock().setType(Material.GLASS);
            }
        }
    }

    private void buildMonsterHouse(Location center) {
        Random random = new Random();
        int size = 11 + random.nextInt(5); // 11-15ブロック（横幅を3マス拡張）
        
        // 基本的な小屋の構造（下2ブロック、上2ブロック拡張）- 中央3x3空間確保
        for (int x = -size/2; x <= size/2; x++) {
            for (int z = -size/2; z <= size/2; z++) {
                // 地面レベルを見つける
                Location groundLoc = center.clone().add(x, 0, z);
                while (groundLoc.getY() > 0 && groundLoc.getBlock().getType() == Material.AIR) {
                    groundLoc.add(0, -1, 0);
                }
                
                // 中央エリア（3x3）は完全に空間として確保
                boolean isCenterArea = (Math.abs(x) <= 1 && Math.abs(z) <= 1);
                
                // 下2ブロックから上2ブロックまで（計5ブロック高）
                for (int y = -2; y <= 2; y++) {
                    boolean isEdge = (x == -size/2 || x == size/2 || z == -size/2 || z == size/2);
                    boolean isTop = (y == 2);
                    boolean isBottom = (y <= -1);
                    boolean isFloor = (y == 0);
                    
                    Location loc = groundLoc.clone().add(0, y + 1, 0);
                    Block block = loc.getBlock();
                    
                    if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                        if (isBottom && !isCenterArea) {
                            // 地下基盤（中央エリア以外）- より深く
                            block.setType(Material.DEEPSLATE);
                        } else if (isFloor && !isCenterArea) {
                            // 床（中央エリアは完全に空ける）
                            block.setType(Material.STONE_BRICKS);
                        } else if (isFloor && isCenterArea) {
                            // 中央エリアの床は空気のまま（プレイヤーの足元余裕）
                            continue;
                        } else if (isEdge && y > 0 && y < 2) {
                            // 壁
                            block.setType(Material.COBBLESTONE);
                        } else if (isTop && !isCenterArea) {
                            // 屋根（中央エリア以外）
                            block.setType(Material.STONE_BRICK_SLAB);
                        }
                        // 中央エリアの天井も完全に空けておく
                    }
                }
            }
        }
        
        // 中央から離れた位置にゾンビスポナーを配置（プレイヤーの3x3エリアを避ける）
        Location spawnerLoc = center.clone().add(3, 1, 3);
        if (spawnerLoc.getBlock().getType() == Material.AIR) {
            spawnerLoc.getBlock().setType(Material.SPAWNER);
            if (spawnerLoc.getBlock().getState() instanceof CreatureSpawner spawner) {
                spawner.setSpawnedType(EntityType.ZOMBIE);
                spawner.setDelay(20); // 1秒間隔
                spawner.update();
            }
        }
        
        // ダンジョンらしい装飾（中央3x3エリアを完全に避けて配置）
        List<Location> decorationSpots = new ArrayList<>();
        for (int x = -size/2 + 1; x < size/2; x++) {
            for (int z = -size/2 + 1; z < size/2; z++) {
                // 中央3x3エリアとスポナー位置を避ける
                if ((Math.abs(x) > 1 || Math.abs(z) > 1) && !(x == 3 && z == 3)) {
                    decorationSpots.add(center.clone().add(x, 1, z));
                }
            }
        }
        
        // ランダムに邪魔な装飾を配置
        for (int i = 0; i < 6 && !decorationSpots.isEmpty(); i++) {
            Location decorLoc = decorationSpots.remove(random.nextInt(decorationSpots.size()));
            if (decorLoc.getBlock().getType() == Material.AIR) {
                Material decorMaterial = switch (random.nextInt(5)) {
                    case 0 -> Material.COBWEB;
                    case 1 -> Material.SOUL_SAND;
                    case 2 -> Material.IRON_BARS;
                    case 3 -> Material.SKELETON_SKULL;
                    default -> Material.ZOMBIE_HEAD;
                };
                decorLoc.getBlock().setType(decorMaterial);
            }
        }
        
        // 出入り口を作成（一方向のみ、脱出可能だが制限的）
        Location doorLoc = center.clone().add(size/2, 1, 0);
        if (doorLoc.getBlock().getType() == Material.COBBLESTONE) {
            doorLoc.getBlock().setType(Material.AIR); // ドア下部
            doorLoc.clone().add(0, 1, 0).getBlock().setType(Material.AIR); // ドア上部
        }
    }

    private void buildBurningHouse(Location center) {
        Random random = new Random();
        int size = 10 + random.nextInt(6); // 10-15ブロック（横幅を3マス拡張）
        
        // 燃えやすい材料で家を建築（下2ブロック、上2ブロック拡張）- 中央3x3空間確保
        for (int x = -size/2; x <= size/2; x++) {
            for (int z = -size/2; z <= size/2; z++) {
                // 地面レベルを見つける
                Location groundLoc = center.clone().add(x, 0, z);
                while (groundLoc.getY() > 0 && groundLoc.getBlock().getType() == Material.AIR) {
                    groundLoc.add(0, -1, 0);
                }
                
                // 中央エリア（3x3）は完全に空間として確保
                boolean isCenterArea = (Math.abs(x) <= 1 && Math.abs(z) <= 1);
                
                // 下2ブロックから上2ブロックまで（計5ブロック高）
                for (int y = -2; y <= 2; y++) {
                    boolean isEdge = (x == -size/2 || x == size/2 || z == -size/2 || z == size/2);
                    boolean isTop = (y == 2);
                    boolean isBottom = (y <= -1);
                    boolean isFloor = (y == 0);
                    
                    Location loc = groundLoc.clone().add(0, y + 1, 0);
                    Block block = loc.getBlock();
                    
                    if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
                        if (isBottom && !isCenterArea) {
                            // 地下は石材（燃えない基盤）- より深く
                            block.setType(Material.COBBLESTONE);
                        } else if (isFloor && !isCenterArea) {
                            // 床 - 燃えやすい材料（中央エリアは完全に空ける）
                            block.setType(Material.DARK_OAK_PLANKS);
                        } else if (isFloor && isCenterArea) {
                            // 中央エリアの床は空気のまま（プレイヤーの足元余裕）
                            continue;
                        } else if (isEdge && y > 0 && y < 2) {
                            // 壁 - 燃えやすい材料
                            Material wallMaterial = switch (random.nextInt(4)) {
                                case 0 -> Material.OAK_PLANKS;
                                case 1 -> Material.SPRUCE_PLANKS;
                                case 2 -> Material.OAK_LOG;
                                default -> Material.DARK_OAK_LOG;
                            };
                            block.setType(wallMaterial);
                        } else if (isTop && !isCenterArea) {
                            // 屋根 - 燃えやすい材料（中央エリア以外）
                            block.setType(random.nextBoolean() ? Material.OAK_SLAB : Material.SPRUCE_SLAB);
                        }
                        // 中央エリアの天井も完全に空けておく
                    }
                }
            }
        }
        
        // 火を設置して燃やす（中央3x3エリアを避ける）
        new BukkitRunnable() {
            int fireCount = 0;
            final int maxFires = 20; // より多くの火を設置（家が大きくなったため）
            
            @Override
            public void run() {
                if (fireCount >= maxFires) {
                    this.cancel();
                    return;
                }
                
                // ランダムな位置に火を設置（中央3x3エリアを避ける）
                int x, z;
                do {
                    x = random.nextInt(size + 1) - size/2;
                    z = random.nextInt(size + 1) - size/2;
                } while (Math.abs(x) <= 1 && Math.abs(z) <= 1); // 中央3x3エリアを避ける
                
                for (int y = 1; y <= 4; y++) {
                    Location fireLoc = center.clone().add(x, y, z);
                    Block fireBlock = fireLoc.getBlock();
                    Block belowBlock = fireLoc.clone().add(0, -1, 0).getBlock();
                    
                    if (fireBlock.getType() == Material.AIR && belowBlock.getType() != Material.AIR) {
                        fireBlock.setType(Material.FIRE);
                        
                        // 燃焼音と煙エフェクト
                        fireLoc.getWorld().playSound(fireLoc, Sound.BLOCK_FIRE_AMBIENT, 0.8f, 1.0f);
                        fireLoc.getWorld().spawnParticle(Particle.SMOKE, fireLoc, 5, 0.5, 0.5, 0.5, 0.02);
                        break;
                    }
                }
                
                fireCount++;
            }
        }.runTaskTimer(plugin, 20L, 10L); // 1秒後から0.5秒間隔で火を設置
        
        // 溶岩は使用せず、代わりに追加の燃えやすいブロックを配置
        for (int i = 0; i < 5; i++) {
            int x, z;
            do {
                x = random.nextInt(size - 2) - (size/2 - 1);
                z = random.nextInt(size - 2) - (size/2 - 1);
            } while (Math.abs(x) <= 1 && Math.abs(z) <= 1); // 中央3x3エリアを避ける
            
            Location extraBurnableLoc = center.clone().add(x, 1, z);
            if (extraBurnableLoc.getBlock().getType() == Material.AIR) {
                // 燃えやすい追加ブロック
                Material burnableMaterial = switch (random.nextInt(3)) {
                    case 0 -> Material.HAY_BLOCK;
                    case 1 -> Material.DRIED_KELP_BLOCK;
                    default -> Material.OAK_LEAVES;
                };
                extraBurnableLoc.getBlock().setType(burnableMaterial);
            }
        }
    }
}