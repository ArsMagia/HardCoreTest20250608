package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;

import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealKitItem extends AbstractCustomItemV2 {

    /** 治療対象のクールダウン管理（エフェクト重複防止） */
    private static final Map<UUID, Long> targetLastHealed = new HashMap<>();
    
    /** 使用者の重複クリック防止（最小限のクールダウン） */
    private static final Map<UUID, Long> userLastClick = new HashMap<>();
    
    /** アイテム名（メッセージ用） */
    private static final String ITEM_NAME = "ヒールキット";
    
    /** 耐久消費量（半分） */
    private static final int DURABILITY_COST_HALF = 131; // 262の半分（ハサミの最大耐久度）
    
    /** 治療対象のクールダウン時間（ミリ秒） */
    private static final long TARGET_COOLDOWN_MS = 5000L; // 5秒
    
    /** 使用者の重複クリック防止時間（ミリ秒） */
    private static final long USER_CLICK_COOLDOWN_MS = 500L; // 0.5秒

    public HealKitItem(JavaPlugin plugin) {
        super(plugin, builder("heal_kit", "ヒールキット")
                .material(Material.SHEARS)
                .rarity(ItemRarity.RARE)
                .addLore("プレイヤーを左クリックで治療")
                .addLore("効果: Regeneration II (4秒)")
                .addHint("耐久消費: 半分"));
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // プレイヤーが他のエンティティを攻撃した場合
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player damager = (Player) event.getDamager();
        ItemStack item = damager.getInventory().getItemInMainHand();
        
        // カスタムアイテムチェック
        if (!isCustomItem(item)) {
            return;
        }
        
        // 攻撃をキャンセル（ノックバック防止）
        event.setCancelled(true);
        
        // 対象がプレイヤーでない場合は無効
        if (!(event.getEntity() instanceof Player)) {
            damager.sendMessage(ChatColor.RED + "プレイヤーのみ治療できます。");
            return;
        }
        
        Player target = (Player) event.getEntity();
        
        // 使用者の重複クリック防止チェック
        UUID damagerId = damager.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long userLastUse = userLastClick.get(damagerId);
        
        if (userLastUse != null && (currentTime - userLastUse) < USER_CLICK_COOLDOWN_MS) {
            // 重複クリック中は無言でリターン
            return;
        }
        
        // 治療対象のクールダウンチェック（エフェクト重複防止）
        UUID targetId = target.getUniqueId();
        Long targetLastUse = targetLastHealed.get(targetId);
        
        if (targetLastUse != null && (currentTime - targetLastUse) < TARGET_COOLDOWN_MS) {
            long remainingTime = TARGET_COOLDOWN_MS - (currentTime - targetLastUse);
            double remainingSeconds = remainingTime / 1000.0;
            
            // 使用者のクリックタイムスタンプを更新（メッセージ重複防止）
            userLastClick.put(damagerId, currentTime);
            
            // 使用者と治療対象の両方に通知
            damager.sendMessage(ChatColor.RED + target.getName() + "はあと" + 
                    String.format("%.1f", remainingSeconds) + "秒でヒール可能になります。");
            target.sendMessage(ChatColor.YELLOW + damager.getName() + "があなたを治療しようとしましたが、あと" + 
                    String.format("%.1f", remainingSeconds) + "秒でヒール可能になります。");
            return;
        }
        
        // 使用者のクリックタイムスタンプを更新
        userLastClick.put(damagerId, currentTime);
        
        // 治療対象のクールダウン設定
        targetLastHealed.put(targetId, currentTime);

        // 治療効果を実行
        executeHealingEffect(damager, target);
        
        // 耐久力消費または破壊（治療が成功した場合のみ）
        EffectUtils.consumeDurabilityOrBreak(damager, item, DURABILITY_COST_HALF, ITEM_NAME);
    }
    
    /**
     * 治療効果を実行
     * @param healer 治療者
     * @param target 治療対象
     */
    private void executeHealingEffect(Player healer, Player target) {
        // Regeneration II を4秒付与
        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1)); // 80tick = 4秒, レベル1 = Regeneration II
        
        // エフェクトとメッセージ
        target.playSound(target.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 
                EffectConstants.STANDARD_VOLUME, EffectConstants.SUCCESS_PITCH);
        target.getWorld().spawnParticle(
                Particle.HEART,
                target.getLocation().add(0, 1, 0),
                5, 0.5, 0.5, 0.5, 0.1
        );
        
        // メッセージ
        healer.sendMessage(ChatColor.GREEN + ITEM_NAME + "で" + target.getName() + "を治療しました！");
        target.sendMessage(ChatColor.GREEN + healer.getName() + "に" + ITEM_NAME + "で治療されました！");
    }
}