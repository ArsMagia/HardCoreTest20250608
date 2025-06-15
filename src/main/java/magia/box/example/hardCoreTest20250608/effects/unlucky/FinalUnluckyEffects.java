package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// 最終アンラッキー効果群（26-50）
public class FinalUnluckyEffects {
    private static final Random random = new Random();
    private static final Map<UUID, Location> previousLocations = new HashMap<>();
    private static final Map<UUID, BukkitRunnable> mirrorTasks = new HashMap<>();
    
    // 26. ミラー効果（逆転世界）
    public static class MirrorWorldEffect extends UnluckyEffectBase {
        public MirrorWorldEffect(JavaPlugin plugin) {
            super(plugin, "ミラー効果", EffectRarity.LEGENDARY);
        }
        @Override
        public String apply(Player player) {
            // 吐き気効果
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 2));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "世界が鏡のように反転しました...");
            player.sendMessage(ChatColor.YELLOW + "10秒間、移動操作が逆転します！");
            
            // 既存のミラータスクがあれば停止
            BukkitRunnable existingTask = mirrorTasks.get(player.getUniqueId());
            if (existingTask != null) {
                existingTask.cancel();
            }
            
            // プレイヤーの初期位置を記録
            previousLocations.put(player.getUniqueId(), player.getLocation().clone());
            
            // 操作反転タスクを開始
            BukkitRunnable mirrorTask = new BukkitRunnable() {
                int ticks = 0;
                final int maxTicks = 200; // 10秒 = 200ticks
                
                @Override
                public void run() {
                    if (ticks >= maxTicks || !player.isOnline()) {
                        // 効果終了
                        previousLocations.remove(player.getUniqueId());
                        mirrorTasks.remove(player.getUniqueId());
                        if (player.isOnline()) {
                            player.sendMessage(ChatColor.GRAY + "ミラー効果が解除されました。");
                        }
                        this.cancel();
                        return;
                    }
                    
                    // プレイヤーの移動を検知して反転
                    Location currentLoc = player.getLocation();
                    Location prevLoc = previousLocations.get(player.getUniqueId());
                    
                    if (prevLoc != null) {
                        double deltaX = currentLoc.getX() - prevLoc.getX();
                        double deltaZ = currentLoc.getZ() - prevLoc.getZ();
                        
                        // 移動が検知された場合（閾値0.01以上）
                        if (Math.abs(deltaX) > 0.01 || Math.abs(deltaZ) > 0.01) {
                            // 反転したベクトルを計算（移動距離を2倍に）
                            Vector reversedVector = new Vector(-deltaX * 2, 0, -deltaZ * 2);
                            
                            // 元の位置から反転した方向に移動
                            Location newLoc = prevLoc.clone().add(reversedVector);
                            newLoc.setY(currentLoc.getY()); // Y座標は維持
                            newLoc.setYaw(currentLoc.getYaw()); // 視点も維持
                            newLoc.setPitch(currentLoc.getPitch());
                            
                            // テレポートで位置を補正
                            player.teleport(newLoc);
                            
                            // 新しい位置を記録
                            previousLocations.put(player.getUniqueId(), newLoc.clone());
                        } else {
                            // 移動していない場合は現在位置を更新
                            previousLocations.put(player.getUniqueId(), currentLoc.clone());
                        }
                    }
                    
                    ticks += 2; // 2tick間隔で実行（0.1秒）
                }
            };
            
            mirrorTask.runTaskTimer(plugin, 0L, 2L); // 0.1秒間隔
            mirrorTasks.put(player.getUniqueId(), mirrorTask);
            
            return getDescription();
        }
    }
    
    // 28. 石化の始まり
    public static class PetrificationEffect extends UnluckyEffectBase {
        public PetrificationEffect(JavaPlugin plugin) {
            super(plugin, "石化の始まり", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 3)); // 20秒間のSlowness IV
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 400, 0)); // 20秒間のResistance I（軽い耐性）
            player.sendMessage(ChatColor.GRAY + "体が石のように硬くなり始めました...");
            return getDescription();
        }
    }
    
    // 31. 影の束縛
    public static class ShadowBindingEffect extends UnluckyEffectBase {
        public ShadowBindingEffect(JavaPlugin plugin) {
            super(plugin, "影の束縛", EffectRarity.EPIC);
        }
        @Override
        public String apply(Player player) {
            // サーバー全体に10秒の警告を開始
            Bukkit.broadcastMessage(ChatColor.DARK_RED + "⚠️ 【警告】影の束縛が発動準備中です！");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "🕐 10秒後にサーバー全体のプレイヤーが影に束縛されます！");
            
            // カウントダウンタスク
            new BukkitRunnable() {
                int countdown = 10;
                
                @Override
                public void run() {
                    if (countdown <= 0) {
                        // 影の束縛を実行
                        executeShadowBinding(player);
                        this.cancel();
                        return;
                    }
                    
                    if (countdown <= 5) {
                        Bukkit.broadcastMessage(ChatColor.RED + "⏰ 影の束縛まで " + countdown + " 秒...");
                    }
                    countdown--;
                }
            }.runTaskTimer(plugin, 20L, 20L); // 1秒間隔
            
            return getDescription();
        }
        
        private void executeShadowBinding(Player caster) {
            Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "🌑 影の束縛が発動しました！サーバー全体のプレイヤーが影に捕らわれます！");
            
            // サーバー全体のプレイヤーに効果を適用
            for (Player target : Bukkit.getOnlinePlayers()) {
                // 7秒間（140ティック）の効果
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 140, 2));
                target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 140, 1));
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 140, 0)); // Darknessエフェクト追加
                
                target.sendMessage(ChatColor.BLACK + "🌑 影に足を掴まれました...");
                target.playSound(target.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0f, 0.5f);
                
                // 影のパーティクル効果
                target.getWorld().spawnParticle(
                    Particle.SQUID_INK,
                    target.getLocation().add(0, 1, 0),
                    20, 1, 1, 1, 0.1
                );
            }
            
            // 7秒後に解除メッセージ
            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "✨ 影の束縛が解除されました！全プレイヤーが自由になりました。");
                }
            }.runTaskLater(plugin, 140L); // 7秒後
        }
    }
    
    
    // 34. 磁力異常
    public static class MagneticAnomalyEffect extends UnluckyEffectBase {
        public MagneticAnomalyEffect(JavaPlugin plugin) {
            super(plugin, "磁力異常", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            // 金属アイテムをランダムに並び替え
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && isMetallic(item.getType()) && random.nextBoolean()) {
                    int swapIndex = random.nextInt(36);
                    ItemStack swapItem = player.getInventory().getItem(swapIndex);
                    player.getInventory().setItem(i, swapItem);
                    player.getInventory().setItem(swapIndex, item);
                }
            }
            player.sendMessage(ChatColor.DARK_GRAY + "磁力異常により金属アイテムが移動しました!");
            return getDescription();
        }
        
        private boolean isMetallic(Material mat) {
            String name = mat.toString();
            return name.contains("IRON") || name.contains("GOLD") || name.contains("DIAMOND") || name.contains("NETHERITE");
        }
    }
    
    
    // 36-50: シンプルな効果をまとめて定義
    public static class QuickDebuffEffect extends UnluckyEffectBase {
        private final PotionEffectType effectType;
        private final String displayName;
        
        public QuickDebuffEffect(JavaPlugin plugin, String name, PotionEffectType type) {
            super(plugin, name, EffectRarity.COMMON);
            this.effectType = type;
            this.displayName = name;
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(effectType, 300, 0));
            player.sendMessage(ChatColor.RED + displayName + "の効果を受けました!");
            return getDescription();
        }
    }
    
    // ファクトリーメソッドで残りの効果を生成
    
    public static QuickDebuffEffect createWeaknessVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "力の減退", PotionEffectType.WEAKNESS);
    }
    
    
    public static QuickDebuffEffect createHungerVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "激しい空腹", PotionEffectType.HUNGER);
    }
    
    public static QuickDebuffEffect createNauseaVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "めまい", PotionEffectType.NAUSEA);
    }
    
    
    public static class LightPoisonEffect extends UnluckyEffectBase {
        public LightPoisonEffect(JavaPlugin plugin) {
            super(plugin, "軽い毒", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 160, 0)); // 8秒
            player.sendMessage(ChatColor.RED + "軽い毒の効果を受けました!");
            return getDescription();
        }
    }
    
    public static class WitherEffect extends UnluckyEffectBase {
        public WitherEffect(JavaPlugin plugin) {
            super(plugin, "衰弱", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 160, 0)); // 8秒
            player.sendMessage(ChatColor.RED + "衰弱の効果を受けました!");
            return getDescription();
        }
    }
    
    public static QuickDebuffEffect createLevitationVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "軽い浮遊", PotionEffectType.LEVITATION);
    }
    
    public static class UnluckEffect extends UnluckyEffectBase {
        public UnluckEffect(JavaPlugin plugin) {
            super(plugin, "不幸", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 300, 0));
            player.sendMessage(ChatColor.RED + "不幸の効果を受けました!");
            return getDescription();
        }
    }
    
    public static class DarknessEffect extends UnluckyEffectBase {
        public DarknessEffect(JavaPlugin plugin) {
            super(plugin, "闇の帳", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0)); // 5秒
            player.sendMessage(ChatColor.RED + "闇の帳の効果を受けました!");
            return getDescription();
        }
    }
    
    // 追加の創造的効果
    public static class ItemShuffleEffect extends UnluckyEffectBase {
        public ItemShuffleEffect(JavaPlugin plugin) {
            super(plugin, "アイテムシャッフル", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // インベントリをシャッフル
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length - 1; i++) {
                if (random.nextBoolean()) {
                    int j = i + 1 + random.nextInt(contents.length - i - 1);
                    ItemStack temp = contents[i];
                    contents[i] = contents[j];
                    contents[j] = temp;
                }
            }
            player.getInventory().setContents(contents);
            player.sendMessage(ChatColor.YELLOW + "インベントリがシャッフルされました!");
            return getDescription();
        }
    }
    
}