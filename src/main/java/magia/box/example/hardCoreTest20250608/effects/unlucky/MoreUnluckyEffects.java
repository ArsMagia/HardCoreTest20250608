package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

// 追加のアンラッキー効果
public class MoreUnluckyEffects {
    
    // エフェクト10: 溺水感覚
    public static class DrowningFeelingEffect extends UnluckyEffectBase {
        public DrowningFeelingEffect(JavaPlugin plugin) {
            super(plugin, "溺水感覚", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 300, 1));
            player.sendMessage(ChatColor.BLUE + "溺れているような感覚に襲われました...");
            return getDescription();
        }
    }
    
    // エフェクト12: 電撃ショック
    public static class DarkphobiaEffect extends UnluckyEffectBase {
        public DarkphobiaEffect(JavaPlugin plugin) {
            super(plugin, "暗闇恐怖症", EffectRarity.UNCOMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
            player.sendMessage(ChatColor.BLACK + "暗闇への恐怖に支配されました...");
            return getDescription();
        }
    }
    
    // エフェクト11: 手の震え
    public static class HandTremorEffect extends UnluckyEffectBase {
        public HandTremorEffect(JavaPlugin plugin) {
            super(plugin, "手の震え", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 2));
            player.sendMessage(ChatColor.YELLOW + "手が震えて作業効率が落ちました...");
            return getDescription();
        }
    }
    
    // エフェクト14: 足の痙攣
    public static class LegCrampEffect extends UnluckyEffectBase {
        public LegCrampEffect(JavaPlugin plugin) {
            super(plugin, "足の痙攣", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 300, -3));
            player.sendMessage(ChatColor.RED + "足が痙攣して思うように動けません!");
            return getDescription();
        }
    }
    
    // エフェクト15: 睡眠不足
    public static class SleepDeprivationEffect extends UnluckyEffectBase {
        public SleepDeprivationEffect(JavaPlugin plugin) {
            super(plugin, "睡眠不足", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 0));
            player.sendMessage(ChatColor.GRAY + "睡眠不足で体がだるくなりました...");
            return getDescription();
        }
    }
    
    // エフェクト16: 混乱の霧
    public static class ConfusionMistEffect extends UnluckyEffectBase {
        public ConfusionMistEffect(JavaPlugin plugin) {
            super(plugin, "混乱の霧", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "混乱の霧に包まれました...");
            return getDescription();
        }
    }
    
    // エフェクト17: 電撃ショック
    public static class ElectricShockEffect extends UnluckyEffectBase {
        public ElectricShockEffect(JavaPlugin plugin) {
            super(plugin, "電撃ショック", EffectRarity.EPIC);
        }
        
        @Override
        public String apply(Player player) {
            // プレイヤーの位置に雷を落とす
            player.getWorld().strikeLightning(player.getLocation());
            player.sendMessage(ChatColor.YELLOW + "電撃ショックを受けました!");
            return getDescription();
        }
    }
    
    // エフェクト18: 冷凍状態
    public static class FrozenStateEffect extends UnluckyEffectBase {
        public FrozenStateEffect(JavaPlugin plugin) {
            super(plugin, "冷凍状態", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 200, 2));
            
            // 寒いエフェクトを追加
            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
            
            player.sendMessage(ChatColor.AQUA + "体が凍りついて動けません!");
            return getDescription();
        }
    }
    
    // エフェクト19: 逆方向歩行
    public static class BackwardWalkingEffect extends UnluckyEffectBase implements Listener {
        private static final Set<UUID> affectedPlayers = new HashSet<>();
        private static final long EFFECT_DURATION = 300L; // 15秒間
        
        public BackwardWalkingEffect(JavaPlugin plugin) {
            super(plugin, "逆方向歩行", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            UUID playerId = player.getUniqueId();
            
            if (affectedPlayers.contains(playerId)) {
                player.sendMessage(ChatColor.YELLOW + "既に逆方向歩行の影響を受けています。");
                return getDescription();
            }
            
            affectedPlayers.add(playerId);
            
            // リスナーを登録
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            
            player.sendMessage(ChatColor.LIGHT_PURPLE + "後ろ向きにしか歩けなくなりました...");
            
            // 15秒後に効果を解除
            new BukkitRunnable() {
                @Override
                public void run() {
                    removeEffect(player);
                }
            }.runTaskLater(plugin, EFFECT_DURATION);
            
            return getDescription();
        }
        
        private void removeEffect(Player player) {
            UUID playerId = player.getUniqueId();
            affectedPlayers.remove(playerId);
            
            if (player.isOnline()) {
                player.sendMessage(ChatColor.GREEN + "正常な歩行が可能になりました。");
            }
            
            // 影響を受けているプレイヤーがいなくなったらリスナーを解除
            if (affectedPlayers.isEmpty()) {
                HandlerList.unregisterAll(this);
            }
        }
        
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            
            if (!affectedPlayers.contains(player.getUniqueId())) {
                return;
            }
            
            // 移動を検知（位置変化のみ、視点変更は除く）
            if (event.getFrom().distance(event.getTo()) > 0.1) {
                // プレイヤーの視点方向を取得
                Vector direction = player.getLocation().getDirection();
                
                // 視点の逆方向にvelocityを設定
                Vector backwardDirection = direction.multiply(-0.3); // 逆方向に0.3倍の力
                backwardDirection.setY(0); // Y方向は変更しない（落下防止）
                
                player.setVelocity(backwardDirection);
            }
        }
    }
    
    // エフェクト20: 道具中毒
    public static class ToolAddictionEffect extends UnluckyEffectBase {
        private final Random random = new Random();
        
        public ToolAddictionEffect(JavaPlugin plugin) {
            super(plugin, "道具中毒", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            Material[] tools = {Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_HOE};
            Material tool = tools[random.nextInt(tools.length)];
            
            // ホットバーの一番左（インデックス0）のみ対象外
            for (int i = 1; i < 9; i++) {
                if (random.nextBoolean()) {
                    player.getInventory().setItem(i, new ItemStack(tool));
                }
            }
            
            player.sendMessage(ChatColor.GOLD + "道具中毒になり、ホットバーが木の道具で埋まりました!");
            return getDescription();
        }
    }
    
    // エフェクト21: 金属アレルギー
    public static class MetalAllergyEffect extends UnluckyEffectBase {
        public MetalAllergyEffect(JavaPlugin plugin) {
            super(plugin, "金属アレルギー", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // インベントリ内で金属アイテムをランダムで1つのみ削除
            ItemStack[] contents = player.getInventory().getContents();
            java.util.List<Integer> metalItemSlots = new java.util.ArrayList<>();
            
            // 金属アイテムのスロットを収集
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item != null && isMetalItem(item.getType())) {
                    metalItemSlots.add(i);
                }
            }
            
            // 金属アイテムがある場合、ランダムで1つだけ削除
            if (!metalItemSlots.isEmpty()) {
                int randomSlot = metalItemSlots.get(new Random().nextInt(metalItemSlots.size()));
                ItemStack removedItem = contents[randomSlot];
                player.getInventory().setItem(randomSlot, null);
                player.sendMessage(ChatColor.RED + "金属アレルギーにより" + removedItem.getType().name() + "が消失しました!");
            } else {
                player.sendMessage(ChatColor.YELLOW + "金属アレルギーが発症しましたが、金属アイテムがありませんでした。");
            }
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
            return getDescription();
        }
        
        private boolean isMetalItem(Material material) {
            String name = material.toString();
            return name.contains("IRON") || name.contains("GOLD") || name.contains("DIAMOND") || name.contains("NETHERITE");
        }
    }
    
    // エフェクト22: 重力酔い
    public static class GravitySicknessEffect extends UnluckyEffectBase {
        public GravitySicknessEffect(JavaPlugin plugin) {
            super(plugin, "重力酔い", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 1)); // 3秒間のLevitation I
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 0)); // 15秒間のNausea I
            player.sendMessage(ChatColor.LIGHT_PURPLE + "重力酔いで気分が悪くなりました...");
            return getDescription();
        }
    }
    
    // エフェクト23: 音響過敏症
    public static class HyperacusisEffect extends UnluckyEffectBase {
        public HyperacusisEffect(JavaPlugin plugin) {
            super(plugin, "音響過敏症", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            new BukkitRunnable() {
                int counter = 0;
                
                @Override
                public void run() {
                    if (counter >= 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 2.0f, 0.5f);
                    counter++;
                }
            }.runTaskTimer(plugin, 0L, 10L);
            
            player.sendMessage(ChatColor.YELLOW + "音響過敏症により、すべての音が大きく聞こえます!");
            return getDescription();
        }
    }
    
    
}