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
        // フェーズ1: 食料スタック数を50%削減
        int reducedCount = reduceFoodStacks(player);
        
        if (reducedCount > 0) {
            player.sendMessage(ChatColor.YELLOW + "⚠ 食料が傷み始めています..." + reducedCount + "種類の食料が減少しました。");
            player.sendMessage(ChatColor.GOLD + "💡 30秒後に残りの食料が腐敗します！今のうちに消費してください！");
            player.playSound(player.getLocation(), Sound.BLOCK_COMPOSTER_FILL, 1.0f, 0.8f);
            
            // フェーズ2: 30秒後に残り食料を腐肉化
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        rotRemainingFood(player);
                    }
                }
            }.runTaskLater(plugin, 30 * 20L); // 30秒後
            
        } else {
            // 食料がない場合の代替効果（安全措置）
            player.sendMessage(ChatColor.YELLOW + "腐る食料がありませんでした。軽微な空腹効果を受けます。");
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 0)); // レベル0、10秒間
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