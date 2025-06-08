package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

// 最終アンラッキー効果群（26-50）
public class FinalUnluckyEffects {
    private static final Random random = new Random();
    
    // 26. ミラー効果（逆転世界）
    public static class MirrorWorldEffect extends UnluckyEffectBase {
        public MirrorWorldEffect(JavaPlugin plugin) {
            super(plugin, "ミラー効果", EffectRarity.LEGENDARY);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 400, 2));
            player.sendMessage(ChatColor.LIGHT_PURPLE + "世界が鏡のように反転しました...");
            return getDescription();
        }
    }
    
    // 27. 蒸発効果
    public static class EvaporationEffect extends UnluckyEffectBase {
        public EvaporationEffect(JavaPlugin plugin) {
            super(plugin, "蒸発効果", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 200, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
            player.sendMessage(ChatColor.GRAY + "体が蒸発して半透明になりました...");
            return getDescription();
        }
    }
    
    // 28. 石化の始まり
    public static class PetrificationEffect extends UnluckyEffectBase {
        public PetrificationEffect(JavaPlugin plugin) {
            super(plugin, "石化の始まり", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 300, 1));
            player.sendMessage(ChatColor.GRAY + "体が石のように硬くなり始めました...");
            return getDescription();
        }
    }
    
    // 29. 重複視覚
    public static class DoubleVisionEffect extends UnluckyEffectBase {
        public DoubleVisionEffect(JavaPlugin plugin) {
            super(plugin, "重複視覚", EffectRarity.COMMON);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 300, 1));
            player.sendMessage(ChatColor.YELLOW + "物が二重に見えます...");
            return getDescription();
        }
    }
    
    // 30. 時計故障
    public static class ClockMalfunctionEffect extends UnluckyEffectBase {
        public ClockMalfunctionEffect(JavaPlugin plugin) {
            super(plugin, "時計故障", EffectRarity.COMMON);
        }
        @Override
        public String apply(Player player) {
            player.sendMessage(ChatColor.DARK_GRAY + "時間の感覚が狂いました...何時かわからなくなります。");
            return getDescription();
        }
    }
    
    // 31. 影の束縛
    public static class ShadowBindingEffect extends UnluckyEffectBase {
        public ShadowBindingEffect(JavaPlugin plugin) {
            super(plugin, "影の束縛", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 1));
            player.sendMessage(ChatColor.BLACK + "影に足を掴まれました...");
            return getDescription();
        }
    }
    
    // 32. 酸素欠乏
    public static class OxygenDeprivationEffect extends UnluckyEffectBase {
        public OxygenDeprivationEffect(JavaPlugin plugin) {
            super(plugin, "酸素欠乏", EffectRarity.COMMON);
        }
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 300, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 300, 1));
            player.sendMessage(ChatColor.BLUE + "酸素が薄くて息苦しいです...");
            return getDescription();
        }
    }
    
    // 33. 電波障害
    public static class RadioInterferenceEffect extends UnluckyEffectBase {
        public RadioInterferenceEffect(JavaPlugin plugin) {
            super(plugin, "電波障害", EffectRarity.COMMON);
        }
        @Override
        public String apply(Player player) {
            player.sendMessage(ChatColor.GRAY + "電波障害により通信が困難になりました...");
            return getDescription();
        }
    }
    
    // 34. 磁力異常
    public static class MagneticAnomalyEffect extends UnluckyEffectBase {
        public MagneticAnomalyEffect(JavaPlugin plugin) {
            super(plugin, "磁力異常", EffectRarity.RARE);
        }
        @Override
        public String apply(Player player) {
            // 金属アイテムをランダムに並び替え
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && isMetallic(item.getType()) && random.nextBoolean()) {
                    int swapIndex = random.nextInt(36);
                    ItemStack swapItem = player.getInventory().getItem(swapIndex);
                    player.getInventory().setItem(i, swapItem);
                    player.getInventory().setItem(swapIndex, item);
                }
            }
            player.sendMessage(ChatColor.DARK_GRAY + "磁力異常により金属アイテムが移動しました!");
            return getDescription();
        }
        
        private boolean isMetallic(Material mat) {
            String name = mat.toString();
            return name.contains("IRON") || name.contains("GOLD") || name.contains("DIAMOND") || name.contains("NETHERITE");
        }
    }
    
    // 35. 幻影の痛み
    public static class PhantomPainEffect extends UnluckyEffectBase {
        public PhantomPainEffect(JavaPlugin plugin) {
            super(plugin, "幻影の痛み", EffectRarity.COMMON);
        }
        @Override
        public String apply(Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            player.sendMessage(ChatColor.RED + "幻影の痛みを感じます...でも実際には怪我していません。");
            return getDescription();
        }
    }
    
    // 36-50: シンプルな効果をまとめて定義
    public static class QuickDebuffEffect extends UnluckyEffectBase {
        private final PotionEffectType effectType;
        private final String displayName;
        
        public QuickDebuffEffect(JavaPlugin plugin, String name, PotionEffectType type) {
            super(plugin, name, EffectRarity.COMMON);
            this.effectType = type;
            this.displayName = name;
        }
        
        @Override
        public String apply(Player player) {
            player.addPotionEffect(new PotionEffect(effectType, 300, 0));
            player.sendMessage(ChatColor.RED + displayName + "の効果を受けました!");
            return getDescription();
        }
    }
    
    // ファクトリーメソッドで残りの効果を生成
    public static QuickDebuffEffect createSlownessVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "重い足", PotionEffectType.SLOWNESS);
    }
    
    public static QuickDebuffEffect createWeaknessVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "力の減退", PotionEffectType.WEAKNESS);
    }
    
    public static QuickDebuffEffect createMiningFatigueVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "採掘疲労", PotionEffectType.MINING_FATIGUE);
    }
    
    public static QuickDebuffEffect createHungerVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "激しい空腹", PotionEffectType.HUNGER);
    }
    
    public static QuickDebuffEffect createNauseaVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "めまい", PotionEffectType.NAUSEA);
    }
    
    public static QuickDebuffEffect createBlindnessVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "一時失明", PotionEffectType.BLINDNESS);
    }
    
    public static QuickDebuffEffect createPoisonVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "軽い毒", PotionEffectType.POISON);
    }
    
    public static QuickDebuffEffect createWitherVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "衰弱", PotionEffectType.WITHER);
    }
    
    public static QuickDebuffEffect createLevitationVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "軽い浮遊", PotionEffectType.LEVITATION);
    }
    
    public static QuickDebuffEffect createUnluckVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "不幸", PotionEffectType.UNLUCK);
    }
    
    public static QuickDebuffEffect createDarknessVariant(JavaPlugin plugin) {
        return new QuickDebuffEffect(plugin, "闇の帳", PotionEffectType.DARKNESS);
    }
    
    // 追加の創造的効果
    public static class ItemShuffleEffect extends UnluckyEffectBase {
        public ItemShuffleEffect(JavaPlugin plugin) {
            super(plugin, "アイテムシャッフル", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            // インベントリをシャッフル
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length - 1; i++) {
                if (random.nextBoolean()) {
                    int j = i + 1 + random.nextInt(contents.length - i - 1);
                    ItemStack temp = contents[i];
                    contents[i] = contents[j];
                    contents[j] = temp;
                }
            }
            player.getInventory().setContents(contents);
            player.sendMessage(ChatColor.YELLOW + "インベントリがシャッフルされました!");
            return getDescription();
        }
    }
    
    public static class SoundSpamEffect extends UnluckyEffectBase {
        public SoundSpamEffect(JavaPlugin plugin) {
            super(plugin, "音響スパム", EffectRarity.RARE);
        }
        
        @Override
        public String apply(Player player) {
            org.bukkit.scheduler.BukkitRunnable task = new org.bukkit.scheduler.BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if (count >= 20 || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    Sound[] sounds = {Sound.ENTITY_CHICKEN_AMBIENT, Sound.ENTITY_COW_AMBIENT, Sound.ENTITY_PIG_AMBIENT};
                    player.playSound(player.getLocation(), sounds[random.nextInt(sounds.length)], 0.5f, 1.0f);
                    count++;
                }
            };
            task.runTaskTimer(plugin, 0L, 5L);
            player.sendMessage(ChatColor.YELLOW + "動物の鳴き声が止まりません!");
            return getDescription();
        }
    }
    
    public static class FakeDamageEffect extends UnluckyEffectBase {
        public FakeDamageEffect(JavaPlugin plugin) {
            super(plugin, "偽ダメージ", EffectRarity.COMMON);
        }
        
        @Override
        public String apply(Player player) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            player.sendMessage(ChatColor.RED + "ダメージを受けた気がしますが...気のせいでした。");
            return getDescription();
        }
    }
}