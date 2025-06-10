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

// 複数のシンプルなアンラッキー効果をまとめたクラス
public class SimpleUnluckyEffects {
    
    // エフェクト1: ランダムテレポート
    public static class RandomTeleportEffect extends UnluckyEffectBase {
        private final Random random = new Random();
        
        public RandomTeleportEffect(JavaPlugin plugin) {
            super(plugin, "ランダムテレポート", EffectRarity.UNCOMMON);
        }
        
        @Override
        public String apply(Player player) {
            org.bukkit.Location loc = player.getLocation();
            int x = loc.getBlockX() + random.nextInt(40) - 20;
            int z = loc.getBlockZ() + random.nextInt(40) - 20;
            int y = loc.getWorld().getHighestBlockYAt(x, z);
            
            org.bukkit.Location newLoc = new org.bukkit.Location(loc.getWorld(), x + 0.5, y + 1, z + 0.5);
            player.teleport(newLoc);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "ランダムな場所にテレポートしました！");
            return getDescription();
        }
    }
    
    // エフェクト2: アイテム重量化
    public static class ItemWeightEffect extends UnluckyEffectBase {
        public ItemWeightEffect(JavaPlugin plugin) {
            super(plugin, "アイテム重量化", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
            player.sendMessage(ChatColor.GRAY + "アイテムが重くなりました...移動速度が低下します。");
            return getDescription();
        }
    }
    
    // エフェクト3: 空腹の呪い
    public static class HungerCurseEffect extends UnluckyEffectBase {
        public HungerCurseEffect(JavaPlugin plugin) {
            super(plugin, "空腹の呪い", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.setFoodLevel(Math.max(1, player.getFoodLevel() - 10));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 400, 1));
            player.sendMessage(ChatColor.GOLD + "強烈な空腹感に襲われました...");
            return getDescription();
        }
    }
    
    // エフェクト4: 炎の雨
    public static class FireRainEffect extends UnluckyEffectBase {
        public FireRainEffect(JavaPlugin plugin) {
            super(plugin, "炎の雨", EffectRarity.UNCOMMON);
        }
        
        @Override
        public String apply(Player player) {
            org.bukkit.Location center = player.getLocation();
            new BukkitRunnable() {
                int counter = 0;
                
                @Override
                public void run() {
                    if (counter >= 10 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    
                    for (int i = 0; i < 3; i++) {
                        org.bukkit.Location fireLoc = center.clone().add(
                            Math.random() * 8 - 4, 0, Math.random() * 8 - 4);
                        if (fireLoc.getBlock().getType() == Material.AIR) {
                            fireLoc.getBlock().setType(Material.FIRE);
                        }
                    }
                    counter++;
                }
            }.runTaskTimer(plugin, 0L, 20L);
            
            player.sendMessage(ChatColor.RED + "炎の雨が降り注ぎます！");
            return getDescription();
        }
    }
    
    // エフェクト5: 経験値流出
    public static class ExpLeakEffect extends UnluckyEffectBase {
        public ExpLeakEffect(JavaPlugin plugin) {
            super(plugin, "経験値流出", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            int lostExp = Math.min(100, player.getTotalExperience() / 5);
            player.giveExp(-lostExp);
            player.sendMessage(ChatColor.GREEN + "経験値が流出しました..." + lostExp + "の経験値を失いました。");
            return getDescription();
        }
    }
    
    // エフェクト6: 逆操作効果
    public static class ReverseControlsEffect extends UnluckyEffectBase {
        public ReverseControlsEffect(JavaPlugin plugin) {
            super(plugin, "逆操作効果", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "操作が逆になりました...混乱します！");
            return getDescription();
        }
    }
    
    // エフェクト7: 偽死効果
    public static class FakeDeathEffect extends UnluckyEffectBase {
        public FakeDeathEffect(JavaPlugin plugin) {
            super(plugin, "偽死効果", EffectRarity.UNCOMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 4));
            player.sendMessage(ChatColor.BLACK + "死んだふりをしてしまいました...");
            return getDescription();
        }
    }
    
    // エフェクト8: 武器呪い
    public static class WeaponCurseEffect extends UnluckyEffectBase {
        public WeaponCurseEffect(JavaPlugin plugin) {
            super(plugin, "武器呪い", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // 武器系アイテムを一時的に削除
            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && isWeapon(item.getType())) {
                    player.getInventory().setItem(i, null);
                }
            }
            player.sendMessage(ChatColor.RED + "武器に呪いがかかり、消失しました！");
            return getDescription();
        }
        
        private boolean isWeapon(Material material) {
            return material.toString().contains("SWORD") || 
                   material.toString().contains("AXE") ||
                   material.toString().contains("BOW") ||
                   material.toString().contains("TRIDENT");
        }
    }
    
    // エフェクト9: 防具消失
    public static class ArmorVanishEffect extends UnluckyEffectBase {
        public ArmorVanishEffect(JavaPlugin plugin) {
            super(plugin, "防具消失", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);
            player.sendMessage(ChatColor.YELLOW + "防具が消失しました！");
            return getDescription();
        }
    }
    
    // エフェクト10: 記憶喪失
    public static class MemoryLossEffect extends UnluckyEffectBase {
        public MemoryLossEffect(JavaPlugin plugin) {
            super(plugin, "記憶喪失", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
            player.sendMessage(ChatColor.DARK_PURPLE + "記憶が一部失われました...混乱しています。");
            return getDescription();
        }
    }
    
}