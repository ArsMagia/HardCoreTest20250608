package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class LuckyBoxDistributionEffect extends LuckyEffectBase {

    public LuckyBoxDistributionEffect(JavaPlugin plugin) {
        super(plugin, "ラッキーボックス大配布", EffectRarity.EPIC);
    }

    @Override
    public String apply(Player player) {
        ItemStack luckyBox = ItemRegistryAccessor.getLuckyBoxItem().createItem();
        luckyBox.setAmount(2);
        
        int distributedCount = 0;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.getInventory().addItem(luckyBox.clone());
            
            onlinePlayer.getWorld().spawnParticle(
                Particle.FIREWORK,
                onlinePlayer.getLocation().add(0, 2, 0),
                20, 1, 1, 1, 0.1
            );
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.2f);
            
            if (onlinePlayer != player) {
                onlinePlayer.sendMessage(ChatColor.GOLD + "✨ " + ChatColor.YELLOW + player.getName() + 
                    ChatColor.GRAY + " の幸運により、全員がラッキーボックス×2を受け取りました！");
            }
            distributedCount++;
        }
        
        // サーバー全体告知
        Bukkit.broadcastMessage(ChatColor.GOLD + "🎉 " + ChatColor.YELLOW + "サーバー全体配布イベント！" +
            ChatColor.GRAY + " 全プレイヤー" + distributedCount + "人にラッキーボックス×2が配布されました！");
        
        player.sendMessage(ChatColor.GREEN + "あなたの幸運がサーバー全体に広がりました！");
        
        return getDescription();
    }
}