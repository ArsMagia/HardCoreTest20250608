package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class ItemScatterEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public ItemScatterEffect(JavaPlugin plugin) {
        super(plugin, "アイテム分散", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // ホットバーの一番左以外（スロット1-8）とメインインベントリ（スロット9-35）、防具・オフハンド（36-40）から対象を収集
        java.util.List<Integer> availableSlots = new java.util.ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();
        
        // ホットバーの1番目～8番目（スロット1-8）
        for (int i = 1; i <= 8; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR) {
                availableSlots.add(i);
            }
        }
        
        // メインインベントリ（スロット9-35）
        for (int i = 9; i <= 35; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR) {
                availableSlots.add(i);
            }
        }
        
        // 防具スロット（スロット36-39）とオフハンド（スロット40）
        for (int i = 36; i <= 40; i++) {
            if (i < contents.length && contents[i] != null && contents[i].getType() != Material.AIR) {
                availableSlots.add(i);
            }
        }
        
        // ランダム3枠のアイテムを選択して正面に飛ばす
        int maxScatter = Math.min(3, availableSlots.size());
        java.util.Collections.shuffle(availableSlots);
        int scatteredCount = 0;
        
        // プレイヤーの正面方向を取得
        org.bukkit.util.Vector direction = player.getLocation().getDirection();
        direction.setY(0.3); // 少し上向きに
        direction = direction.normalize().multiply(1.5); // 速度調整
        
        for (int i = 0; i < maxScatter; i++) {
            int slotIndex = availableSlots.get(i);
            ItemStack item = contents[slotIndex];
            
            if (item != null && item.getType() != Material.AIR) {
                // プレイヤーの正面にドロップ
                Location dropLoc = player.getLocation().add(0, 1, 0);
                
                org.bukkit.entity.Item droppedItem = player.getWorld().dropItemNaturally(dropLoc, item);
                droppedItem.setGlowing(true);
                droppedItem.setVelocity(direction); // 正面方向に飛ばす
                
                player.getInventory().setItem(slotIndex, null);
                scatteredCount++;
                
                dropLoc.getWorld().spawnParticle(
                    Particle.ITEM,
                    dropLoc,
                    8, 0.3, 0.3, 0.3, 0.15,
                    item
                );
            }
        }
        
        if (scatteredCount > 0) {
            player.sendMessage(ChatColor.RED + "💥 アイテム分散！" + scatteredCount + "個のアイテムが正面に飛び出しました！");
            player.sendMessage(ChatColor.GRAY + "（ホットバーの一番左のアイテムは安全です）");
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);
            
            // より派手な爆発エフェクト
            player.getLocation().getWorld().spawnParticle(
                Particle.EXPLOSION,
                player.getLocation().add(0, 1, 0),
                5, 1.5, 1.5, 1.5, 0.2
            );
            
            player.getLocation().getWorld().spawnParticle(
                Particle.SMOKE,
                player.getLocation().add(0, 1, 0),
                15, 1, 1, 1, 0.1
            );
        } else {
            player.sendMessage(ChatColor.YELLOW + "散らばるアイテムがありませんでした。代わりに強烈な混乱効果を受けます。");
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.NAUSEA, 300, 2));
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.SLOWNESS, 200, 1));
        }
        
        return getDescription();
    }
}