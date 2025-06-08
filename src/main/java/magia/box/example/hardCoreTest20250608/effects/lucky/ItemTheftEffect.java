package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemTheftEffect extends LuckyEffectBase {

    private final Random random = new Random();

    public ItemTheftEffect(JavaPlugin plugin) {
        super(plugin, "アイテム窃盗", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // 他のプレイヤーを取得
        List<Player> otherPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                otherPlayers.add(onlinePlayer);
            }
        }

        // 他のプレイヤーがいない場合はラッキーボックスを1つ取得
        if (otherPlayers.isEmpty()) {
            return giveAlternateLuckyBox(player);
        }

        // ランダムなプレイヤーを選択
        Player targetPlayer = otherPlayers.get(random.nextInt(otherPlayers.size()));
        
        // 対象プレイヤーの質の良いアイテムを探す
        ItemStack stolenItem = findBestItem(targetPlayer);
        
        if (stolenItem == null) {
            player.sendMessage(ChatColor.YELLOW + targetPlayer.getName() + " から盗むアイテムがありませんでした...");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return "窃盗失敗";
        }

        // アイテムを盗む
        removeItemFromPlayer(targetPlayer, stolenItem);
        
        // プレイヤーにアイテムを渡す
        String itemDisplayName = EffectUtils.getItemDisplayName(stolenItem);
        boolean success = EffectUtils.safeGiveItem(player, stolenItem);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " から " + 
                ChatColor.AQUA + itemDisplayName + ChatColor.GREEN + " を盗みました！");
        } else {
            player.sendMessage(ChatColor.GREEN + targetPlayer.getName() + " から " + 
                ChatColor.AQUA + itemDisplayName + ChatColor.GREEN + 
                " を盗みました！インベントリが満杯のため足元にドロップしました。");
        }
        
        // 被害者への通知とエフェクト
        notifyVictimAndPlayEffects(player, targetPlayer, itemDisplayName);

        return getDescription() + " (" + itemDisplayName + ")";
    }
    
    /**
     * 代替ラッキーボックスを与える
     */
    private String giveAlternateLuckyBox(Player player) {
        ItemStack luckyBox = ItemRegistryAccessor.getLuckyBoxItem().createItem();
        boolean success = EffectUtils.safeGiveItem(player, luckyBox);
        
        if (success) {
            player.sendMessage(ChatColor.GOLD + "誰もいないので代わりにラッキーボックスを獲得しました！");
        } else {
            player.sendMessage(ChatColor.GOLD + "誰もいないので代わりにラッキーボックスを獲得！インベントリが満杯のため足元にドロップしました。");
        }
        
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        player.getWorld().spawnParticle(
            Particle.HAPPY_VILLAGER,
            player.getLocation().add(0, 1, 0),
            15, 1, 1, 1, 0.1
        );
        
        return "ラッキーボックス獲得";
    }
    
    /**
     * 被害者への通知とエフェクト
     */
    private void notifyVictimAndPlayEffects(Player thief, Player victim, String itemName) {
        victim.sendMessage(ChatColor.RED + thief.getName() + " があなたの " + 
            ChatColor.AQUA + itemName + ChatColor.RED + " を盗みました！");
        
        // エフェクト再生
        thief.playSound(thief.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
        victim.playSound(victim.getLocation(), Sound.ENTITY_VILLAGER_HURT, 
                EffectConstants.STANDARD_VOLUME, 0.8f);
        
        thief.getWorld().spawnParticle(
            Particle.HAPPY_VILLAGER,
            thief.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1
        );
        
        victim.getWorld().spawnParticle(
            Particle.SMOKE,
            victim.getLocation().add(0, 1, 0),
            15, 1, 1, 1, 0.1
        );
    }

    private ItemStack findBestItem(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack bestItem = null;
        int bestScore = 0;

        // インベントリ全体をチェック
        for (ItemStack item : inventory.getContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            
            int score = calculateItemValue(item);
            if (score > bestScore) {
                bestScore = score;
                bestItem = item.clone();
            }
        }

        // 防具も確認
        for (ItemStack item : inventory.getArmorContents()) {
            if (item == null || item.getType() == Material.AIR) continue;
            
            int score = calculateItemValue(item);
            if (score > bestScore) {
                bestScore = score;
                bestItem = item.clone();
            }
        }

        // オフハンドも確認
        ItemStack offhand = inventory.getItemInOffHand();
        if (offhand != null && offhand.getType() != Material.AIR) {
            int score = calculateItemValue(offhand);
            if (score > bestScore) {
                bestItem = offhand.clone();
            }
        }

        return bestItem;
    }

    /**
     * アイテムの価値を算出
     * @param item 対象アイテム
     * @return 価値スコア
     */
    private int calculateItemValue(ItemStack item) {
        int score = 0;
        Material type = item.getType();
        String typeName = type.name();
        
        // 基本材料による価値
        if (typeName.contains("DIAMOND")) score += EffectConstants.ITEM_VALUE_DIAMOND;
        else if (typeName.contains("NETHERITE")) score += EffectConstants.ITEM_VALUE_NETHERITE;
        else if (typeName.contains("EMERALD")) score += EffectConstants.ITEM_VALUE_EMERALD;
        else if (typeName.contains("GOLD")) score += EffectConstants.ITEM_VALUE_GOLD;
        else if (typeName.contains("IRON")) score += EffectConstants.ITEM_VALUE_IRON;
        
        // アイテムタイプによる価値
        if (isWeaponOrTool(typeName)) {
            score += EffectConstants.ITEM_VALUE_WEAPON_TOOL;
        } else if (isArmor(typeName)) {
            score += EffectConstants.ITEM_VALUE_ARMOR;
        }
        
        // エンチャントによる価値
        if (item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
            score += item.getItemMeta().getEnchants().size() * EffectConstants.ITEM_VALUE_ENCHANT;
        }
        
        // カスタム名による価値
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            score += EffectConstants.ITEM_VALUE_CUSTOM_NAME;
        }
        
        // スタック数による価値
        score += item.getAmount() * EffectConstants.ITEM_VALUE_PER_STACK;
        
        // 特別なアイテム
        if (isSpecialItem(type)) {
            score += EffectConstants.ITEM_VALUE_SPECIAL;
        }
        
        return score;
    }
    
    /**
     * 武器・ツールかどうかチェック
     */
    private boolean isWeaponOrTool(String typeName) {
        return typeName.contains("SWORD") || typeName.contains("AXE") || 
               typeName.contains("PICKAXE") || typeName.contains("SHOVEL");
    }
    
    /**
     * 防具かどうかチェック
     */
    private boolean isArmor(String typeName) {
        return typeName.contains("HELMET") || typeName.contains("CHESTPLATE") || 
               typeName.contains("LEGGINGS") || typeName.contains("BOOTS");
    }
    
    /**
     * 特別アイテムかどうかチェック
     */
    private boolean isSpecialItem(Material type) {
        return type == Material.NETHER_STAR || type == Material.ENDER_PEARL || 
               type == Material.TOTEM_OF_UNDYING;
    }

    private void removeItemFromPlayer(Player player, ItemStack targetItem) {
        PlayerInventory inventory = player.getInventory();
        
        // メインインベントリから削除を試行
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.isSimilar(targetItem)) {
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    inventory.setItem(i, null);
                }
                return;
            }
        }
        
        // 防具から削除を試行
        ItemStack[] armor = inventory.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && armor[i].isSimilar(targetItem)) {
                armor[i] = null;
                inventory.setArmorContents(armor);
                return;
            }
        }
        
        // オフハンドから削除を試行
        if (inventory.getItemInOffHand().isSimilar(targetItem)) {
            if (inventory.getItemInOffHand().getAmount() > 1) {
                inventory.getItemInOffHand().setAmount(inventory.getItemInOffHand().getAmount() - 1);
            } else {
                inventory.setItemInOffHand(new ItemStack(Material.AIR));
            }
        }
    }

    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        
        String materialName = item.getType().name().toLowerCase().replace('_', ' ');
        return materialName.substring(0, 1).toUpperCase() + materialName.substring(1);
    }
}