package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

// 追加のアンラッキー効果
public class MoreUnluckyEffects {
    
    // エフェクト11: 溺れる感覚
    public static class DrowningFeelingEffect extends UnluckyEffectBase {
        public DrowningFeelingEffect(JavaPlugin plugin) {
            super(plugin, "溺れる感覚", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
            player.sendMessage(ChatColor.BLUE + "溺れているような感覚に襲われます...息苦しい!");
            return getDescription();
        }
    }
    
    // エフェクト12: 暗闇恐怖症
    public static class DarkphobiaEffect extends UnluckyEffectBase {
        public DarkphobiaEffect(JavaPlugin plugin) {
            super(plugin, "暗闇恐怖症", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
            player.sendMessage(ChatColor.BLACK + "暗闇への恐怖に支配されました...");
            return getDescription();
        }
    }
    
    // エフェクト13: 手の震え
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
            super(plugin, "電撃ショック", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.damage(2.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
            player.sendMessage(ChatColor.YELLOW + "電撃ショックを受けました!");
            player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 2.0f);
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
            player.sendMessage(ChatColor.AQUA + "体が凍りついて動けません!");
            return getDescription();
        }
    }
    
    // エフェクト19: 逆方向歩行
    public static class BackwardWalkingEffect extends UnluckyEffectBase {
        public BackwardWalkingEffect(JavaPlugin plugin) {
            super(plugin, "逆方向歩行", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 0));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "後ろ向きにしか歩けなくなりました...");
            return getDescription();
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
            
            for (int i = 0; i < 9; i++) {
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
            // 金属装備をチェックして削除
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item != null && isMetalItem(item.getType())) {
                    player.getInventory().setItem(i, null);
                }
            }
            
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
            player.sendMessage(ChatColor.RED + "金属アレルギーにより金属アイテムが消失しました!");
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
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
    
    // エフェクト24: 数字忘却症
    public static class NumberAmnesiaEffect extends UnluckyEffectBase {
        public NumberAmnesiaEffect(JavaPlugin plugin) {
            super(plugin, "数字忘却症", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.sendMessage(ChatColor.DARK_GRAY + "数字を忘れてしまいました...座標やアイテム数がわからなくなります。");
            return getDescription();
        }
    }
    
    // エフェクト25: 呪文詠唱失敗
    public static class SpellFailureEffect extends UnluckyEffectBase {
        public SpellFailureEffect(JavaPlugin plugin) {
            super(plugin, "呪文詠唱失敗", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.damage(1.0);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
            player.sendMessage(ChatColor.DARK_PURPLE + "呪文の詠唱に失敗し、反動を受けました!");
            return getDescription();
        }
    }
}