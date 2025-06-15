package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EffectRegistration(
    id = "hand_tremor",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class HandTremorEffect extends UnluckyEffectBase {
    
    private static final Set<UUID> affectedPlayers = new HashSet<>();
    private static final long EFFECT_DURATION = 600L; // 30秒間
    private final Random random = new Random();

    public HandTremorEffect(JavaPlugin plugin) {
        super(plugin, "手汗症", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (affectedPlayers.contains(playerId)) {
            player.sendMessage(ChatColor.YELLOW + "💧 既に手汗症の影響を受けています。");
            return getDescription();
        }
        
        affectedPlayers.add(playerId);
        
        player.sendMessage(ChatColor.RED + "💦 手汗症が発症しました！手が滑ってアイテムを落としやすくなります...");
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.5f);
        
        // 手汗の視覚効果
        spawnSweatParticles(player);
        
        // 定期的にアイテムドロップチェック（2秒間隔で15回）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (!affectedPlayers.contains(playerId) || !player.isOnline() || count >= 15) {
                    this.cancel();
                    return;
                }
                
                checkForItemDrop(player);
                
                // 手汗の視覚効果を定期的に表示
                if (count % 3 == 0) {
                    spawnSweatParticles(player);
                }
                
                count++;
            }
        }.runTaskTimer(plugin, 40L, 40L); // 2秒間隔
        
        // 30秒後に効果を解除
        new BukkitRunnable() {
            @Override
            public void run() {
                removeEffect(player);
            }
        }.runTaskLater(plugin, EFFECT_DURATION);
        
        return getDescription();
    }
    
    private void removeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        affectedPlayers.remove(playerId);
        
        if (player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "🧽 手汗症が治癒しました。手が乾いてアイテムがしっかり握れます！");
            player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.5f);
            
            // 回復エフェクト
            player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                15, 0.5, 0.5, 0.5, 0.1
            );
        }
    }
    
    private void spawnSweatParticles(Player player) {
        if (!player.isOnline()) return;
        
        // 手の位置（プレイヤーの少し前方）で汗のパーティクル
        org.bukkit.Location handLocation = player.getLocation()
            .add(player.getLocation().getDirection().multiply(0.5))
            .add(0, 1.2, 0);
        
        // 汗の滴を表現
        player.getWorld().spawnParticle(
            Particle.DRIPPING_WATER,
            handLocation,
            8, 0.3, 0.2, 0.3, 0.1
        );
        
        player.getWorld().spawnParticle(
            Particle.DRIPPING_WATER,
            handLocation.add(0, -0.3, 0),
            5, 0.2, 0.1, 0.2, 0.1
        );
    }
    
    private void checkForItemDrop(Player player) {
        if (!player.isOnline()) return;
        
        // 30%の確率でアイテムドロップチェック
        if (random.nextInt(100) >= 30) {
            return;
        }
        
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        
        boolean droppedSomething = false;
        
        // メインハンドのアイテムをチェック（70%の確率）
        if (mainHandItem != null && mainHandItem.getType() != Material.AIR) {
            if (random.nextInt(100) < 70) {
                dropItemFromHand(player, mainHandItem, true);
                droppedSomething = true;
            }
        }
        
        // オフハンドのアイテムをチェック（50%の確率）
        if (offHandItem != null && offHandItem.getType() != Material.AIR) {
            if (random.nextInt(100) < 50) {
                dropItemFromHand(player, offHandItem, false);
                droppedSomething = true;
            }
        }
        
        // アイテムが何もなければ、インベントリからランダムに1個ドロップ（20%の確率）
        if (!droppedSomething && random.nextInt(100) < 20) {
            dropRandomInventoryItem(player);
        }
    }
    
    private void dropItemFromHand(Player player, ItemStack item, boolean isMainHand) {
        // アイテムの一部（1個または最大スタック数の10%）をドロップ
        int dropAmount = Math.max(1, Math.min(item.getAmount(), Math.max(1, item.getMaxStackSize() / 10)));
        
        ItemStack dropItem = item.clone();
        dropItem.setAmount(dropAmount);
        
        // 元のアイテムから数量を減らす
        if (item.getAmount() <= dropAmount) {
            // 全てドロップする場合
            if (isMainHand) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            } else {
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
            }
        } else {
            // 一部だけドロップする場合
            item.setAmount(item.getAmount() - dropAmount);
            if (isMainHand) {
                player.getInventory().setItemInMainHand(item);
            } else {
                player.getInventory().setItemInOffHand(item);
            }
        }
        
        // アイテムをドロップ
        org.bukkit.entity.Item droppedItem = player.getWorld().dropItemNaturally(
            player.getLocation().add(0, 1, 0), dropItem
        );
        droppedItem.setVelocity(droppedItem.getVelocity().multiply(0.5)); // ゆっくり落下
        
        // 滑り落ちるメッセージと効果
        String handName = isMainHand ? "メインハンド" : "オフハンド";
        player.sendMessage(ChatColor.YELLOW + "💦 手汗で" + handName + "から" + 
                         dropItem.getType().name() + " x" + dropAmount + "が滑り落ちました！");
        
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.5f, 0.7f);
        
        // ドロップ時のパーティクル
        spawnSweatParticles(player);
        player.getWorld().spawnParticle(
            Particle.ITEM,
            player.getLocation().add(0, 1.5, 0),
            5, 0.3, 0.3, 0.3, 0.1,
            dropItem
        );
    }
    
    private void dropRandomInventoryItem(Player player) {
        ItemStack[] contents = player.getInventory().getContents();
        java.util.List<Integer> availableSlots = new java.util.ArrayList<>();
        
        // 空でないスロットを収集（ホットバー含む）
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null && contents[i].getType() != Material.AIR) {
                availableSlots.add(i);
            }
        }
        
        if (availableSlots.isEmpty()) {
            return;
        }
        
        // ランダムなスロットを選択
        int randomSlot = availableSlots.get(random.nextInt(availableSlots.size()));
        ItemStack item = contents[randomSlot];
        
        // 1個だけドロップ
        ItemStack dropItem = item.clone();
        dropItem.setAmount(1);
        
        // 元のアイテムから1個減らす
        if (item.getAmount() <= 1) {
            player.getInventory().setItem(randomSlot, new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount() - 1);
            player.getInventory().setItem(randomSlot, item);
        }
        
        // アイテムをドロップ
        org.bukkit.entity.Item droppedItem = player.getWorld().dropItemNaturally(
            player.getLocation().add(0, 1, 0), dropItem
        );
        droppedItem.setVelocity(droppedItem.getVelocity().multiply(0.3)); // さらにゆっくり
        
        player.sendMessage(ChatColor.GRAY + "💧 手汗でインベントリから" + 
                         dropItem.getType().name() + "が滑り出ました...");
        
        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.3f, 0.5f);
    }
}