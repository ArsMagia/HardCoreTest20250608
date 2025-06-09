package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnhancedPickaxeItem extends AbstractCustomItemV2 {

    /** クールダウン管理 */
    private static final Map<UUID, Long> lastActivation = new HashMap<>();
    
    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "強化ピッケル";
    
    /** 範囲採掘の範囲（5x5） */
    private static final int BREAK_RANGE = 2; // 中心から2ブロック = 5x5
    
    /** 耐久消費量 */
    private static final int DURABILITY_COST = 15;
    
    /** 破壊可能なブロックのセット */
    private static final Set<Material> BREAKABLE_MATERIALS = Set.of(
        Material.STONE, Material.COBBLESTONE, Material.GRANITE, Material.DIORITE, Material.ANDESITE,
        Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE,
        Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COPPER_ORE, Material.DEEPSLATE_COAL_ORE,
        Material.DEEPSLATE_IRON_ORE, Material.DEEPSLATE_GOLD_ORE, Material.DEEPSLATE_DIAMOND_ORE,
        Material.DEEPSLATE_EMERALD_ORE, Material.DEEPSLATE_LAPIS_ORE, Material.DEEPSLATE_REDSTONE_ORE,
        Material.DEEPSLATE_COPPER_ORE, Material.DEEPSLATE, Material.COBBLED_DEEPSLATE,
        Material.SANDSTONE, Material.RED_SANDSTONE, Material.NETHERRACK, Material.BLACKSTONE,
        Material.BASALT, Material.SMOOTH_BASALT, Material.CALCITE, Material.TUFF,
        Material.DIRT, Material.GRAVEL, Material.SAND, Material.RED_SAND
    );

    public EnhancedPickaxeItem(JavaPlugin plugin) {
        super(plugin, builder("enhanced_pickaxe", "強化ピッケル")
                .material(Material.IRON_PICKAXE)
                .rarity(ItemRarity.LEGENDARY)
                .addLore("5x5範囲で一度に採掘")
                .addLore("効率強化IV内蔵")
                .addLore("耐久消費: " + DURABILITY_COST + " per use"));
    }

    @Override
    public ItemStack createItem() {
        ItemStack item = super.createItem();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // 効率強化IVを追加（プレイヤーには見えないように設定）
            meta.addEnchant(Enchantment.EFFICIENCY, 4, true);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (!isCustomItem(item)) {
            return;
        }
        
        // クールダウンチェック（短時間の二重実行防止）
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUse = lastActivation.get(playerId);
        
        if (lastUse != null && (currentTime - lastUse) < 50L) {
            // クールダウン中は無言でリターン（メッセージ重複防止）
            return;
        }
        
        // クールダウン設定（複数回発動防止のため早期設定）
        lastActivation.put(playerId, currentTime);
        
        Block brokenBlock = event.getBlock();
        
        // 範囲採掘を実行
        executeAreaMining(player, item, brokenBlock);
    }
    
    /**
     * 範囲採掘を実行
     * @param player プレイヤー
     * @param pickaxe ピッケル
     * @param centerBlock 中心ブロック
     */
    private void executeAreaMining(Player player, ItemStack pickaxe, Block centerBlock) {
        int blocksDestroyed = 0;
        int obsidianCount = 0;
        
        // 5x5x5の範囲でブロックを破壊
        for (int x = -BREAK_RANGE; x <= BREAK_RANGE; x++) {
            for (int y = -BREAK_RANGE; y <= BREAK_RANGE; y++) {
                for (int z = -BREAK_RANGE; z <= BREAK_RANGE; z++) {
                    // 中心ブロックはスキップ（既に破壊される）
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    Block targetBlock = centerBlock.getRelative(x, y, z);
                    
                    if (canBreakBlock(targetBlock)) {
                        // 黒曜石の場合は特別処理
                        if (targetBlock.getType() == Material.OBSIDIAN) {
                            obsidianCount++;
                        }
                        
                        // ブロックを破壊してドロップ
                        targetBlock.breakNaturally(pickaxe);
                        blocksDestroyed++;
                    }
                }
            }
        }
        
        // 中心ブロックが黒曜石の場合もカウント
        if (centerBlock.getType() == Material.OBSIDIAN) {
            obsidianCount++;
        }
        
        // 耐久力消費（黒曜石は50、他は15）
        if (blocksDestroyed > 0 || obsidianCount > 0) {
            int totalDurabilityCost = DURABILITY_COST + (obsidianCount * 35); // 15 + 35 = 50 per obsidian
            EffectUtils.consumeDurabilityOrBreak(player, pickaxe, totalDurabilityCost, ITEM_NAME);
            
            // 残り耐久力を表示
            displayRemainingDurability(player, pickaxe);
            
            // 黒曜石破壊時の特別メッセージ
            if (obsidianCount > 0) {
                player.sendMessage(ChatColor.DARK_PURPLE + "🔮 黒曜石 x" + obsidianCount + " を破壊！追加耐久消費: " + (obsidianCount * 35));
            }
            
            // エフェクトとメッセージ
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            player.getWorld().spawnParticle(
                Particle.EXPLOSION,
                centerBlock.getLocation().add(0.5, 0.5, 0.5),
                5, 2, 2, 2, 0.1
            );
        }
    }
    
    /**
     * ブロックが破壊可能かチェック
     * @param block 対象ブロック
     * @return 破壊可能かどうか
     */
    private boolean canBreakBlock(Block block) {
        Material material = block.getType();
        
        // 空気ブロックはスキップ
        if (material == Material.AIR) return false;
        
        // 岩盤は破壊不可（黒曜石は破壊可能）
        if (material == Material.BEDROCK) return false;
        
        // 破壊可能なマテリアルのリストにあるか、または鉄ピッケルで破壊可能なブロック
        return BREAKABLE_MATERIALS.contains(material) || 
               material.getHardness() > 0 && material.getHardness() <= 50.0f;
    }
    
    /**
     * 残り耐久力を表示
     * @param player プレイヤー
     * @param item アイテム
     */
    private void displayRemainingDurability(Player player, ItemStack item) {
        if (item.getItemMeta() == null) return;
        
        org.bukkit.inventory.meta.Damageable damageable = 
            (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
        
        int maxDurability = item.getType().getMaxDurability();
        int currentDamage = damageable.getDamage();
        int remainingDurability = maxDurability - currentDamage;
        
        // 耐久力に応じてメッセージの色を変更
        ChatColor durabilityColor;
        if (remainingDurability > maxDurability * 0.6) {
            durabilityColor = ChatColor.GREEN;
        } else if (remainingDurability > maxDurability * 0.3) {
            durabilityColor = ChatColor.YELLOW;
        } else {
            durabilityColor = ChatColor.RED;
        }
        
        player.sendMessage(ChatColor.GRAY + "残り耐久: " + durabilityColor + remainingDurability + 
                          ChatColor.GRAY + "/" + ChatColor.WHITE + maxDurability);
    }
}