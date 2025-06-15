# Javaプログラミング基礎から理解するMinecraftカスタムアイテム実装ガイド

## 📖 このガイドについて

このガイドでは、**Javaコーディング初級者**が段階的にスキルアップして、最終的に**高度なカスタムアイテムシステム**を理解・実装できるようになることを目指します。

---

## 🎯 学習の流れ

1. **Javaの基本構造理解** → **抽象化の概念** → **実装パターン習得** → **カスタムアイテム完成**

---

## 📚 第1章: Javaの基本構造を理解しよう

### 1.1 クラスって何？ - 設計図の概念

**まずは身近な例で理解しましょう**

```java
// 🏠 家の設計図（クラス）
public class House {
    // 特徴（フィールド）
    private String color;      // 色
    private int rooms;         // 部屋数
    private boolean hasGarden; // 庭があるか
    
    // 作り方（コンストラクタ）
    public House(String color, int rooms, boolean hasGarden) {
        this.color = color;
        this.rooms = rooms;
        this.hasGarden = hasGarden;
    }
    
    // できること（メソッド）
    public void openDoor() {
        System.out.println(color + "の家のドアを開けました");
    }
    
    public void showInfo() {
        System.out.println("色: " + color + ", 部屋数: " + rooms + ", 庭: " + (hasGarden ? "あり" : "なし"));
    }
}

// 実際に家を建てる（インスタンス作成）
public class Main {
    public static void main(String[] args) {
        // 設計図から実際の家を作る
        House myHouse = new House("青", 3, true);
        House friendHouse = new House("赤", 2, false);
        
        // 家を使う
        myHouse.openDoor();     // "青の家のドアを開けました"
        myHouse.showInfo();     // "色: 青, 部屋数: 3, 庭: あり"
        
        friendHouse.openDoor(); // "赤の家のドアを開けました"
        friendHouse.showInfo(); // "色: 赤, 部屋数: 2, 庭: なし"
    }
}
```

**ポイント**:
- **クラス** = 設計図（House）
- **インスタンス** = 実際に作られたもの（myHouse, friendHouse）
- **フィールド** = 特徴・状態（color, rooms, hasGarden）
- **メソッド** = できること・機能（openDoor, showInfo）

### 1.2 継承（extends）- 設計図を改良する

**「家」の設計図を元に「マンション」を作ってみましょう**

```java
// 🏠 基本の家（親クラス）
public class House {
    protected String color;  // protected = 子クラスからアクセス可能
    protected int rooms;
    
    public House(String color, int rooms) {
        this.color = color;
        this.rooms = rooms;
    }
    
    public void openDoor() {
        System.out.println(color + "の建物のドアを開けました");
    }
    
    public void showInfo() {
        System.out.println("色: " + color + ", 部屋数: " + rooms);
    }
}

// 🏢 マンション（子クラス）- 家の機能を引き継いで拡張
public class Apartment extends House {
    private int floor;        // マンション特有の特徴
    private boolean hasElevator;
    
    // コンストラクタ
    public Apartment(String color, int rooms, int floor, boolean hasElevator) {
        super(color, rooms);  // 親クラスのコンストラクタを呼び出し
        this.floor = floor;
        this.hasElevator = hasElevator;
    }
    
    // 親のメソッドを改良（オーバーライド）
    @Override
    public void showInfo() {
        super.showInfo();  // 親の処理を実行
        System.out.println("階数: " + floor + ", エレベーター: " + (hasElevator ? "あり" : "なし"));
    }
    
    // マンション特有の機能
    public void useElevator() {
        if (hasElevator) {
            System.out.println("エレベーターを使いました");
        } else {
            System.out.println("階段を使います");
        }
    }
}

// 使用例
public class Main {
    public static void main(String[] args) {
        House house = new House("青", 3);
        Apartment apartment = new Apartment("白", 10, 5, true);
        
        house.openDoor();      // 家の機能
        house.showInfo();      // 家の情報表示
        
        apartment.openDoor();  // 継承した家の機能
        apartment.showInfo();  // 拡張された情報表示
        apartment.useElevator(); // マンション特有の機能
    }
}
```

**継承のメリット**:
- 📝 **コードの再利用**: 共通部分を一度書けばOK
- 🔧 **拡張性**: 元の機能を残しながら新機能追加
- 🛡️ **保守性**: 共通部分の修正が全体に反映

