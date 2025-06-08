package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhantomBladeItem extends AbstractCustomItemV2 {

    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "Phantom Blade";
    
    /** 耐久消費量 */
    private static final int DURABILITY_COST = 1;

    public PhantomBladeItem(JavaPlugin plugin) {
        super(plugin, builder("phantom_blade", "Phantom Blade")
                .material(Material.IRON_SWORD)
                .rarity(ItemRarity.RARE)
                .addLore("右クリックで前方ダッシュ")
                .addLore("ダッシュ力: 強力")
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

        // ダッシュ効果を実行
        executeDashEffect(player);
        
        // 耐久力消費または破壊
        EffectUtils.consumeDurabilityOrBreak(player, item, EffectConstants.PHANTOM_BLADE_DURABILITY_COST, ITEM_NAME);
    }
    
    /**
     * ダッシュ効果を実行
     * @param player 対象プレイヤー
     */
    private void executeDashEffect(Player player) {
        // 前方ダッシュベクトル計算
        Vector direction = player.getLocation().getDirection();
        direction.setY(0.3); // 少し上向きに
        direction = direction.normalize().multiply(EffectConstants.PHANTOM_BLADE_DASH_POWER);
        
        // ダッシュ実行
        player.setVelocity(direction);
        
        // エフェクトとメッセージ
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            player.getLocation().add(0, 1, 0),
            30, 0.5, 0.5, 0.5, 0.2
        );
        
        player.sendMessage(ChatColor.DARK_PURPLE + ITEM_NAME + "でダッシュしました！");
    }
}