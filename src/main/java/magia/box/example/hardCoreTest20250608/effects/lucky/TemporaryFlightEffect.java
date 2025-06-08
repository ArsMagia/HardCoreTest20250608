package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TemporaryFlightEffect extends LuckyEffectBase {

    public TemporaryFlightEffect(JavaPlugin plugin) {
        super(plugin, "5秒間飛行能力", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.setAllowFlight(true);
        player.setFlying(true);
        player.sendMessage(ChatColor.AQUA + "5秒間飛行可能になりました！");
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline() && player.getGameMode() == GameMode.SURVIVAL) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(ChatColor.GRAY + "飛行時間が終了しました。");
                }
            }
        }.runTaskLater(plugin, 100L);
        
        return getDescription();
    }
}