---

## 📚 第2章: インターフェース（interface）- 約束事を決める

### 2.1 インターフェースって何？

**「できること」の約束を決める仕組み**

```java
// 🎵 楽器の約束事（インターフェース）
public interface Instrument {
    void play();        // 演奏する（必ず実装が必要）
    void stop();        // 停止する（必ず実装が必要）
    String getSound();  // 音を返す（必ず実装が必要）
}

// 🎹 ピアノ - 楽器の約束を守って実装
public class Piano implements Instrument {
    private boolean isPlaying = false;
    
    @Override
    public void play() {
        isPlaying = true;
        System.out.println("ピアノを弾いています: " + getSound());
    }
    
    @Override
    public void stop() {
        isPlaying = false;
        System.out.println("ピアノの演奏を停止しました");
    }
    
    @Override
    public String getSound() {
        return "ポロロ〜ン♪";
    }
}

// 🎸 ギター - 同じ約束を違う方法で実装
public class Guitar implements Instrument {
    private boolean isPlaying = false;
    
    @Override
    public void play() {
        isPlaying = true;
        System.out.println("ギターを弾いています: " + getSound());
    }
    
    @Override
    public void stop() {
        isPlaying = false;
        System.out.println("ギターの演奏を停止しました");
    }
    
    @Override
    public String getSound() {
        return "ジャカジャカ♪";
    }
}

// 🎼 楽器演奏システム
public class MusicSystem {
    // どんな楽器でも演奏できる（多態性）
    public void performConcert(Instrument instrument) {
        System.out.println("コンサート開始！");
        instrument.play();
        
        // 5秒待つ（実際はThread.sleep使用）
        System.out.println("...5秒後...");
        
        instrument.stop();
        System.out.println("コンサート終了！");
    }
}

// 使用例
public class Main {
    public static void main(String[] args) {
        MusicSystem system = new MusicSystem();
        
        Instrument piano = new Piano();
        Instrument guitar = new Guitar();
        
        system.performConcert(piano);  // ピアノコンサート
        system.performConcert(guitar); // ギターコンサート
    }
}
```

**インターフェースのメリット**:
- 📋 **統一された操作**: 異なる実装でも同じ方法で使える
- 🔄 **交換可能性**: 実装を変更しても使う側は影響なし
- 🎯 **設計の明確化**: 何ができるかが明確

### 2.2 抽象クラス（abstract）- 半完成の設計図

**「共通部分は作るけど、一部は子クラスに任せる」仕組み**

```java
// 🚗 抽象的な車クラス（半完成の設計図）
public abstract class Vehicle {
    protected String brand;
    protected String color;
    protected boolean isRunning = false;
    
    // 通常のコンストラクタ
    public Vehicle(String brand, String color) {
        this.brand = brand;
        this.color = color;
    }
    
    // 共通の機能（完成済み）
    public void start() {
        if (!isRunning) {
            isRunning = true;
            System.out.println(brand + "のエンジンをかけました");
            playEngineSound(); // 抽象メソッドを呼び出し
        }
    }
    
    public void stop() {
        if (isRunning) {
            isRunning = false;
            System.out.println(brand + "のエンジンを停止しました");
        }
    }
    
    public void showInfo() {
        System.out.println("ブランド: " + brand + ", 色: " + color + ", 稼働中: " + isRunning);
    }
    
    // 抽象メソッド（子クラスで必ず実装）
    public abstract void playEngineSound();
    public abstract void accelerate();
    public abstract int getMaxSpeed();
}

// 🏎️ スポーツカー
public class SportsCar extends Vehicle {
    public SportsCar(String brand, String color) {
        super(brand, color);
    }
    
    @Override
    public void playEngineSound() {
        System.out.println("ヴゥゥゥゥーーン！！！");
    }
    
    @Override
    public void accelerate() {
        if (isRunning) {
            System.out.println("スポーツカーが急加速！");
        }
    }
    
    @Override
    public int getMaxSpeed() {
        return 300; // 時速300km
    }
}

// 🚐 バン
public class Van extends Vehicle {
    public Van(String brand, String color) {
        super(brand, color);
    }
    
    @Override
    public void playEngineSound() {
        System.out.println("ブルルル〜");
    }
    
    @Override
    public void accelerate() {
        if (isRunning) {
            System.out.println("バンがゆっくり加速");
        }
    }
    
    @Override
    public int getMaxSpeed() {
        return 120; // 時速120km
    }
}

// 使用例
public class Main {
    public static void main(String[] args) {
        Vehicle sports = new SportsCar("フェラーリ", "赤");
        Vehicle van = new Van("トヨタ", "白");
        
        // 共通の操作
        sports.start();        // エンジンかけて音も鳴る
        sports.accelerate();   // スポーツカー特有の加速
        sports.showInfo();     // 情報表示
        
        van.start();           // エンジンかけて音も鳴る
        van.accelerate();      // バン特有の加速
        van.showInfo();        // 情報表示
    }
}
```

