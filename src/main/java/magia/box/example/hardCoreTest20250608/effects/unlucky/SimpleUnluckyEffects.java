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
    
    // エフェクト1: 逆転操作
    public static class ReverseControlsEffect extends UnluckyEffectBase {
        public ReverseControlsEffect(JavaPlugin plugin) {
            super(plugin, "逆転操作", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
            player.sendMessage(ChatColor.YELLOW + "操作が逆転しました！15秒間混乱します。");
            return getDescription();
        }
    }
    
    // エフェクト2: 偽の死
    public static class FakeDeathEffect extends UnluckyEffectBase {
        public FakeDeathEffect(JavaPlugin plugin) {
            super(plugin, "偽の死", EffectRarity.LEGENDARY);
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 255));
            player.sendMessage(ChatColor.DARK_RED + "死んだふりをしています...5秒間動けません。");
            return getDescription();
        }
    }
    
    // エフェクト3: ランダムテレポート
    public static class RandomTeleportEffect extends UnluckyEffectBase {
        private final Random random = new Random();
        
        public RandomTeleportEffect(JavaPlugin plugin) {
            super(plugin, "ランダムテレポート", EffectRarity.RARE);
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
    
    // エフェクト4: アイテム重量化
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
    
    // エフェクト5: 空腹の呪い
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
    
    // エフェクト6: 記憶喪失
    public static class MemoryLossEffect extends UnluckyEffectBase {
        public MemoryLossEffect(JavaPlugin plugin) {
            super(plugin, "記憶喪失", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(" ");
            player.sendMessage(ChatColor.DARK_PURPLE + "記憶が曖昧になりました...何をしていたか思い出せません。");
            return getDescription();
        }
    }
    
    // エフェクト7: 炎の雨
    public static class FireRainEffect extends UnluckyEffectBase {
        public FireRainEffect(JavaPlugin plugin) {
            super(plugin, "炎の雨", EffectRarity.RARE);
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
    
    // エフェクト8: 経験値流出
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
    
    // エフェクト9: 武器呪い
    public static class WeaponCurseEffect extends UnluckyEffectBase {
        public WeaponCurseEffect(JavaPlugin plugin) {
            super(plugin, "武器呪い", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (mainHand != null && mainHand.getType() != Material.AIR) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.WOODEN_SWORD));
                player.sendMessage(ChatColor.RED + "武器が呪われた木の剣に変化しました！");
            } else {
                player.sendMessage(ChatColor.YELLOW + "呪う武器がありませんでした。");
            }
            return getDescription();
        }
    }
    
    // エフェクト10: 防具消失
    public static class ArmorVanishEffect extends UnluckyEffectBase {
        public ArmorVanishEffect(JavaPlugin plugin) {
            super(plugin, "防具消失", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            boolean hasArmor = false;
            ItemStack[] armor = player.getInventory().getArmorContents();
            for (int i = 0; i < armor.length; i++) {
                if (armor[i] != null && armor[i].getType() != Material.AIR) {
                    armor[i] = null;
                    hasArmor = true;
                }
            }
            
            if (hasArmor) {
                player.getInventory().setArmorContents(armor);
                player.sendMessage(ChatColor.RED + "防具が消失しました！");
            } else {
                player.sendMessage(ChatColor.YELLOW + "消失する防具がありませんでした。");
            }
            return getDescription();
        }
    }
}