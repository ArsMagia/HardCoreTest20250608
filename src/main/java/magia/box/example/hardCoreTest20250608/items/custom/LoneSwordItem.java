package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoneSwordItem extends AbstractCustomItemV2 {

    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "Lone Sword";
    
    /** 耐久消費量 */
    private static final int DURABILITY_COST = 1;

    public LoneSwordItem(JavaPlugin plugin) {
        super(plugin, builder("lone_sword", "Lone Sword")
                .material(Material.IRON_SWORD)
                .rarity(ItemRarity.RARE)
                .addLore("右クリックで体力回復")
                .addLore("回復量: 2ハート")
                .addHint("耐久力消費: " + DURABILITY_COST));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // クールダウンチェック
        if (EffectUtils.checkCooldown(player, lastActivation.get(player.getUniqueId()), 
                EffectConstants.ITEM_COOLDOWN_MS, ITEM_NAME)) {
            return;
        }
        lastActivation.put(player.getUniqueId(), System.currentTimeMillis());

        if (!isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);

        // 体力回復効果を実行
        executeHealingEffect(player);
        
        // 耐久力消費または破壊
        EffectUtils.consumeDurabilityOrBreak(player, item, EffectConstants.LONE_SWORD_DURABILITY_COST, ITEM_NAME);
    }
    
    /**
     * 体力回復効果を実行
     * @param player 対象プレイヤー
     */
    private void executeHealingEffect(Player player) {
        double currentHealth = player.getHealth();
        double maxHealth = EffectUtils.getMaxHealth(player);
        
        if (maxHealth <= 0) {
            player.sendMessage(ChatColor.RED + "体力情報を取得できませんでした。");
            return;
        }
        
        double newHealth = Math.min(currentHealth + EffectConstants.LONE_SWORD_HEAL_AMOUNT, maxHealth);
        player.setHealth(newHealth);
        
        // エフェクトとメッセージ
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        player.sendMessage(ChatColor.GREEN + ITEM_NAME + "で体力を回復しました！");
    }
}