**抽象クラス vs インターフェース**:

| 特徴 | 抽象クラス | インターフェース |
|------|------------|------------------|
| 完成したメソッド | ✅ 持てる | ❌ 持てない（※Java8以降は例外） |
| 抽象メソッド | ✅ 持てる | ✅ 持てる |
| フィールド | ✅ 持てる | ❌ 持てない |
| 継承 | 1つだけ | 複数可能 |
| 用途 | 共通機能がある場合 | 約束事だけ決める場合 |

---

## 📚 第3章: enum（列挙型）- 決まった選択肢を表現

### 3.1 enumって何？

**「決まった選択肢の中から選ぶ」仕組み**

```java
// 🌈 色の選択肢（enum）
public enum Color {
    RED,      // 赤
    GREEN,    // 緑
    BLUE,     // 青
    YELLOW,   // 黄
    BLACK,    // 黒
    WHITE     // 白
}

// 📏 サイズの選択肢
public enum Size {
    SMALL,    // S
    MEDIUM,   // M
    LARGE,    // L
    EXTRA_LARGE // XL
}

// 👕 Tシャツクラス
public class TShirt {
    private Color color;
    private Size size;
    private int price;
    
    public TShirt(Color color, Size size) {
        this.color = color;
        this.size = size;
        this.price = calculatePrice();
    }
    
    private int calculatePrice() {
        int basePrice = 1000;
        
        // サイズによる価格調整
        switch (size) {
            case SMALL:
            case MEDIUM:
                return basePrice;
            case LARGE:
                return basePrice + 200;
            case EXTRA_LARGE:
                return basePrice + 400;
            default:
                return basePrice;
        }
    }
    
    public void showInfo() {
        System.out.println("Tシャツ - 色: " + color + ", サイズ: " + size + ", 価格: " + price + "円");
    }
}

// 使用例
public class Main {
    public static void main(String[] args) {
        // enumを使用（間違った値を入れることができない）
        TShirt shirt1 = new TShirt(Color.RED, Size.LARGE);
        TShirt shirt2 = new TShirt(Color.BLUE, Size.SMALL);
        
        shirt1.showInfo(); // Tシャツ - 色: RED, サイズ: LARGE, 価格: 1200円
        shirt2.showInfo(); // Tシャツ - 色: BLUE, サイズ: SMALL, 価格: 1000円
        
        // ❌ これはコンパイルエラーになる（存在しない値）
        // TShirt badShirt = new TShirt("紫", "XXL");
    }
}
```

### 3.2 enumに機能を持たせる

**enumに値や機能を追加できます**

