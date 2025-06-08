package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class HotbarToolEnchantEpicEffect extends LuckyEffectBase {

    /** 剣に付与可能なエンチャント（レベルIII-IV） */
    private static final Map<Enchantment, int[]> SWORD_ENCHANTS = Map.of(
        Enchantment.SHARPNESS, new int[]{3, 4},
        Enchantment.KNOCKBACK, new int[]{3, 4},
        Enchantment.LOOTING, new int[]{3, 4}
    );

    /** ツール系（ピッケル・斧・シャベル）に付与可能なエンチャント（レベルIII-IV） */
    private static final Map<Enchantment, int[]> TOOL_ENCHANTS = Map.of(
        Enchantment.EFFICIENCY, new int[]{3, 4},
        Enchantment.UNBREAKING, new int[]{3, 4}
    );

    private final Random random = new Random();

    public HotbarToolEnchantEpicEffect(JavaPlugin plugin) {
        super(plugin, "ツール強化(強)", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        // ホットバーから主要ツールを検索
        List<ToolInfo> eligibleTools = findEligibleTools(player);
        
        if (eligibleTools.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "✨ 強力なエンチャント効果を試みましたが、対象のツールが見つかりませんでした。");
            player.sendMessage(ChatColor.GRAY + "ホットバーに剣・ピッケル・斧・シャベルを配置してください。");
            
            // 軽いエフェクト
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 
                    EffectConstants.STANDARD_VOLUME, 0.8f);
            
            return getDescription() + " (ツールなし)";
        }
        
        // ランダムにツールを選択
        ToolInfo selectedTool = eligibleTools.get(random.nextInt(eligibleTools.size()));
        
        // エンチャントを付与
        String result = applyEnchantment(player, selectedTool);
        
        // 豪華なエフェクト
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 
                EffectConstants.STANDARD_VOLUME, 1.5f);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, 1.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 
                EffectConstants.STANDARD_VOLUME, 2.0f);
        
        // 豪華なパーティクルエフェクト
        player.getWorld().spawnParticle(
            Particle.ENCHANT,
            player.getLocation().add(0, 2, 0),
            80, 1.5, 1.5, 1.5, 0.8
        );
        
        player.getWorld().spawnParticle(
            Particle.TOTEM_OF_UNDYING,
            player.getLocation().add(0, 1, 0),
            25, 1, 1, 1, 0.1
        );
        
        return result;
    }
    
    /**
     * ホットバーから対象ツールを検索
     * @param player プレイヤー
     * @return 対象ツールのリスト
     */
    private List<ToolInfo> findEligibleTools(Player player) {
        List<ToolInfo> tools = new ArrayList<>();
        
        // ホットバー（スロット0-8）をチェック
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || item.getType() == Material.AIR) continue;
            
            ToolType toolType = getToolType(item.getType());
            if (toolType != null) {
                tools.add(new ToolInfo(i, item, toolType));
            }
        }
        
        return tools;
    }
    
    /**
     * マテリアルからツールタイプを取得
     * @param material マテリアル
     * @return ツールタイプ（該当しない場合はnull）
     */
    private ToolType getToolType(Material material) {
        String name = material.name();
        
        if (name.contains("SWORD")) return ToolType.SWORD;
        if (name.contains("PICKAXE")) return ToolType.PICKAXE;
        if (name.contains("AXE") && !name.contains("PICKAXE")) return ToolType.AXE;
        if (name.contains("SHOVEL")) return ToolType.SHOVEL;
        
        return null;
    }
    
    /**
     * エンチャントを付与
     * @param player プレイヤー
     * @param toolInfo ツール情報
     * @return 結果メッセージ
     */
    private String applyEnchantment(Player player, ToolInfo toolInfo) {
        ItemStack tool = toolInfo.item;
        Map<Enchantment, int[]> availableEnchants = getAvailableEnchants(toolInfo.toolType);
        
        // 既にあるエンチャントを除外
        Map<Enchantment, int[]> applicableEnchants = new HashMap<>();
        for (Map.Entry<Enchantment, int[]> entry : availableEnchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            if (!tool.containsEnchantment(enchant)) {
                applicableEnchants.put(enchant, entry.getValue());
            }
        }
        
        if (applicableEnchants.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "✨ " + getToolDisplayName(toolInfo.toolType) + 
                " は既に最高級のエンチャントが付与されています！");
            return getDescription() + " (既に最高級)";
        }
        
        // ランダムにエンチャントを選択
        List<Enchantment> enchantList = new ArrayList<>(applicableEnchants.keySet());
        Enchantment selectedEnchant = enchantList.get(random.nextInt(enchantList.size()));
        int[] levelRange = applicableEnchants.get(selectedEnchant);
        int level = levelRange[random.nextInt(levelRange.length)];
        
        // エンチャントを付与
        ItemMeta meta = tool.getItemMeta();
        if (meta != null) {
            meta.addEnchant(selectedEnchant, level, true);
            tool.setItemMeta(meta);
            
            // インベントリを更新
            player.getInventory().setItem(toolInfo.slot, tool);
        }
        
        String enchantName = getEnchantmentDisplayName(selectedEnchant);
        String toolName = getToolDisplayName(toolInfo.toolType);
        
        player.sendMessage(ChatColor.GOLD + "⚡ " + ChatColor.AQUA + toolName + ChatColor.GOLD + 
            " に " + ChatColor.LIGHT_PURPLE + enchantName + " Lv." + level + ChatColor.GOLD + " が付与されました！");
        
        return getDescription() + " (" + enchantName + " Lv." + level + ")";
    }
    
    /**
     * ツールタイプに応じたエンチャントマップを取得
     * @param toolType ツールタイプ
     * @return エンチャントマップ
     */
    private Map<Enchantment, int[]> getAvailableEnchants(ToolType toolType) {
        return switch (toolType) {
            case SWORD -> SWORD_ENCHANTS;
            case PICKAXE, AXE, SHOVEL -> TOOL_ENCHANTS;
        };
    }
    
    /**
     * エンチャントの表示名を取得
     * @param enchantment エンチャント
     * @return 日本語表示名
     */
    private String getEnchantmentDisplayName(Enchantment enchantment) {
        if (enchantment.equals(Enchantment.SHARPNESS)) return "鋭さ";
        if (enchantment.equals(Enchantment.KNOCKBACK)) return "ノックバック";
        if (enchantment.equals(Enchantment.LOOTING)) return "ドロップ増加";
        if (enchantment.equals(Enchantment.EFFICIENCY)) return "効率強化";
        if (enchantment.equals(Enchantment.UNBREAKING)) return "耐久力";
        
        return enchantment.getKey().getKey();
    }
    
    /**
     * ツールタイプの表示名を取得
     * @param toolType ツールタイプ
     * @return 日本語表示名
     */
    private String getToolDisplayName(ToolType toolType) {
        return switch (toolType) {
            case SWORD -> "剣";
            case PICKAXE -> "ピッケル";
            case AXE -> "斧";
            case SHOVEL -> "シャベル";
        };
    }
    
    /** ツールタイプ列挙 */
    private enum ToolType {
        SWORD, PICKAXE, AXE, SHOVEL
    }
    
    /** ツール情報クラス */
    private static class ToolInfo {
        final int slot;
        final ItemStack item;
        final ToolType toolType;
        
        ToolInfo(int slot, ItemStack item, ToolType toolType) {
            this.slot = slot;
            this.item = item;
            this.toolType = toolType;
        }
    }
}