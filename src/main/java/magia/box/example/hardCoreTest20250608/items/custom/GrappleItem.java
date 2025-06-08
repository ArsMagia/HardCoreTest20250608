package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class GrappleItem extends
        AbstractCustomItemV2 {

    private final ArrayList<UUID> grappling = new ArrayList<>();
    private static final int POWER = 5;

    public GrappleItem(JavaPlugin plugin) {
        super(plugin, builder("grapple", "グラップル")
                .material(Material.FISHING_ROD)
                .rarity(ItemRarity.UNCOMMON)
                .addLore("釣り竿でグラップリング！")
                .addLore("地面や壁に向かって使用")
                .addLore("無制限使用可能")
                .addHint("パワー: " + POWER));
    }

    @EventHandler
    public void onGrappleThrow(PlayerFishEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!isCustomItem(itemInHand)) {
            return;
        }

        Vector grapple = event.getHook().getLocation().subtract(player.getLocation()).toVector().normalize().multiply(POWER);
        Entity hookedEntity = event.getHook().getHookedEntity();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (event.getHand() == null) return;
            grappling.add(uuid);
        } else if (grappling.contains(uuid)) {
            switch (event.getState()) {
                case REEL_IN:
                    if (canGrapple(event.getHook().getLocation())) {
                        player.setVelocity(player.getVelocity().add(grapple));
                        // 成功時の音響フィードバック
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.2f);
                    } else {
                        player.sendMessage(ChatColor.RED + "グラップリングできません");
                    }
                    break;
                case IN_GROUND:
                    player.setVelocity(player.getVelocity().add(grapple));
                    // 成功時の音響フィードバック
                    player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.2f);
                    break;
                case CAUGHT_ENTITY:
                    if (hookedEntity == null || hookedEntity.getType() != EntityType.PLAYER) break;
                    if (hookedEntity.getType() == EntityType.PLAYER) {
                        player.setVelocity(player.getVelocity().add(grapple));
                        // 成功時の音響フィードバック
                        player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 1.2f);
                    }
                    break;
                default:
                    break;
            }
            grappling.remove(uuid);
        }
    }
    
    /**
     * グラップリング可能かどうかを判定
     * @param hookLocation フックの位置
     * @return グラップリング可能かどうか
     */
    private boolean canGrapple(Location hookLocation) {
        World world = hookLocation.getWorld();
        if (world == null) return false;
        
        Block hookBlock = hookLocation.getBlock();
        
        // 1. 地面にしっかりと着地している場合
        if (hookBlock.getType() != Material.AIR) {
            return true;
        }
        
        // 2. 水中の場合
        if (hookBlock.getType() == Material.WATER) {
            return true;
        }
        
        // 3. 近接ブロック検出（1ブロック範囲）
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // 中心はスキップ
                    
                    Block nearbyBlock = hookLocation.clone().add(x, y, z).getBlock();
                    
                    // 隣接する固体ブロックがある場合はグラップリング可能
                    if (nearbyBlock.getType() != Material.AIR && 
                        nearbyBlock.getType() != Material.WATER &&
                        nearbyBlock.getType().isSolid()) {
                        return true;
                    }
                }
            }
        }
        
        // どの条件も満たさない場合は不可
        return false;
    }
    
    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (isCustomItem(item)) {
            // グラップルアイテムの耐久消費を無効化
            event.setCancelled(true);
        }
    }
}