```java
// 🎮 ゲームの難易度設定
public enum Difficulty {
    EASY("簡単", 1.0f, 100),
    NORMAL("普通", 1.5f, 70),
    HARD("難しい", 2.0f, 50),
    EXPERT("エキスパート", 3.0f, 20);
    
    private final String displayName;  // 表示名
    private final float damageMultiplier; // ダメージ倍率
    private final int playerHealth;    // プレイヤー体力
    
    // enumのコンストラクタ
    Difficulty(String displayName, float damageMultiplier, int playerHealth) {
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
        this.playerHealth = playerHealth;
    }
    
    // ゲッターメソッド
    public String getDisplayName() { return displayName; }
    public float getDamageMultiplier() { return damageMultiplier; }
    public int getPlayerHealth() { return playerHealth; }
    
    // ダメージ計算メソッド
    public int calculateDamage(int baseDamage) {
        return Math.round(baseDamage * damageMultiplier);
    }
    
    // 表示用メソッド
    public void showInfo() {
        System.out.printf("難易度: %s (ダメージ倍率: %.1fx, 体力: %d)\n", 
            displayName, damageMultiplier, playerHealth);
    }
}

// ゲームクラス
public class Game {
    private Difficulty currentDifficulty;
    
    public Game(Difficulty difficulty) {
        this.currentDifficulty = difficulty;
    }
    
    public void startGame() {
        System.out.println("=== ゲーム開始 ===");
        currentDifficulty.showInfo();
        
        // 敵の攻撃をシミュレート
        int enemyAttack = 10; // 基本ダメージ
        int actualDamage = currentDifficulty.calculateDamage(enemyAttack);
        
        System.out.println("敵の攻撃！ " + actualDamage + "ダメージを受けました");
    }
    
    public void changeDifficulty(Difficulty newDifficulty) {
        this.currentDifficulty = newDifficulty;
        System.out.println("難易度を " + newDifficulty.getDisplayName() + " に変更しました");
    }
}

// 使用例
public class Main {
    public static void main(String[] args) {
        // 全ての難易度を表示
        System.out.println("=== 利用可能な難易度 ===");
        for (Difficulty diff : Difficulty.values()) {
            diff.showInfo();
        }
        
        System.out.println();
        
        // ゲーム開始
        Game game = new Game(Difficulty.NORMAL);
        game.startGame();
        
        // 難易度変更
        game.changeDifficulty(Difficulty.EXPERT);
        game.startGame();
    }
}
```

**enumの利点**:
- ✅ **タイプセーフ**: 間違った値を入れられない
- 📋 **値の管理**: 関連する値をまとめて管理
- 🔒 **変更防止**: 勝手に値を変えられない
- 📖 **可読性**: コードが読みやすい

---

## 📚 第4章: Minecraftプラグインでの応用

### 4.1 アイテムレアリティシステム

**enumを使ってアイテムのレアリティを表現**

```java
// 💎 アイテムレアリティ（enum）
public enum ItemRarity {
    COMMON("コモン", ChatColor.WHITE, 1.0f),
    UNCOMMON("アンコモン", ChatColor.GREEN, 0.7f),
    RARE("レア", ChatColor.BLUE, 0.3f),
    EPIC("エピック", ChatColor.DARK_PURPLE, 0.1f),
    LEGENDARY("レジェンダリー", ChatColor.GOLD, 0.05f);
    
    private final String displayName;
    private final ChatColor color;
    private final float dropChance; // ドロップ確率
    
    ItemRarity(String displayName, ChatColor color, float dropChance) {
        this.displayName = displayName;
        this.color = color;
        this.dropChance = dropChance;
    }
    
    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public float getDropChance() { return dropChance; }
    
    // カラー付きの表示名を取得
    public String getColoredName() {
        return color + displayName;
    }
    
    // レアリティに応じたエフェクト
    public void playEffect(Player player) {
        Location loc = player.getLocation();
        
        switch (this) {
            case LEGENDARY:
                player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                loc.getWorld().spawnParticle(Particle.TOTEM, loc, 50, 1, 1, 1, 0.1);
                break;
            case EPIC:
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                loc.getWorld().spawnParticle(Particle.ENCHANT, loc, 30, 0.5, 0.5, 0.5, 0.2);
                break;
            case RARE:
                player.playSound(loc, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
                loc.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc, 20, 0.3, 0.3, 0.3, 0.1);
                break;
            default:
                player.playSound(loc, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
                break;
        }
    }
}
```

### 4.2 抽象カスタムアイテムクラス

**継承・抽象クラス・インターフェースを組み合わせた設計**

