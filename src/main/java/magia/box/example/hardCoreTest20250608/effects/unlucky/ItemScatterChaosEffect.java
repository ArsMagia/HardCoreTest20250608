package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemScatterChaosEffect extends UnluckyEffectBase {

    /** 効果の持続時間（秒） */
    private static final int SCATTER_DURATION_SECONDS = 10;
    
    /** アイテム投げ間隔（ティック） */
    private static final int THROW_INTERVAL_TICKS = 10; // 0.5秒間隔
    
    /** 投げる力の強さ */
    private static final double THROW_VELOCITY = 1.5;
    
    /** ランダム生成器 */
    private final Random random = new Random();

    public ItemScatterChaosEffect(JavaPlugin plugin) {
        super(plugin, "アイテム大飛散", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        PlayerInventory inventory = player.getInventory();
        List<ItemStack> itemsToThrow = new ArrayList<>();
        
        // 投げるアイテムを収集（ホットバー左端とアーマーを除く）
        collectItemsToThrow(inventory, itemsToThrow);
        
        if (itemsToThrow.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "💥 アイテム大飛散を試みましたが、投げるアイテムがありませんでした。");
            
            // 軽いエフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 
                    EffectConstants.STANDARD_VOLUME, 0.5f);
            
            return getDescription() + " (効果なし)";
        }
        
        // プレイヤーへの通知
        player.sendMessage(ChatColor.DARK_RED + "💥 アイテム大飛散！あなたの持ち物が四方八方に飛び散ります！");
        player.sendMessage(ChatColor.RED + "⚠ " + itemsToThrow.size() + "個のアイテムが10秒間で投げられます！");
        
        // 初期エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 
                EffectConstants.STANDARD_VOLUME, 0.8f);
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 
                EffectConstants.STANDARD_VOLUME, 1.2f);
        
        player.getWorld().spawnParticle(
            Particle.EXPLOSION,
            player.getLocation().add(0, 1, 0),
            10, 1, 1, 1, 0.2
        );
        
        // アイテム投げタスクを開始
        startItemThrowingTask(player, itemsToThrow);
        
        return getDescription() + " (" + itemsToThrow.size() + "個)";
    }
    
    /**
     * 投げるアイテムを収集
     * @param inventory プレイヤーのインベントリ
     * @param itemsToThrow 収集されたアイテムのリスト
     */
    private void collectItemsToThrow(PlayerInventory inventory, List<ItemStack> itemsToThrow) {
        // メインインベントリ（ホットバー左端 [0] を除く）
        for (int i = 1; i < inventory.getSize(); i++) { // スロット1から開始
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                itemsToThrow.add(item.clone());
                inventory.setItem(i, null); // インベントリから削除
            }
        }
        
        // オフハンドも対象
        ItemStack offhandItem = inventory.getItemInOffHand();
        if (offhandItem != null && offhandItem.getType() != Material.AIR) {
            itemsToThrow.add(offhandItem.clone());
            inventory.setItemInOffHand(new ItemStack(Material.AIR));
        }
        
        // アーマーは除外（要求通り）
    }
    
    /**
     * アイテム投げタスクを開始
     * @param player プレイヤー
     * @param itemsToThrow 投げるアイテムのリスト
     */
    private void startItemThrowingTask(Player player, List<ItemStack> itemsToThrow) {
        new BukkitRunnable() {
            int ticksElapsed = 0;
            int itemsThrown = 0;
            final int maxTicks = SCATTER_DURATION_SECONDS * 20;
            final int totalItems = itemsToThrow.size();
            
            @Override
            public void run() {
                if (ticksElapsed >= maxTicks || itemsThrown >= totalItems) {
                    // 終了処理
                    finishScatterEffect(player, itemsThrown);
                    cancel();
                    return;
                }
                
                // 投げ間隔ごとに実行
                if (ticksElapsed % THROW_INTERVAL_TICKS == 0 && itemsThrown < totalItems) {
                    // 複数アイテムを同時に投げる（スタック分散）
                    int itemsToThrowNow = Math.min(3, totalItems - itemsThrown); // 最大3個まで同時
                    
                    for (int i = 0; i < itemsToThrowNow; i++) {
                        if (itemsThrown < totalItems) {
                            throwSingleItem(player, itemsToThrow.get(itemsThrown));
                            itemsThrown++;
                        }
                    }
                    
                    // 進捗メッセージ
                    if (itemsThrown % 5 == 0 || itemsThrown == totalItems) {
                        player.sendMessage(ChatColor.YELLOW + "📦 " + itemsThrown + "/" + totalItems + " 個のアイテムが飛び散りました...");
                    }
                }
                
                // 継続的なエフェクト
                if (ticksElapsed % 20 == 0) { // 1秒ごと
                    player.getWorld().spawnParticle(
                        Particle.ITEM,
                        player.getLocation().add(0, 1, 0),
                        15, 1, 1, 1, 0.3,
                        new ItemStack(Material.DIAMOND) // エフェクト用アイテム
                    );
                    
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 
                            EffectConstants.STANDARD_VOLUME, 0.5f);
                }
                
                ticksElapsed++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
    
    /**
     * 単一アイテムを投げる
     * @param player プレイヤー
     * @param itemStack 投げるアイテム
     */
    private void throwSingleItem(Player player, ItemStack itemStack) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        
        if (world == null) return;
        
        // ランダムな方向ベクトルを生成（360度）
        double angle = random.nextDouble() * 2 * Math.PI; // 0-2π
        double pitch = (random.nextDouble() - 0.5) * Math.PI * 0.6; // 上下角度（-54度から54度）
        
        Vector velocity = new Vector(
            Math.cos(angle) * Math.cos(pitch) * THROW_VELOCITY,
            Math.sin(pitch) * THROW_VELOCITY + 0.3, // 少し上向きに
            Math.sin(angle) * Math.cos(pitch) * THROW_VELOCITY
        );
        
        // アイテムエンティティを生成
        Location spawnLoc = playerLoc.clone().add(0, 1.5, 0); // プレイヤーの胸の高さ
        Item droppedItem = world.dropItem(spawnLoc, itemStack);
        droppedItem.setVelocity(velocity);
        droppedItem.setPickupDelay(60); // 3秒間拾えない
        
        // 投げエフェクト
        world.spawnParticle(
            Particle.POOF,
            spawnLoc,
            5, 0.2, 0.2, 0.2, 0.1
        );
        
        world.playSound(spawnLoc, Sound.ENTITY_SNOWBALL_THROW, 
                EffectConstants.STANDARD_VOLUME * 0.3f, 
                1.0f + (random.nextFloat() - 0.5f) * 0.4f); // ピッチを少し変動
    }
    
    /**
     * 飛散効果の終了処理
     * @param player プレイヤー
     * @param itemsThrown 投げられたアイテム数
     */
    private void finishScatterEffect(Player player, int itemsThrown) {
        player.sendMessage(ChatColor.GREEN + "💥 アイテム大飛散が終了しました。");
        player.sendMessage(ChatColor.GRAY + "合計 " + itemsThrown + " 個のアイテムが飛び散りました。");
        player.sendMessage(ChatColor.YELLOW + "💡 散らばったアイテムを回収してください！");
        
        // 終了エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, 0.8f);
        
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            20, 1, 1, 1, 0.1
        );
        
        // 周囲にパーティクルリング
        Location playerLoc = player.getLocation();
        for (int i = 0; i < 16; i++) {
            double angle = i * Math.PI * 2 / 16;
            Location ringLoc = playerLoc.clone().add(
                Math.cos(angle) * 3,
                0.5,
                Math.sin(angle) * 3
            );
            
            playerLoc.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                ringLoc,
                3, 0.1, 0.1, 0.1, 0
            );
        }
    }
}