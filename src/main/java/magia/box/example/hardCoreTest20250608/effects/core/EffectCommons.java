package magia.box.example.hardCoreTest20250608.effects.core;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

/**
 * エフェクト共通ユーティリティクラス
 * パーティクル、音響、メッセージの統一管理
 */
public class EffectCommons {
    
    /**
     * 成功エフェクトを再生
     */
    public static void playSuccessEffect(Player player, EffectRarity rarity) {
        Location loc = player.getLocation();
        
        // レアリティに応じた音響
        switch (rarity) {
            case LEGENDARY:
                player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                break;
            case EPIC:
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                break;
            case RARE:
                player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
                break;
            case UNCOMMON:
                player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
                break;
            case COMMON:
                player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.0f);
                break;
        }
        
        // レアリティに応じたパーティクル
        switch (rarity) {
            case LEGENDARY:
                loc.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, loc.add(0, 1, 0), 30, 1, 1, 1, 0.1);
                break;
            case EPIC:
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                break;
            case RARE:
                loc.getWorld().spawnParticle(Particle.ENCHANT, loc.add(0, 1, 0), 15, 0.5, 0.5, 0.5, 0.2);
                break;
            case UNCOMMON:
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 10, 0.3, 0.3, 0.3, 0.1);
                break;
            case COMMON:
                loc.getWorld().spawnParticle(Particle.HEART, loc.add(0, 1, 0), 5, 0.3, 0.3, 0.3, 0.1);
                break;
        }
    }
    
    /**
     * 失敗エフェクトを再生
     */
    public static void playFailureEffect(Player player, EffectRarity rarity) {
        Location loc = player.getLocation();
        
        // レアリティに応じた音響
        switch (rarity) {
            case LEGENDARY:
                player.playSound(loc, Sound.ENTITY_WITHER_SPAWN, 0.8f, 0.8f);
                break;
            case EPIC:
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 0.6f);
                break;
            case RARE:
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
                break;
            case UNCOMMON:
                player.playSound(loc, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                break;
            case COMMON:
                player.playSound(loc, Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
                break;
        }
        
        // レアリティに応じたパーティクル
        switch (rarity) {
            case LEGENDARY:
                loc.getWorld().spawnParticle(Particle.SMOKE, loc.add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                break;
            case EPIC:
                loc.getWorld().spawnParticle(Particle.LARGE_SMOKE, loc.add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.05);
                break;
            case RARE:
                loc.getWorld().spawnParticle(Particle.SMOKE, loc.add(0, 1, 0), 15, 0.3, 0.3, 0.3, 0.05);
                break;
            case UNCOMMON:
                loc.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 1, 0), 10, 0.2, 0.2, 0.2, 0.02);
                break;
            case COMMON:
                loc.getWorld().spawnParticle(Particle.SMOKE, loc.add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0.02);
                break;
        }
    }
    
    /**
     * エフェクトメッセージを統一フォーマットで放送
     */
    public static void broadcastEffectMessage(Player player, String effectName, String description, EffectRarity rarity, boolean isLucky) {
        ChatColor color = isLucky ? getRarityColor(rarity) : ChatColor.RED;
        String prefix = isLucky ? "✨" : "💀";
        String rarityText = getRarityText(rarity);
        
        String message = String.format("%s %s%s[%s] %s が発動！", 
            prefix, color, effectName, rarityText, description);
        
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Bukkit.broadcastMessage(message);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "対象: " + player.getName());
        Bukkit.broadcastMessage(ChatColor.GRAY + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        Bukkit.broadcastMessage("");
    }
    
    /**
     * レアリティに応じた色を取得
     */
    public static ChatColor getRarityColor(EffectRarity rarity) {
        switch (rarity) {
            case LEGENDARY: return ChatColor.GOLD;
            case EPIC: return ChatColor.LIGHT_PURPLE;
            case RARE: return ChatColor.BLUE;
            case UNCOMMON: return ChatColor.GREEN;
            case COMMON: return ChatColor.WHITE;
            default: return ChatColor.GRAY;
        }
    }
    
    /**
     * レアリティテキストを取得
     */
    public static String getRarityText(EffectRarity rarity) {
        switch (rarity) {
            case LEGENDARY: return "伝説";
            case EPIC: return "エピック";
            case RARE: return "レア";
            case UNCOMMON: return "アンコモン";
            case COMMON: return "コモン";
            default: return "不明";
        }
    }
    
    /**
     * プレイヤーの状態検証
     */
    public static boolean isPlayerValid(Player player) {
        return player != null && player.isOnline() && !player.isDead();
    }
    
    /**
     * 安全な体力操作
     */
    public static void modifyHealthSafely(Player player, double amount) {
        if (!isPlayerValid(player)) return;
        
        double newHealth = player.getHealth() + amount;
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        
        newHealth = Math.max(0.5, Math.min(newHealth, maxHealth)); // 最低0.5ハート、最大値制限
        player.setHealth(newHealth);
    }
    
    /**
     * 安全な最大体力操作
     */
    public static void modifyMaxHealthSafely(Player player, double amount) {
        if (!isPlayerValid(player)) return;
        
        double currentMax = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double newMax = Math.max(2.0, currentMax + amount); // 最低1ハート
        
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newMax);
        
        // 現在体力が新しい最大値を超えている場合は調整
        if (player.getHealth() > newMax) {
            player.setHealth(newMax);
        }
    }
}