```java
// 🎯 カスタムアイテムの基本契約（インターフェース）
public interface CustomItem {
    ItemStack createItem();
    boolean isCustomItem(ItemStack item);
    String getItemId();
    ItemRarity getRarity();
}

// 🏗️ 抽象カスタムアイテムクラス
public abstract class AbstractCustomItem implements CustomItem, Listener {
    protected final JavaPlugin plugin;
    protected final String itemId;
    protected final String displayName;
    protected final Material material;
    protected final ItemRarity rarity;
    protected final List<String> lore;
    
    // コンストラクタ
    public AbstractCustomItem(JavaPlugin plugin, String itemId, String displayName, 
                            Material material, ItemRarity rarity) {
        this.plugin = plugin;
        this.itemId = itemId;
        this.displayName = displayName;
        this.material = material;
        this.rarity = rarity;
        this.lore = new ArrayList<>();
        
        // リスナー自動登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    // 共通実装（完成済み）
    @Override
    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // レアリティに応じた色付き名前
            meta.setDisplayName(rarity.getColor() + displayName);
            
            // 説明文を設定
            List<String> finalLore = new ArrayList<>();
            finalLore.addAll(lore);
            finalLore.add("");
            finalLore.add(ChatColor.GRAY + "レアリティ: " + rarity.getColoredName());
            finalLore.add(ChatColor.DARK_GRAY + "ID: " + itemId);
            meta.setLore(finalLore);
            
            // カスタムアイテムの印をつける
            meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "custom_item_id"),
                PersistentDataType.STRING,
                itemId
            );
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    @Override
    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        String storedId = meta.getPersistentDataContainer().get(
            new NamespacedKey(plugin, "custom_item_id"),
            PersistentDataType.STRING
        );
        
        return itemId.equals(storedId);
    }
    
    // ゲッター（実装済み）
    @Override
    public String getItemId() { return itemId; }
    @Override
    public ItemRarity getRarity() { return rarity; }
    
    // 説明文追加メソッド
    protected void addLore(String line) {
        lore.add(ChatColor.GRAY + line);
    }
    
    // 共通イベントハンドラ
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (event.getAction().name().contains("RIGHT_CLICK") && isCustomItem(item)) {
            event.setCancelled(true);
            onRightClick(player, item);
        }
    }
    
    // 抽象メソッド（子クラスで実装必須）
    protected abstract void onRightClick(Player player, ItemStack item);
}
```

### 4.3 具体的なカスタムアイテム実装

**抽象クラスを継承した実際のアイテム**

```java
// ⚡ 雷の杖
public class LightningWand extends AbstractCustomItem {
    
    public LightningWand(JavaPlugin plugin) {
        super(plugin, "lightning_wand", "雷の杖", Material.BLAZE_ROD, ItemRarity.EPIC);
        
        // 説明文を追加
        addLore("右クリックで雷を呼び出す");
        addLore("クールダウン: 5秒");
        addLore("");
        addLore(ChatColor.YELLOW + "⚡ 強力な雷の力を秘めた杖");
    }
    
    @Override
    protected void onRightClick(Player player, ItemStack item) {
        // クールダウンチェック（省略）
        
        // プレイヤーが見ている場所に雷を落とす
        Block targetBlock = player.getTargetBlock(null, 50);
        Location lightningLocation = targetBlock.getLocation();
        
        // 雷を落とす
        player.getWorld().strikeLightning(lightningLocation);
        
        // エフェクトとメッセージ
        player.sendMessage(rarity.getColor() + "⚡ 雷の力を解放しました！");
        rarity.playEffect(player);
        
        // 耐久度を減らす（アイテム消費）
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
}

// 🌟 回復のオーブ
public class HealingOrb extends AbstractCustomItem {
    
    public HealingOrb(JavaPlugin plugin) {
        super(plugin, "healing_orb", "回復のオーブ", Material.MAGMA_CREAM, ItemRarity.RARE);
        
        addLore("右クリックで体力を回復");
        addLore("回復量: 5ハート");
        addLore("");
        addLore(ChatColor.GREEN + "💚 生命の力を宿したオーブ");
    }
    
    @Override
    protected void onRightClick(Player player, ItemStack item) {
        // 体力回復
        double currentHealth = player.getHealth();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double newHealth = Math.min(maxHealth, currentHealth + 10.0); // 5ハート回復
        
        player.setHealth(newHealth);
        
        // エフェクトとメッセージ
        player.sendMessage(rarity.getColor() + "💚 体力が回復しました！");
        rarity.playEffect(player);
        
        // 追加エフェクト
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        
        // アイテム消費
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
}

// 🚀 テレポートブーツ
public class TeleportBoots extends AbstractCustomItem {
    
    public TeleportBoots(JavaPlugin plugin) {
        super(plugin, "teleport_boots", "瞬間移動ブーツ", Material.DIAMOND_BOOTS, ItemRarity.LEGENDARY);
        
        addLore("装備して右クリックでテレポート");
        addLore("範囲: 20ブロック");
        addLore("");
        addLore(ChatColor.LIGHT_PURPLE + "🚀 空間を歪める神秘のブーツ");
    }
    
    @Override
    protected void onRightClick(Player player, ItemStack item) {
        // 装備している場合のみ動作
        ItemStack boots = player.getInventory().getBoots();
        if (!isCustomItem(boots)) {
            player.sendMessage(ChatColor.RED + "ブーツを装備してください！");
            return;
        }
        
        // テレポート先を決定
        Block targetBlock = player.getTargetBlock(null, 20);
        if (targetBlock.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "テレポート先が見つかりません！");
            return;
        }
        
        Location teleportLocation = targetBlock.getLocation().add(0, 1, 0);
        
        // テレポート実行
        Location oldLocation = player.getLocation();
        player.teleport(teleportLocation);
        
        // エフェクト
        player.sendMessage(rarity.getColor() + "🚀 瞬間移動しました！");
        rarity.playEffect(player);
        
        // 元の場所と新しい場所にパーティクル
        oldLocation.getWorld().spawnParticle(Particle.PORTAL, oldLocation, 30, 0.5, 0.5, 0.5, 0.1);
        teleportLocation.getWorld().spawnParticle(Particle.PORTAL, teleportLocation, 30, 0.5, 0.5, 0.5, 0.1);
    }
}
```

