package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class FakeInfoEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();

    public FakeInfoEffect(JavaPlugin plugin) {
        super(plugin, "偽情報", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.YELLOW + "情報が混乱しています...30秒間、偽の情報が表示されます。");
        
        String[] fakeMessages = {
            "ダイヤモンドが近くに見つかりました！",
            "あなたの体力が回復しました！",
            "経験値を獲得しました！",
            "レアなアイテムを発見しました！",
            "隠された宝箱があります！",
            "敵が接近しています！",
            "天気が変わります！",
            "新しいバイオームを発見しました！"
        };
        
        new BukkitRunnable() {
            int counter = 0;
            
            @Override
            public void run() {
                if (counter >= 6 || !player.isOnline()) {
                    player.sendMessage(ChatColor.GRAY + "情報の混乱が収まりました。");
                    this.cancel();
                    return;
                }
                
                String fakeMessage = fakeMessages[random.nextInt(fakeMessages.length)];
                player.sendMessage(ChatColor.DARK_GRAY + "[偽情報] " + ChatColor.WHITE + fakeMessage);
                
                counter++;
            }
        }.runTaskTimer(plugin, 0L, 100L);
        
        return getDescription();
    }
}