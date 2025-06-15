package magia.box.example.hardCoreTest20250608.effects.unlucky.common;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@EffectRegistration(
    id = "fake_ui_glitch",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON
)
public class FakeUIGlitchEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();
    
    private static final List<String> FAKE_DAMAGE_MESSAGES = Arrays.asList(
        "§c💀 クリーパーに爆破されました！",
        "§c⚡ 雷に撃たれました！",
        "§c🔥 溶岩で焼死しました！",
        "§c💥 TNTで爆死しました！",
        "§c🏹 スケルトンに射殺されました！"
    );
    
    private static final List<String> FAKE_ACHIEVEMENT_MESSAGES = Arrays.asList(
        "§6🏆 成果を解除しました: §a「偽の王者」",
        "§6🏆 成果を解除しました: §a「幻想の勇者」",
        "§6🏆 成果を解除しました: §a「虚無の探検家」",
        "§6🏆 成果を解除しました: §a「偽装の名人」",
        "§6🏆 成果を解除しました: §a「錯覚の達人」"
    );
    
    private static final List<String> FAKE_LEVEL_MESSAGES = Arrays.asList(
        "§aレベルアップ！ §e経験値レベル: 99→100",
        "§aレベルアップ！ §e経験値レベル: 50→51", 
        "§aレベルアップ！ §e経験値レベル: 75→76",
        "§aレベルアップ！ §e経験値レベル: 30→31"
    );
    
    private static final List<String> FAKE_ITEM_MESSAGES = Arrays.asList(
        "§d🎁 レアアイテムを発見: §6ダイヤモンドの剣 (エンチャント付き)",
        "§d🎁 レアアイテムを発見: §6ネザライトインゴット x64",
        "§d🎁 レアアイテムを発見: §6エリトラ (耐久無限)",
        "§d🎁 レアアイテムを発見: §6エンチャントの本 (修繕)",
        "§d🎁 レアアイテムを発見: §6ビーコン x3"
    );

    public FakeUIGlitchEffect(JavaPlugin plugin) {
        super(plugin, "偽UI症候群", EffectRarity.COMMON);
    }

    @Override
    public String apply(Player player) {
        player.sendMessage(ChatColor.YELLOW + "🖥️ システムに異常が発生しています...");
        
        // 3秒間隔で偽のメッセージを表示（計20秒間、6回）
        new BukkitRunnable() {
            private int count = 0;
            
            @Override
            public void run() {
                if (count >= 6 || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                
                showRandomFakeMessage(player);
                count++;
            }
        }.runTaskTimer(plugin, 60L, 60L); // 3秒間隔
        
        // 最後に正常化メッセージ
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GREEN + "💻 システムが正常化されました。上記のメッセージは全て偽物でした。");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
                }
            }
        }.runTaskLater(plugin, 400L); // 20秒後
        
        return getDescription();
    }
    
    private void showRandomFakeMessage(Player player) {
        int messageType = random.nextInt(4);
        
        switch (messageType) {
            case 0: // 偽のダメージメッセージ
                String damageMsg = FAKE_DAMAGE_MESSAGES.get(random.nextInt(FAKE_DAMAGE_MESSAGES.size()));
                player.sendMessage(damageMsg);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 0.3f, 0.8f);
                break;
                
            case 1: // 偽の成果解除
                String achievementMsg = FAKE_ACHIEVEMENT_MESSAGES.get(random.nextInt(FAKE_ACHIEVEMENT_MESSAGES.size()));
                player.sendMessage(achievementMsg);
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.3f, 1.2f);
                break;
                
            case 2: // 偽のレベルアップ
                String levelMsg = FAKE_LEVEL_MESSAGES.get(random.nextInt(FAKE_LEVEL_MESSAGES.size()));
                player.sendMessage(levelMsg);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.3f, 1.5f);
                break;
                
            case 3: // 偽のアイテム発見
                String itemMsg = FAKE_ITEM_MESSAGES.get(random.nextInt(FAKE_ITEM_MESSAGES.size()));
                player.sendMessage(itemMsg);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 0.7f);
                break;
        }
        
        // たまに警告メッセージも表示
        if (random.nextInt(3) == 0) {
            player.sendMessage(ChatColor.GRAY + "§o(これらのメッセージは偽物です)");
        }
    }
}