### 4.4 アイテム管理システム

**Registryパターンでアイテムを管理**

```java
// 📦 カスタムアイテム管理システム
public class CustomItemManager {
    private final Map<String, AbstractCustomItem> items = new HashMap<>();
    private final JavaPlugin plugin;
    
    public CustomItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeItems();
    }
    
    private void initializeItems() {
        // アイテムを登録
        registerItem(new LightningWand(plugin));
        registerItem(new HealingOrb(plugin));
        registerItem(new TeleportBoots(plugin));
        
        plugin.getLogger().info("カスタムアイテム " + items.size() + "種類を登録しました");
    }
    
    public void registerItem(AbstractCustomItem item) {
        items.put(item.getItemId(), item);
        plugin.getLogger().info("カスタムアイテム登録: " + item.getItemId() + " (" + item.getRarity().getDisplayName() + ")");
    }
    
    public AbstractCustomItem getItem(String itemId) {
        return items.get(itemId);
    }
    
    public ItemStack createItem(String itemId) {
        AbstractCustomItem item = items.get(itemId);
        if (item == null) return null;
        return item.createItem();
    }
    
    public List<String> getAllItemIds() {
        return new ArrayList<>(items.keySet());
    }
    
    // レアリティ別でアイテムを取得
    public List<AbstractCustomItem> getItemsByRarity(ItemRarity rarity) {
        return items.values().stream()
                .filter(item -> item.getRarity() == rarity)
                .collect(Collectors.toList());
    }
}
```

### 4.5 コマンドシステム

**アイテム配布コマンド**

```java
// 📋 カスタムアイテム配布コマンド
public class CustomItemCommand implements CommandExecutor, TabCompleter {
    private final CustomItemManager itemManager;
    
    public CustomItemCommand(CustomItemManager itemManager) {
        this.itemManager = itemManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("プレイヤーのみ実行可能です");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showAvailableItems(player);
            return true;
        }
        
        String itemId = args[0].toLowerCase();
        ItemStack item = itemManager.createItem(itemId);
        
        if (item == null) {
            player.sendMessage(ChatColor.RED + "アイテムが見つかりません: " + itemId);
            return true;
        }
        
        // アイテムを渡す
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendMessage(ChatColor.YELLOW + "インベントリが満杯だったので、足元にドロップしました");
        } else {
            player.getInventory().addItem(item);
        }
        
        AbstractCustomItem customItem = itemManager.getItem(itemId);
        player.sendMessage(ChatColor.GREEN + "✓ " + customItem.getRarity().getColoredName() + " アイテムを受け取りました: " + 
                          customItem.getRarity().getColor() + item.getItemMeta().getDisplayName());
        
        return true;
    }
    
    private void showAvailableItems(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== 利用可能なカスタムアイテム ===");
        
        for (ItemRarity rarity : ItemRarity.values()) {
            List<AbstractCustomItem> rarityItems = itemManager.getItemsByRarity(rarity);
            if (!rarityItems.isEmpty()) {
                player.sendMessage("");
                player.sendMessage(rarity.getColoredName() + " (" + rarityItems.size() + "種類):");
                
                for (AbstractCustomItem item : rarityItems) {
                    player.sendMessage("  " + ChatColor.GRAY + "- " + item.getItemId() + 
                                     " " + ChatColor.RESET + "(" + item.createItem().getItemMeta().getDisplayName() + ChatColor.RESET + ")");
                }
            }
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "使用方法: /customitem <アイテムID>");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return itemManager.getAllItemIds().stream()
                    .filter(id -> id.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
```

