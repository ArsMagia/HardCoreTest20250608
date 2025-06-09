package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShuffleItem extends AbstractCustomItemV2 {

    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "シャッフル";

    public ShuffleItem(JavaPlugin plugin) {
        super(plugin, builder("shuffle", "シャッフル")
                .material(Material.ENDER_PEARL)
                .rarity(ItemRarity.LEGENDARY)
                .addLore("右クリックでプレイヤーと位置・体力交換")
                .addLore("一番近いプレイヤーと位置を交換")
                .addLore("一番遠いプレイヤーと体力を交換")
                .addHint("使用回数: 1回"));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!isCustomItem(item)) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // カスタムアイテムの場合は常にエンダーパールの通常動作をキャンセル
        event.setCancelled(true);
        
        // クールダウンチェック（複数回発動防止）
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUse = lastActivation.get(playerId);
        
        if (lastUse != null && (currentTime - lastUse) < EffectConstants.ITEM_COOLDOWN_MS) {
            // クールダウン中は無言でリターン（メッセージ重複防止）
            return;
        }
        
        // クールダウン設定（複数回発動防止のため早期設定）
        lastActivation.put(playerId, currentTime);

        // 他のオンラインプレイヤーを取得
        List<Player> otherPlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                otherPlayers.add(onlinePlayer);
            }
        }

        // アイテムを消費（成功・失敗に関わらず）
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // 他プレイヤーが0人の場合：失敗エフェクト
        if (otherPlayers.isEmpty()) {
            player.sendMessage(ChatColor.RED + "他のプレイヤーがいないため、シャッフルに失敗しました！");
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 10, 1, 1, 1, 0.1);
            return;
        }

        // 他プレイヤーが1人の場合：その人を対象にすべての効果を発動
        if (otherPlayers.size() == 1) {
            Player targetPlayer = otherPlayers.get(0);
            
            // 同じワールドかチェック
            if (!targetPlayer.getWorld().equals(player.getWorld())) {
                player.sendMessage(ChatColor.RED + "同じワールドに他のプレイヤーがいません！");
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
                player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 10, 1, 1, 1, 0.1);
                return;
            }
            
            // 位置と体力の両方を同じ人と交換
            Location playerLocation = player.getLocation().clone();
            Location targetLocation = targetPlayer.getLocation().clone();
            
            double playerHealth = player.getHealth();
            double targetHealth = targetPlayer.getHealth();
            
            // 位置交換
            player.teleport(targetLocation);
            targetPlayer.teleport(playerLocation);
            
            // 体力交換
            player.setHealth(Math.min(targetHealth, player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
            targetPlayer.setHealth(Math.min(playerHealth, targetPlayer.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
            
            // エフェクトと通知
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            targetPlayer.playSound(targetPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            
            player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.3);
            targetPlayer.getWorld().spawnParticle(Particle.PORTAL, targetPlayer.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.3);
            targetPlayer.getWorld().spawnParticle(Particle.HEART, targetPlayer.getLocation().add(0, 2, 0), 10, 0.5, 0.5, 0.5, 0.1);
            
            Bukkit.broadcastMessage(ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + " 特殊シャッフル発動！ " + ChatColor.MAGIC + "a");
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " と " + 
                    ChatColor.YELLOW + targetPlayer.getName() + ChatColor.GRAY + " が位置と体力を完全交換しました！");
            return;
        }

        // 2人以上の場合：通常の処理
        // 一番近いプレイヤーと一番遠いプレイヤーを見つける
        Player nearestPlayer = null;
        Player farthestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;
        double farthestDistance = 0;
        
        Location playerLoc = player.getLocation();
        
        for (Player other : otherPlayers) {
            if (!other.getWorld().equals(player.getWorld())) continue; // 同じワールドのみ
            
            double distance = playerLoc.distance(other.getLocation());
            
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = other;
            }
            
            if (distance > farthestDistance) {
                farthestDistance = distance;
                farthestPlayer = other;
            }
        }

        if (nearestPlayer == null || farthestPlayer == null) {
            player.sendMessage(ChatColor.RED + "同じワールドに他のプレイヤーがいません！");
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation().add(0, 1, 0), 10, 1, 1, 1, 0.1);
            return;
        }

        // 位置交換（一番近いプレイヤーと）
        Location playerLocation = player.getLocation().clone();
        Location nearestLocation = nearestPlayer.getLocation().clone();
        
        player.teleport(nearestLocation);
        nearestPlayer.teleport(playerLocation);

        // 体力交換（一番遠いプレイヤーと）
        double playerHealth = player.getHealth();
        double farthestHealth = farthestPlayer.getHealth();
        
        player.setHealth(Math.min(farthestHealth, player.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
        farthestPlayer.setHealth(Math.min(playerHealth, farthestPlayer.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));

        // エフェクトと通知
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        nearestPlayer.playSound(nearestPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
        farthestPlayer.playSound(farthestPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
        
        // パーティクル
        player.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.3);
        nearestPlayer.getWorld().spawnParticle(Particle.PORTAL, nearestPlayer.getLocation().add(0, 1, 0), 50, 1, 1, 1, 0.3);
        farthestPlayer.getWorld().spawnParticle(Particle.HEART, farthestPlayer.getLocation().add(0, 2, 0), 10, 0.5, 0.5, 0.5, 0.1);

        // 全プレイヤーに通知
        Bukkit.broadcastMessage(ChatColor.MAGIC + "a" + ChatColor.RESET + ChatColor.GOLD + " シャッフル発動！ " + ChatColor.MAGIC + "a");
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " と " + 
                ChatColor.YELLOW + nearestPlayer.getName() + ChatColor.GRAY + " が位置を交換しました！");
        Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " と " + 
                ChatColor.YELLOW + farthestPlayer.getName() + ChatColor.GRAY + " が体力を交換しました！");

    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) {
            return;
        }
        
        ItemStack item = player.getInventory().getItemInMainHand();
        if (isCustomItem(item) && item.getType() == Material.ENDER_PEARL) {
            // カスタムアイテムのエンダーパール投擲を完全にキャンセル
            event.setCancelled(true);
        }
    }
}