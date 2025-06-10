package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class TreasureRainEffect extends LuckyEffectBase {
    
    private final Random random = new Random();

    public TreasureRainEffect(JavaPlugin plugin) {
        super(plugin, "宝の雨", EffectRarity.LEGENDARY);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.GOLD + "宝の雨が降り始めました！30秒間、空からアイテムが降ってきます。");
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.2f);
        
        Material[] treasures = {
            Material.DIAMOND, Material.EMERALD, Material.GOLD_INGOT,
            Material.IRON_INGOT, Material.REDSTONE, Material.LAPIS_LAZULI,
            Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE,
            Material.ENDER_PEARL, Material.BLAZE_ROD, Material.GHAST_TEAR
        };
        
        new BukkitRunnable() {
            int counter = 0;
            int totalItemsDropped = 0;
            final int maxItems = 50; // アイテム数制限
            
            @Override
            public void run() {
                if (counter >= 30 || !player.isOnline() || totalItemsDropped >= maxItems) {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GRAY + "宝の雨が止みました。");
                        if (totalItemsDropped >= maxItems) {
                            player.sendMessage(ChatColor.YELLOW + "（パフォーマンス保護により制限に達しました）");
                        }
                    }
                    this.cancel();
                    return;
                }
                
                // 毎回ではなく、確率的にアイテムをドロップ
                if (random.nextInt(100) < 80) { // 80%の確率
                    Location dropLoc = player.getLocation().add(
                        random.nextInt(10) - 5,
                        10 + random.nextInt(5),
                        random.nextInt(10) - 5
                    );
                    
                    Material treasure = treasures[random.nextInt(treasures.length)];
                    int amount = treasure == Material.DIAMOND || treasure == Material.EMERALD ? 
                        1 + random.nextInt(2) : 1 + random.nextInt(4);
                    
                    ItemStack item = new ItemStack(treasure, amount);
                    player.getWorld().dropItemNaturally(dropLoc, item);
                    totalItemsDropped++;
                    
                    // パーティクル削減（5分の1に）
                    if (totalItemsDropped % 5 == 0) {
                        dropLoc.getWorld().spawnParticle(
                            Particle.TOTEM_OF_UNDYING,
                            dropLoc,
                            5, 0.5, 0.5, 0.5, 0.05
                        );
                    }
                }
                
                // 音響効果も削減
                if (counter % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.5f);
                }
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 20L);
        
        return getDescription();
    }
}