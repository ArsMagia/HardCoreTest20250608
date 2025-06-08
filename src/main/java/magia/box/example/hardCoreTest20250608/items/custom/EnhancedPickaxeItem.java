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
    private static final int DURABILITY_COST = 5;
    
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
        if (EffectUtils.checkCooldown(player, lastActivation.get(player.getUniqueId()), 
                50L, ITEM_NAME)) { // 50ms クールダウン
            return;
        }
        lastActivation.put(player.getUniqueId(), System.currentTimeMillis());
        
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
        
        // 5x5x5の範囲でブロックを破壊
        for (int x = -BREAK_RANGE; x <= BREAK_RANGE; x++) {
            for (int y = -BREAK_RANGE; y <= BREAK_RANGE; y++) {
                for (int z = -BREAK_RANGE; z <= BREAK_RANGE; z++) {
                    // 中心ブロックはスキップ（既に破壊される）
                    if (x == 0 && y == 0 && z == 0) continue;
                    
                    Block targetBlock = centerBlock.getRelative(x, y, z);
                    
                    if (canBreakBlock(targetBlock)) {
                        // ブロックを破壊してドロップ
                        targetBlock.breakNaturally(pickaxe);
                        blocksDestroyed++;
                    }
                }
            }
        }
        
        // 耐久力消費
        if (blocksDestroyed > 0) {
            EffectUtils.consumeDurabilityOrBreak(player, pickaxe, DURABILITY_COST, ITEM_NAME);
            
            // エフェクトとメッセージ
            player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            player.getWorld().spawnParticle(
                Particle.EXPLOSION,
                centerBlock.getLocation().add(0.5, 0.5, 0.5),
                5, 2, 2, 2, 0.1
            );
            
            player.sendMessage(ChatColor.GOLD + ITEM_NAME + "で" + 
                ChatColor.YELLOW + (blocksDestroyed + 1) + "個" + 
                ChatColor.GOLD + "のブロックを破壊しました！");
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
        
        // 岩盤と黒曜石は破壊不可
        if (material == Material.BEDROCK || material == Material.OBSIDIAN) return false;
        
        // 破壊可能なマテリアルのリストにあるか、または鉄ピッケルで破壊可能なブロック
        return BREAKABLE_MATERIALS.contains(material) || 
               material.getHardness() > 0 && material.getHardness() <= 50.0f;
    }
}