package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 段階的腐敗システム
 * フェーズ1: 食料スタック数50%削減
 * フェーズ2: 30秒後に残り食料を腐肉化
 * 安全措置: 最低1個は残し、時間内消費で回避可能
 */
public class FoodDecayEffect extends UnluckyEffectBase {

    public FoodDecayEffect(JavaPlugin plugin) {
        super(plugin, "腐敗の呪い", EffectRarity.UNCOMMON);
    }

    private final Random random = new Random();

    @Override
    public String apply(Player player) {
        // インベントリのランダム3スロット分の食料をそれぞれ残り3つにし、消したアイテムの合計数のゾンビ肉を足元へドロップ
        List<Integer> foodSlots = new ArrayList<>();
        
        // 食料アイテムのスロットを収集
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) {
                foodSlots.add(i);
            }
        }
        
        if (foodSlots.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "腐る食料がありませんでした。");
            return getDescription();
        }
        
        // ランダム3スロット選択
        Collections.shuffle(foodSlots);
        int slotsToProcess = Math.min(3, foodSlots.size());
        int totalRemovedItems = 0;
        
        for (int i = 0; i < slotsToProcess; i++) {
            int slotIndex = foodSlots.get(i);
            ItemStack item = player.getInventory().getItem(slotIndex);
            if (item != null && item.getAmount() > 3) {
                int removedAmount = item.getAmount() - 3;
                totalRemovedItems += removedAmount;
                item.setAmount(3);
            }
        }
        
        // 消したアイテムの合計数のゾンビ肉を足元へドロップ
        if (totalRemovedItems > 0) {
            ItemStack rottenFlesh = new ItemStack(Material.ROTTEN_FLESH, totalRemovedItems);
            player.getWorld().dropItemNaturally(player.getLocation(), rottenFlesh);
            
            player.sendMessage(ChatColor.YELLOW + "食料が腐敗し始めました！" + totalRemovedItems + "個のゾンビ肉が足元に落ちました。");
            
            // 残り全ても残っていれば5秒後にゾンビ肉へ変化
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        rotRemainingFood(player);
                    }
                }
            }.runTaskLater(plugin, 5 * 20L); // 5秒後
        } else {
            player.sendMessage(ChatColor.YELLOW + "食料の量が少ないため、腐敗効果は軽微でした。");
        }
        
        return getDescription();
    }
    
    /**
     * 食料スタック数を50%削減
     * @param player 対象プレイヤー
     * @return 削減された食料種類数
     */
    private int reduceFoodStacks(Player player) {
        int reducedCount = 0;
        
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) {
                int currentAmount = item.getAmount();
                int newAmount = Math.max(1, currentAmount / 2); // 最低1個は残す
                
                if (newAmount < currentAmount) {
                    item.setAmount(newAmount);
                    reducedCount++;
                }
            }
        }
        
        return reducedCount;
    }
    
    /**
     * 残りの食料を腐肉に変化させる
     * @param player 対象プレイヤー
     */
    private void rotRemainingFood(Player player) {
        int rottedCount = 0;
        List<String> rottedItems = new ArrayList<>();
        
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType().isEdible() && item.getType() != Material.ROTTEN_FLESH) {
                // 食料名を記録（日本語表示用）
                String itemName = getFoodDisplayName(item.getType());
                if (!rottedItems.contains(itemName)) {
                    rottedItems.add(itemName);
                }
                
                int amount = item.getAmount();
                player.getInventory().setItem(i, new ItemStack(Material.ROTTEN_FLESH, amount));
                rottedCount++;
            }
        }
        
        if (rottedCount > 0) {
            player.sendMessage(ChatColor.DARK_GREEN + "🦴 残りの食料がすべて腐肉に変化しました...");
            player.sendMessage(ChatColor.GRAY + "腐敗した食料: " + String.join(", ", rottedItems));
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 0.8f);
        } else {
            player.sendMessage(ChatColor.GREEN + "✅ 時間内に食料を消費したようですね。腐敗を免れました！");
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.2f);
        }
    }
    
    /**
     * 食料アイテムの日本語表示名を取得
     * @param material 食料アイテム
     * @return 日本語表示名
     */
    private String getFoodDisplayName(Material material) {
        switch (material) {
            case BREAD: return "パン";
            case APPLE: return "リンゴ";
            case COOKED_BEEF: return "焼き牛肉";
            case COOKED_PORKCHOP: return "焼き豚肉";
            case COOKED_CHICKEN: return "焼き鳥";
            case COOKED_SALMON: return "焼きサーモン";
            case COOKED_COD: return "焼きタラ";
            case BAKED_POTATO: return "ベイクドポテト";
            case CARROT: return "ニンジン";
            case POTATO: return "ジャガイモ";
            case BEETROOT: return "ビートルート";
            case MELON_SLICE: return "スイカ";
            case SWEET_BERRIES: return "スイートベリー";
            case GOLDEN_APPLE: return "金のリンゴ";
            case GOLDEN_CARROT: return "金のニンジン";
            default: return material.name().toLowerCase().replace("_", " ");
        }
    }
}