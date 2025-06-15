package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ItemTransferEffect extends UnluckyEffectBase {

    public ItemTransferEffect(JavaPlugin plugin) {
        super(plugin, "このアイテムをお前に託す", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        onlinePlayers.remove(player); // 自分自身を除外
        
        if (onlinePlayers.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "⚠ 他にオンラインのプレイヤーがいないため、効果は発動しませんでした。");
            return "アイテム転送は他のプレイヤーがいないため失敗しました";
        }
        
        player.sendMessage(ChatColor.DARK_RED + "💀 あなたのアイテムが他のプレイヤーに送られます...");
        
        // アイテム転送を実行
        new BukkitRunnable() {
            @Override
            public void run() {
                transferItems(player, onlinePlayers);
            }
        }.runTaskLater(plugin, 20L); // 1秒後に実行
        
        return "アイテムを他のプレイヤーに転送中です";
    }

    private void transferItems(Player sender, List<Player> potentialReceivers) {
        List<ItemStack> transferableItems = getTransferableItems(sender);
        
        if (transferableItems.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "⚠ 送れるアイテムがありませんでした。");
            return;
        }
        
        // 最大3つのアイテムをランダムに選択
        Collections.shuffle(transferableItems);
        int transferCount = Math.min(3, transferableItems.size());
        List<ItemStack> itemsToTransfer = transferableItems.subList(0, transferCount);
        
        Map<Player, List<ItemStack>> transferMap = new HashMap<>();
        Random random = new Random();
        
        // 各アイテムをランダムなプレイヤーに割り当て
        for (ItemStack item : itemsToTransfer) {
            Player receiver = potentialReceivers.get(random.nextInt(potentialReceivers.size()));
            transferMap.computeIfAbsent(receiver, k -> new ArrayList<>()).add(item.clone());
        }
        
        // アイテムを送信者のインベントリから削除
        removeItemsFromInventory(sender, itemsToTransfer);
        
        // 各受信者にアイテムを送る
        for (Map.Entry<Player, List<ItemStack>> entry : transferMap.entrySet()) {
            Player receiver = entry.getKey();
            List<ItemStack> items = entry.getValue();
            
            sendItemsToPlayer(sender, receiver, items);
        }
        
        // 効果音とパーティクル
        sender.getWorld().playSound(sender.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);
        sender.getWorld().spawnParticle(Particle.PORTAL, sender.getLocation(), 50, 1, 1, 1, 0.5);
        
        // 全体通知
        String itemNames = itemsToTransfer.stream()
            .map(item -> getItemDisplayName(item))
            .reduce((a, b) -> a + ", " + b)
            .orElse("アイテム");
        
        Bukkit.broadcastMessage(ChatColor.YELLOW + "📦 " + sender.getName() + " が " + transferCount + "個のアイテムを他のプレイヤーに送りました！");
        sender.sendMessage(ChatColor.RED + "送ったアイテム: " + ChatColor.GRAY + itemNames);
    }

    private List<ItemStack> getTransferableItems(Player player) {
        List<ItemStack> transferableItems = new ArrayList<>();
        ItemStack[] contents = player.getInventory().getContents();
        
        // ホットバーの左3つ（インデックス0,1,2）は対象外
        for (int i = 3; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() != Material.AIR) {
                transferableItems.add(item);
            }
        }
        
        return transferableItems;
    }

    private void removeItemsFromInventory(Player player, List<ItemStack> itemsToRemove) {
        for (ItemStack itemToRemove : itemsToRemove) {
            // アイテムをインベントリから削除
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 3; i < contents.length; i++) { // インデックス0,1,2をスキップ
                ItemStack item = contents[i];
                if (item != null && item.isSimilar(itemToRemove)) {
                    if (item.getAmount() >= itemToRemove.getAmount()) {
                        if (item.getAmount() == itemToRemove.getAmount()) {
                            contents[i] = null;
                        } else {
                            item.setAmount(item.getAmount() - itemToRemove.getAmount());
                        }
                        break;
                    }
                }
            }
            player.getInventory().setContents(contents);
        }
    }

    private void sendItemsToPlayer(Player sender, Player receiver, List<ItemStack> items) {
        for (ItemStack item : items) {
            // 受信者のインベントリに空きがあるかチェック
            HashMap<Integer, ItemStack> leftover = receiver.getInventory().addItem(item);
            
            if (!leftover.isEmpty()) {
                // インベントリが満杯の場合は地面にドロップ
                for (ItemStack leftoverItem : leftover.values()) {
                    receiver.getWorld().dropItemNaturally(receiver.getLocation(), leftoverItem);
                }
            }
        }
        
        // 受信者に通知
        String itemNames = items.stream()
            .map(item -> getItemDisplayName(item))
            .reduce((a, b) -> a + ", " + b)
            .orElse("アイテム");
        
        receiver.sendMessage(ChatColor.GREEN + "📦 " + sender.getName() + " からアイテムが送られました！");
        receiver.sendMessage(ChatColor.GRAY + "受け取ったアイテム: " + itemNames);
        
        // 受信者にエフェクト
        receiver.getWorld().playSound(receiver.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        receiver.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, receiver.getLocation(), 20, 1, 1, 1, 0.3);
        
        // 送信者に詳細通知
        sender.sendMessage(ChatColor.GRAY + "→ " + receiver.getName() + " に送付: " + itemNames);
    }

    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        
        // マテリアル名を日本語風に変換
        String materialName = item.getType().name().toLowerCase().replace("_", " ");
        if (item.getAmount() > 1) {
            return materialName + " x" + item.getAmount();
        }
        return materialName;
    }
}