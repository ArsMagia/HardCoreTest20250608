package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerTrackingCompassItem extends AbstractCustomItemV2 {

    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "プレイヤー追跡コンパス";
    
    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** プレイヤーごとの追跡対象管理 */
    private static final Map<UUID, String> trackingTargets = new HashMap<>();
    
    /** 更新タスク管理 */
    private static final Map<UUID, BukkitRunnable> updateTasks = new HashMap<>();

    public PlayerTrackingCompassItem(JavaPlugin plugin) {
        super(plugin, builder("player_tracking_compass", "プレイヤー追跡コンパス")
                .material(Material.COMPASS)
                .rarity(ItemRarity.RARE)
                .addLore("他プレイヤーの位置を追跡")
                .addLore("右クリック: 対象プレイヤー変更")
                .addLore("左クリック: 座標通知")
                .addHint("範囲: 同一ディメンション"));
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta meta = item.getItemMeta();
        
        if (meta instanceof CompassMeta compassMeta) {
            // 初期状態では方角なし（ぐるぐる回る）
            compassMeta.setLodestone(null);
            compassMeta.setLodestoneTracked(false);
            item.setItemMeta(compassMeta);
        }
        
        return item;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // カスタムアイテムチェックを先に実行
        if (!isCustomItem(item)) {
            return;
        }

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

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // 右クリック: 対象プレイヤー変更
            switchTrackingTarget(player, item);
        } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // 左クリック: 座標通知
            notifyTargetLocation(player);
        }
    }
    
    /**
     * 追跡対象プレイヤーを切り替え
     * @param player コンパスを使用するプレイヤー
     * @param compass コンパスアイテム
     */
    private void switchTrackingTarget(Player player, ItemStack compass) {
        List<Player> onlinePlayers = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.equals(player)) {
                onlinePlayers.add(onlinePlayer);
            }
        }
        
        if (onlinePlayers.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "📍 他にオンラインのプレイヤーがいません。");
            setCompassTarget(player, compass, null);
            return;
        }
        
        // 現在の対象を取得
        String currentTarget = trackingTargets.get(player.getUniqueId());
        int currentIndex = -1;
        
        // 現在の対象のインデックスを見つける
        for (int i = 0; i < onlinePlayers.size(); i++) {
            if (onlinePlayers.get(i).getName().equals(currentTarget)) {
                currentIndex = i;
                break;
            }
        }
        
        // 次の対象を選択
        int nextIndex = (currentIndex + 1) % onlinePlayers.size();
        Player targetPlayer = onlinePlayers.get(nextIndex);
        
        // 追跡対象を設定
        trackingTargets.put(player.getUniqueId(), targetPlayer.getName());
        setCompassTarget(player, compass, targetPlayer);
        
        // メッセージとエフェクト
        player.sendMessage(ChatColor.GREEN + "📍 追跡対象: " + ChatColor.AQUA + targetPlayer.getName());
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
        
        // 自動更新タスクを開始
        startCompassUpdateTask(player, compass);
    }
    
    /**
     * 対象プレイヤーの座標を通知
     * @param player コンパスを使用するプレイヤー
     */
    private void notifyTargetLocation(Player player) {
        String targetName = trackingTargets.get(player.getUniqueId());
        
        if (targetName == null) {
            player.sendMessage(ChatColor.RED + "📍 追跡対象が設定されていません。右クリックで対象を選択してください。");
            return;
        }
        
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(ChatColor.RED + "📍 " + targetName + " はオフラインです。");
            return;
        }
        
        if (!targetPlayer.getWorld().equals(player.getWorld())) {
            player.sendMessage(ChatColor.RED + "📍 " + targetName + " は別のディメンションにいます。");
            return;
        }
        
        Location targetLoc = targetPlayer.getLocation();
        int distance = (int) player.getLocation().distance(targetLoc);
        
        // 座標情報をクリック可能な形式で送信
        player.sendMessage(ChatColor.GREEN + "📍 " + ChatColor.AQUA + targetName + ChatColor.GREEN + " の位置:");
        
        TextComponent locationMessage = new TextComponent(
            ChatColor.YELLOW + "座標: " + ChatColor.WHITE + 
            (int)targetLoc.getX() + ", " + (int)targetLoc.getY() + ", " + (int)targetLoc.getZ() + 
            ChatColor.GRAY + " (距離: " + distance + "m)"
        );
        locationMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
            "/tp " + player.getName() + " " + (int)targetLoc.getX() + " " + (int)targetLoc.getY() + " " + (int)targetLoc.getZ()));
        locationMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ChatColor.GREEN + "クリックでテレポート\n" + 
                ChatColor.GRAY + "※OP権限が必要です").create()));
        
        player.spigot().sendMessage(locationMessage);
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 
                EffectConstants.STANDARD_VOLUME, 1.5f);
        
        player.getWorld().spawnParticle(
            Particle.COMPOSTER,
            player.getLocation().add(0, 2, 0),
            10, 0.5, 0.5, 0.5, 0.1
        );
    }
    
    /**
     * コンパスの追跡対象を設定
     * @param player コンパスを使用するプレイヤー
     * @param compass コンパスアイテム
     * @param target 追跡対象プレイヤー（nullの場合はリセット）
     */
    private void setCompassTarget(Player player, ItemStack compass, Player target) {
        ItemMeta meta = compass.getItemMeta();
        if (!(meta instanceof CompassMeta compassMeta)) return;
        
        if (target == null || !target.isOnline() || !target.getWorld().equals(player.getWorld())) {
            // 追跡不可能な場合はロードストーンをクリア（ぐるぐる回る）
            compassMeta.setLodestone(null);
            compassMeta.setLodestoneTracked(false);
            
            // アイテム名を更新
            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                // 最初の行を更新（対象情報）
                lore.set(0, ChatColor.GRAY + "対象: " + ChatColor.RED + "なし");
                compassMeta.setLore(lore);
            }
        } else {
            // 追跡可能な場合は対象の位置にロードストーンを設定
            Location targetLoc = target.getLocation();
            compassMeta.setLodestone(targetLoc);
            compassMeta.setLodestoneTracked(false); // 実際のロードストーンは不要
            
            // アイテム名を更新
            List<String> lore = meta.getLore();
            if (lore != null && !lore.isEmpty()) {
                // 最初の行を更新（対象情報）
                lore.set(0, ChatColor.GRAY + "対象: " + ChatColor.AQUA + target.getName());
                compassMeta.setLore(lore);
            }
        }
        
        compass.setItemMeta(compassMeta);
    }
    
    /**
     * コンパス自動更新タスクを開始
     * @param player コンパスを使用するプレイヤー
     * @param compass コンパスアイテム
     */
    private void startCompassUpdateTask(Player player, ItemStack compass) {
        // 既存のタスクがあればキャンセル
        BukkitRunnable existingTask = updateTasks.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
        }
        
        // 新しい更新タスクを作成
        BukkitRunnable updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                String targetName = trackingTargets.get(player.getUniqueId());
                if (targetName == null) {
                    cancel();
                    return;
                }
                
                // プレイヤーがオフラインの場合はタスク終了
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                // コンパスを持っているかチェック
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                if (!isCustomItem(currentItem)) {
                    // オフハンドもチェック
                    currentItem = player.getInventory().getItemInOffHand();
                    if (!isCustomItem(currentItem)) {
                        return; // コンパスを持っていない場合は更新スキップ
                    }
                }
                
                Player targetPlayer = Bukkit.getPlayer(targetName);
                setCompassTarget(player, currentItem, targetPlayer);
            }
        };
        
        updateTasks.put(player.getUniqueId(), updateTask);
        updateTask.runTaskTimer(plugin, 0L, 20L); // 1秒ごとに更新
    }
    
    /**
     * プレイヤーがオフラインになった際のクリーンアップ
     * @param playerUUID プレイヤーのUUID
     */
    public static void cleanupPlayer(UUID playerUUID) {
        trackingTargets.remove(playerUUID);
        
        BukkitRunnable task = updateTasks.remove(playerUUID);
        if (task != null) {
            task.cancel();
        }
    }
}