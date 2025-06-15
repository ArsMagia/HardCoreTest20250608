package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
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
        private final Random random = new Random();
        
        public FireRainEffect(JavaPlugin plugin) {
            super(plugin, "炎の雨", EffectRarity.UNCOMMON);
        }
        
        @Override
        public String apply(Player player) {
            org.bukkit.Location playerLoc = player.getLocation();
            
            // 2,3個のファイアチャージを頭上のどこか近くから降らせる
            int fireChargeCount = 2 + random.nextInt(2); // 2-3個
            
            for (int i = 0; i < fireChargeCount; i++) {
                org.bukkit.Location spawnLoc = playerLoc.clone().add(
                    (random.nextDouble() - 0.5) * 10, // 頭上のどこか近く
                    15,
                    (random.nextDouble() - 0.5) * 10
                );
                
                // ファイアチャージを生成
                org.bukkit.entity.Fireball fireball = playerLoc.getWorld().spawn(spawnLoc, org.bukkit.entity.SmallFireball.class);
                fireball.setDirection(new org.bukkit.util.Vector(0, -1, 0));
            }
            
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
    
    // エフェクト8: 武器呪い
    public static class WeaponCurseEffect extends UnluckyEffectBase {
        public WeaponCurseEffect(JavaPlugin plugin) {
            super(plugin, "武器呪い", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // すべての武器、ツールの耐久力を10にする
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && isWeaponOrTool(item.getType()) && item.getType().getMaxDurability() > 0) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof Damageable) {
                        Damageable damageable = (Damageable) meta;
                        int maxDurability = item.getType().getMaxDurability();
                        damageable.setDamage(maxDurability - 10);
                        item.setItemMeta(meta);
                    }
                }
            }
            player.sendMessage(ChatColor.RED + "武器に呪いがかかり、耐久力が10になりました！");
            return getDescription();
        }
        
        private boolean isWeaponOrTool(Material material) {
            String name = material.toString();
            return name.contains("SWORD") || name.contains("AXE") || name.contains("BOW") ||
                   name.contains("TRIDENT") || name.contains("PICKAXE") || name.contains("SHOVEL") ||
                   name.contains("HOE") || name.contains("SHEARS") || name.contains("FLINT_AND_STEEL") ||
                   name.contains("FISHING_ROD");
        }
    }
    
    // エフェクト9: 防具消失
    public static class ArmorVanishEffect extends UnluckyEffectBase {
        public ArmorVanishEffect(JavaPlugin plugin) {
            super(plugin, "防具消失", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // 装備している防具スロットを確認
            java.util.List<String> armorSlots = new java.util.ArrayList<>();
            
            if (player.getInventory().getHelmet() != null) armorSlots.add("helmet");
            if (player.getInventory().getChestplate() != null) armorSlots.add("chestplate");
            if (player.getInventory().getLeggings() != null) armorSlots.add("leggings");
            if (player.getInventory().getBoots() != null) armorSlots.add("boots");
            
            if (armorSlots.isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + "防具消失が発動しましたが、装備している防具がありませんでした。");
                return getDescription();
            }
            
            // ランダムで1つの防具スロットを選択して削除
            String selectedSlot = armorSlots.get(new Random().nextInt(armorSlots.size()));
            String removedArmorName = "";
            
            switch (selectedSlot) {
                case "helmet":
                    removedArmorName = player.getInventory().getHelmet().getType().name();
                    player.getInventory().setHelmet(null);
                    break;
                case "chestplate":
                    removedArmorName = player.getInventory().getChestplate().getType().name();
                    player.getInventory().setChestplate(null);
                    break;
                case "leggings":
                    removedArmorName = player.getInventory().getLeggings().getType().name();
                    player.getInventory().setLeggings(null);
                    break;
                case "boots":
                    removedArmorName = player.getInventory().getBoots().getType().name();
                    player.getInventory().setBoots(null);
                    break;
            }
            
            player.sendMessage(ChatColor.YELLOW + "防具消失！" + removedArmorName + "が消失しました！");
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
            player.sendMessage(ChatColor.DARK_PURPLE + "記憶が一部失われました...混乱しています。");
            
            // 空のチャットメッセージを20回送信
            new BukkitRunnable() {
                int count = 0;
                
                @Override
                public void run() {
                    if (count >= 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    
                    player.sendMessage(" "); // 空のメッセージ
                    count++;
                }
            }.runTaskTimer(plugin, 5L, 5L); // 0.25秒間隔で送信
            
            return getDescription();
        }
    }
    
}