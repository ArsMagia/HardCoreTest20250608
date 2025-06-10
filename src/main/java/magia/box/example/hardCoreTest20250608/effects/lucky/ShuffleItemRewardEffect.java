package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShuffleItemRewardEffect extends LuckyEffectBase {

    public ShuffleItemRewardEffect(JavaPlugin plugin) {
        super(plugin, "シャッフル（アイテム）", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        // オンラインのプレイヤーを取得（自分以外）
        List<Player> onlinePlayers = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player && p.isOnline()) {
                onlinePlayers.add(p);
            }
        }
        
        if (onlinePlayers.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "他のオンラインプレイヤーがいないため、ランダムな安全な場所にテレポートします。");
            
            // ランダムな安全な場所を生成
            Location randomLoc = generateSafeLocation(player);
            if (randomLoc != null) {
                teleportWithEffects(player, randomLoc, null);
            } else {
                player.sendMessage(ChatColor.RED + "安全な場所が見つかりませんでした。");
                return "テレポート失敗";
            }
        } else {
            // ランダムなプレイヤーを選択
            Random random = new Random();
            Player targetPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
            
            teleportWithEffects(player, targetPlayer.getLocation(), targetPlayer);
        }
        
        return getDescription();
    }
    
    private void teleportWithEffects(Player player, Location targetLocation, Player targetPlayer) {
        Location originalLoc = player.getLocation();
        
        // テレポート前エフェクト
        originalLoc.getWorld().spawnParticle(
            Particle.PORTAL,
            originalLoc.add(0, 1, 0),
            50, 1, 1, 1, 1.0
        );
        player.playSound(originalLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // テレポート実行
        Location safeLoc = targetLocation.clone().add(0, 1, 0); // 少し上に
        player.teleport(safeLoc);
        
        // テレポート後エフェクト
        safeLoc.getWorld().spawnParticle(
            Particle.PORTAL,
            safeLoc,
            30, 1, 1, 1, 0.5
        );
        player.playSound(safeLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        
        // メッセージ
        if (targetPlayer != null) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ " + targetPlayer.getName() + " の場所にランダムテレポートしました！");
            targetPlayer.sendMessage(ChatColor.YELLOW + "⚡ " + player.getName() + " があなたの場所にテレポートしてきました！");
        } else {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ ランダムな場所にテレポートしました！");
        }
    }
    
    private Location generateSafeLocation(Player player) {
        Random random = new Random();
        Location baseLoc = player.getLocation();
        
        // 10回まで試行
        for (int i = 0; i < 10; i++) {
            int x = baseLoc.getBlockX() + random.nextInt(200) - 100; // ±100ブロック
            int z = baseLoc.getBlockZ() + random.nextInt(200) - 100;
            int y = baseLoc.getWorld().getHighestBlockYAt(x, z) + 1;
            
            Location testLoc = new Location(baseLoc.getWorld(), x + 0.5, y, z + 0.5);
            
            // 安全性チェック
            if (y > 0 && y < 256 && testLoc.getBlock().getType().isAir() && 
                testLoc.clone().add(0, 1, 0).getBlock().getType().isAir()) {
                return testLoc;
            }
        }
        
        // 安全な場所が見つからない場合はnull
        return null;
    }
}