---

## 📚 第5章: 理解度チェック問題

### 5.1 基礎問題

**問題1**: 以下のコードの`abstract`キーワードを外すとどうなりますか？

```java
public abstract class Animal {
    public abstract void makeSound();
}
```

<details>
<summary>答えを見る</summary>

コンパイルエラーになります。`makeSound()`メソッドに`abstract`がついているのに、クラスに`abstract`がないためです。

解決方法:
1. クラスから`abstract`を外して、`makeSound()`メソッドに実装を追加
2. またはクラスを`abstract`のままにする

</details>

**問題2**: enumの利点を3つ答えてください。

<details>
<summary>答えを見る</summary>

1. **タイプセーフ**: 間違った値を入れることができない
2. **値の管理**: 関連する定数をまとめて管理できる
3. **可読性**: コードが読みやすく、意図が明確になる

</details>

### 5.2 応用問題

**問題3**: 次の要件を満たすカスタムアイテムを作成してください：

- アイテム名: "炎の剣"
- 素材: DIAMOND_SWORD
- レアリティ: EPIC
- 機能: 右クリックで周囲5ブロックの敵に炎上効果

<details>
<summary>答えを見る</summary>

```java
public class FireSword extends AbstractCustomItem {
    
    public FireSword(JavaPlugin plugin) {
        super(plugin, "fire_sword", "炎の剣", Material.DIAMOND_SWORD, ItemRarity.EPIC);
        
        addLore("右クリックで周囲の敵を燃やす");
        addLore("範囲: 5ブロック");
        addLore("");
        addLore(ChatColor.RED + "🔥 燃え盛る炎を宿した剣");
    }
    
    @Override
    protected void onRightClick(Player player, ItemStack item) {
        Location playerLoc = player.getLocation();
        
        // 周囲5ブロックの敵を検索
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Monster) {
                Monster monster = (Monster) entity;
                
                // 炎上効果を付与
                monster.setFireTicks(100); // 5秒間燃える
                
                // エフェクト
                entity.getWorld().spawnParticle(Particle.FLAME, 
                    entity.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
            }
        }
        
        player.sendMessage(rarity.getColor() + "🔥 炎の力を解放しました！");
        rarity.playEffect(player);
    }
}
```

</details>

---

## 🎯 まとめ: なぜこの設計が優れているのか

### 1. **継承の活用**
- `AbstractCustomItem`で共通機能を実装
- 子クラスは特有の機能のみに集中
- コードの重複を大幅に削減

### 2. **enumによる安全性**
- `ItemRarity`で間違った値の入力を防止
- レアリティに関連する全ての情報を一箇所で管理
- 新しいレアリティの追加が簡単

### 3. **インターフェースによる統一性**
- `CustomItem`インターフェースで統一された操作
- 異なる実装でも同じ方法で使用可能
- テストやデバッグが容易

### 4. **抽象クラスの効果的活用**
- 共通部分は実装済み（`createItem`など）
- 特殊部分は抽象メソッド（`onRightClick`）
- 新しいアイテム追加時の工数削減

### 5. **Registryパターンによる管理**
- 全アイテムの一元管理
- 動的なアイテム取得・生成
- 拡張性の確保

---

## 🚀 次のステップ

このガイドでJavaの基本構造からカスタムアイテムシステムまでを学びました。次は：

1. **実際にコードを書く**: 理解したことを実践
2. **独自アイテムの作成**: オリジナルのカスタムアイテム開発
3. **より高度な機能**: データベース連携、非同期処理など
4. **他の設計パターン学習**: Factory、Observer、Commandパターンなど

**重要**: 一度に全てを理解しようとせず、段階的にスキルアップしていきましょう！

---

*Happy Coding! 🎮✨*