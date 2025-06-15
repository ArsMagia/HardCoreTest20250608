package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 生命の息吹
 * 最大体力が10❤以下のプレイヤーを選択して体力を回復・増強する効果
 */
public class LifeBreathEffect extends LuckyEffectBase implements Listener {

    private static final String GUI_TITLE = "生命の息吹 - 対象選択";
    private Player caster;

    public LifeBreathEffect(JavaPlugin plugin) {
        super(plugin, "生命の息吹", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        this.caster = player;
        
        // 対象プレイヤーを取得（最大体力10❤以下）
        List<Player> eligiblePlayers = getEligiblePlayers();
        
        if (eligiblePlayers.isEmpty()) {
            // 対象がいない場合は自分に効果を適用
            applySelfBoost(player);
            return "自己強化を実行";
        }
        
        // イベントリスナーを登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        // GUIを開く
        openSelectionGUI(player, eligiblePlayers);
        
        player.sendMessage(ChatColor.GREEN + "✨ 生命の息吹が発動しました！");
        player.sendMessage(ChatColor.YELLOW + "💚 対象を選択してください...");
        
        // 発動音
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.5f);
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 2, 0),
            15, 1, 1, 1, 0.1
        );
        
        return getDescription();
    }
    
    /**
     * 対象となるプレイヤーを取得（最大体力10❤以下）
     */
    private List<Player> getEligiblePlayers() {
        List<Player> eligible = new ArrayList<>();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            if (maxHealth <= 20.0) { // 20HP = 10❤
                eligible.add(player);
            }
        }
        
        return eligible;
    }
    
    /**
     * 選択GUIを開く
     */
    private void openSelectionGUI(Player caster, List<Player> eligiblePlayers) {
        // インベントリサイズを計算（9の倍数、最低18スロット）
        int size = Math.max(18, ((eligiblePlayers.size() + 8) / 9) * 9);
        Inventory gui = Bukkit.createInventory(null, size, GUI_TITLE);
        
        // 各プレイヤーのアイテムを作成
        for (int i = 0; i < eligiblePlayers.size() && i < size - 9; i++) {
            Player target = eligiblePlayers.get(i);
            ItemStack playerHead = createPlayerHead(target);
            gui.setItem(i, playerHead);
        }
        
        // 説明アイテム（最下段中央）
        ItemStack info = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GREEN + "生命の息吹について");
        List<String> infoLore = new ArrayList<>();
        infoLore.add(ChatColor.GRAY + "プレイヤーを選択して体力を回復・増強");
        infoLore.add("");
        infoLore.add(ChatColor.YELLOW + "• 最大体力9❤以下 → 10❤に回復");
        infoLore.add(ChatColor.YELLOW + "  + Regeneration I 3秒");
        infoLore.add(ChatColor.GOLD + "• 最大体力10❤ → 11❤に増強");
        infoLore.add("");
        infoLore.add(ChatColor.GREEN + "対象: 最大体力10❤以下のプレイヤー");
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        gui.setItem(size - 5, info); // 最下段中央
        
        caster.openInventory(gui);
    }
    
    /**
     * プレイヤーの頭アイテムを作成
     */
    private ItemStack createPlayerHead(Player target) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        
        skullMeta.setOwningPlayer(target);
        skullMeta.setDisplayName(ChatColor.GREEN + target.getName());
        
        List<String> lore = new ArrayList<>();
        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        double currentHealth = target.getHealth();
        int maxHearts = (int) (maxHealth / 2);
        int currentHearts = (int) Math.ceil(currentHealth / 2);
        
        lore.add(ChatColor.GRAY + "現在の体力: " + ChatColor.RED + currentHearts + "❤ / " + maxHearts + "❤");
        lore.add("");
        
        if (maxHealth < 20.0) { // 9❤以下
            lore.add(ChatColor.YELLOW + "効果: 最大体力を10❤に回復");
            lore.add(ChatColor.YELLOW + "      + Regeneration I 3秒");
        } else { // 10❤
            lore.add(ChatColor.GOLD + "効果: 最大体力を11❤に増強");
        }
        
        lore.add("");
        lore.add(ChatColor.GREEN + "クリックして選択");
        
        skullMeta.setLore(lore);
        head.setItemMeta(skullMeta);
        
        return head;
    }
    
    /**
     * GUIクリックイベント
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_TITLE)) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player clicker = (Player) event.getWhoClicked();
        if (!clicker.equals(caster)) {
            return;
        }
        
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() != Material.PLAYER_HEAD) {
            return;
        }
        
        SkullMeta skullMeta = (SkullMeta) clickedItem.getItemMeta();
        if (skullMeta.getOwningPlayer() == null) {
            return;
        }
        
        Player target = Bukkit.getPlayer(skullMeta.getOwningPlayer().getUniqueId());
        if (target == null || !target.isOnline()) {
            clicker.sendMessage(ChatColor.RED + "⚠ 対象プレイヤーがオフラインです。");
            return;
        }
        
        // GUIを閉じてイベントリスナーを解除
        clicker.closeInventory();
        HandlerList.unregisterAll(this);
        
        // 生命の息吹を適用
        applyLifeBreath(clicker, target);
    }
    
    /**
     * 生命の息吹を適用
     */
    private void applyLifeBreath(Player caster, Player target) {
        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        
        caster.sendMessage(ChatColor.GREEN + "✨ " + target.getName() + " に生命の息吹を与えました！");
        
        if (maxHealth < 20.0) { // 9❤以下の場合
            // 最大体力を10❤に回復
            target.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20.0);
            target.setHealth(20.0); // 現在の体力も満タンに
            
            // Regeneration I を3秒
            target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0)); // 60tick = 3秒
            
            target.sendMessage(ChatColor.GREEN + "💚 " + caster.getName() + " の生命の息吹で最大体力が10❤に回復しました！");
            target.sendMessage(ChatColor.YELLOW + "✨ Regeneration I を3秒間受けています");
            
            // サーバー通知
            Bukkit.broadcastMessage(ChatColor.GREEN + "💚 " + caster.getName() + " が " + target.getName() + " の生命力を回復させました！");
            
        } else { // 10❤の場合
            // 最大体力を11❤に増強
            target.getAttribute(Attribute.MAX_HEALTH).setBaseValue(22.0);
            target.setHealth(22.0); // 現在の体力も満タンに
            
            target.sendMessage(ChatColor.GOLD + "💛 " + caster.getName() + " の生命の息吹で最大体力が11❤に増強されました！");
            
            // サーバー通知
            Bukkit.broadcastMessage(ChatColor.GOLD + "💛 " + caster.getName() + " が " + target.getName() + " の生命力を増強させました！");
        }
        
        // エフェクト
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.8f);
        target.getWorld().spawnParticle(
            Particle.HEART,
            target.getLocation().add(0, 2, 0),
            20, 1, 2, 1, 0.1
        );
        target.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            target.getLocation().add(0, 1, 0),
            15, 0.5, 1, 0.5, 0.1
        );
        
        // 施術者にもエフェクト
        caster.playSound(caster.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 2.0f);
        caster.getWorld().spawnParticle(
            Particle.ENCHANT,
            caster.getLocation().add(0, 1, 0),
            10, 1, 1, 1, 0.1
        );
    }
    
    /**
     * 自分に生命の息吹を適用（フォールバック）
     */
    private void applySelfBoost(Player player) {
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        
        player.sendMessage(ChatColor.GREEN + "✨ 対象がいないため、自分に生命の息吹を与えました！");
        
        if (maxHealth >= 32.0) { // 16❤以上の場合
            // Regeneration II を4秒間
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1)); // 80tick = 4秒, レベルII
            
            player.sendMessage(ChatColor.YELLOW + "💚 最大体力が上限のため、Regeneration II を4秒間受けました！");
            
            // サーバー通知
            Bukkit.broadcastMessage(ChatColor.GREEN + "💚 " + player.getName() + " が自分に生命の息吹で再生力を与えました！");
            
        } else { // 16❤未満の場合
            // 最大体力を1❤増加
            double newMaxHealth = maxHealth + 2.0; // 2HP = 1❤
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newMaxHealth);
            
            // 現在の体力も回復
            double newCurrentHealth = Math.min(player.getHealth() + 2.0, newMaxHealth);
            player.setHealth(newCurrentHealth);
            
            int addedHearts = (int) (newMaxHealth / 2);
            player.sendMessage(ChatColor.GOLD + "💛 最大体力が1❤増加しました！（現在" + addedHearts + "❤）");
            
            // サーバー通知
            Bukkit.broadcastMessage(ChatColor.GOLD + "💛 " + player.getName() + " が自分に生命の息吹で体力を増強しました！");
        }
        
        // エフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.8f);
        player.getWorld().spawnParticle(
            Particle.HEART,
            player.getLocation().add(0, 2, 0),
            20, 1, 2, 1, 0.1
        );
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            15, 0.5, 1, 0.5, 0.1
        );
        
        // 自己強化のエフェクト
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 1, 0),
            15, 1, 1, 1, 0.1
        );
    }
}