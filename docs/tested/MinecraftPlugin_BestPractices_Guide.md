# Minecraftプラグイン開発 ベストプラクティスガイド

## 📖 はじめに

このガイドでは、実際の大規模プラグインから学んだ**再利用可能な設計パターン**を、初学者にも分かりやすく解説します。小規模なプラグイン開発から始めて、段階的にスキルアップできるように構成されています。

---

## 🎯 学習の進め方

### Step 1: 基礎パターンを1つずつマスター
### Step 2: 小さなプラグインで実際に使ってみる  
### Step 3: 慣れてから次のパターンに進む

**重要**: 全てを一度に覚える必要はありません。必要な時に必要な部分を参照してください。

---

## 🚀 レベル1: 最初に覚えたい基礎パターン

### 1.1 プレイヤーの安全性チェック - 最優先で覚えよう

**なぜ重要？**: Minecraftではプレイヤーがログアウトしたり死んでいる可能性があります

```java
// 👍 安全なプレイヤーチェック
public static boolean isPlayerValid(Player player) {
    return player != null &&           // プレイヤーが存在する
           player.isOnline() &&         // オンライン状態
           !player.isDead();            // 生きている
}

// 使用例
public void giveReward(Player player) {
    if (!isPlayerValid(player)) {
        return; // 安全でない場合は何もしない
    }
    
    // ここに報酬を渡すコード
    player.sendMessage("報酬を受け取りました！");
}
```

**実際の使用場面**:
- アイテムを渡す前
- メッセージを送る前  
- エフェクトをかける前
- **基本的に、プレイヤーに何かする前は必ずチェック**

### 1.2 安全なアイテム配布 - インベントリ満杯対策

**なぜ重要？**: プレイヤーのインベントリが満杯だとアイテムが消える

```java
// 👍 安全なアイテム配布
public static void safeGiveItem(Player player, ItemStack item) {
    if (!isPlayerValid(player)) return;
    
    // インベントリに空きがあるかチェック
    if (player.getInventory().firstEmpty() == -1) {
        // 空きがない場合は足元にドロップ
        player.getWorld().dropItem(player.getLocation(), item);
        player.sendMessage(ChatColor.YELLOW + "インベントリが満杯だったので、アイテムを足元に落としました！");
    } else {
        // 空きがある場合は普通に追加
        player.getInventory().addItem(item);
        player.sendMessage(ChatColor.GREEN + "アイテムを受け取りました！");
    }
}

// 使用例
ItemStack diamond = new ItemStack(Material.DIAMOND, 5);
safeGiveItem(player, diamond);
```

### 1.3 統一されたメッセージとサウンド

**なぜ重要？**: プラグイン全体で一貫したユーザー体験を提供

```java
// 👍 統一されたフィードバック
public class PluginUX {
    
    // 成功時の統一表示
    public static void showSuccess(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "✓ " + message);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }
    
    // エラー時の統一表示
    public static void showError(Player player, String message) {
        player.sendMessage(ChatColor.RED + "✗ " + message);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }
    
    // 情報表示
    public static void showInfo(Player player, String message) {
        player.sendMessage(ChatColor.YELLOW + "ℹ " + message);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.0f);
    }
}

// 使用例
PluginUX.showSuccess(player, "クエストを完了しました！");
PluginUX.showError(player, "アイテムが足りません");
PluginUX.showInfo(player, "新しいクエストが利用可能です");
```

---

## 🛠️ レベル2: カスタムアイテムの作り方

### 2.1 基本的なカスタムアイテムクラス

**これを覚えると**: 右クリックで特殊効果があるアイテムが作れる

```java
// AbstractCustomItemV2を簡略化した基本版
public abstract class CustomItem implements Listener {
    protected final JavaPlugin plugin;
    protected final String itemName;
    protected final Material material;
    
    public CustomItem(JavaPlugin plugin, String itemName, Material material) {
        this.plugin = plugin;
        this.itemName = itemName;
        this.material = material;
        
        // このアイテムのイベントリスナーを自動登録
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    // カスタムアイテムを作成
    public ItemStack createItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + itemName);
            
            // カスタムアイテムの印をつける（NBT）
            meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "custom_item"),
                PersistentDataType.STRING,
                getClass().getSimpleName()
            );
            
            item.setItemMeta(meta);
        }
        return item;
    }
    
    // このアイテムかどうかチェック
    protected boolean isThisItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(
            new NamespacedKey(plugin, "custom_item"),
            PersistentDataType.STRING
        );
    }
    
    // 右クリック時の処理（継承先で実装）
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // 右クリックかつ、このカスタムアイテムの場合
        if (event.getAction().name().contains("RIGHT_CLICK") && isThisItem(item)) {
            event.setCancelled(true); // 通常の右クリック処理をキャンセル
            onRightClick(player); // カスタム処理を実行
        }
    }
    
    // 継承先で実装する処理
    protected abstract void onRightClick(Player player);
}
```

### 2.2 実際のカスタムアイテム例

```java
// 🌟 テレポート杖の例
public class TeleportWand extends CustomItem {
    
    public TeleportWand(JavaPlugin plugin) {
        super(plugin, "テレポート杖", Material.BLAZE_ROD);
    }
    
    @Override
    protected void onRightClick(Player player) {
        if (!isPlayerValid(player)) return;
        
        // プレイヤーが見ている方向に10ブロックテレポート
        Location current = player.getLocation();
        Vector direction = current.getDirection().multiply(10);
        Location newLocation = current.add(direction);
        
        // 安全な場所にテレポート（地面の上）
        newLocation.setY(newLocation.getWorld().getHighestBlockYAt(newLocation) + 1);
        
        player.teleport(newLocation);
        PluginUX.showSuccess(player, "テレポートしました！");
        
        // パーティクルエフェクト
        player.getWorld().spawnParticle(Particle.PORTAL, current, 20, 0.5, 0.5, 0.5, 0.1);
        player.getWorld().spawnParticle(Particle.PORTAL, newLocation, 20, 0.5, 0.5, 0.5, 0.1);
    }
}

// 🍎 回復アイテムの例
public class HealingApple extends CustomItem {
    
    public HealingApple(JavaPlugin plugin) {
        super(plugin, "治癒のリンゴ", Material.GOLDEN_APPLE);
    }
    
    @Override
    protected void onRightClick(Player player) {
        if (!isPlayerValid(player)) return;
        
        // 体力を回復
        double currentHealth = player.getHealth();
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        double newHealth = Math.min(maxHealth, currentHealth + 10.0); // 5ハート回復
        
        player.setHealth(newHealth);
        PluginUX.showSuccess(player, "体力が回復しました！");
        
        // アイテムを1つ消費
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
    }
}
```

### 2.3 カスタムアイテムの登録と使用

```java
// メインプラグインクラスで登録
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // カスタムアイテムを作成（自動的にリスナー登録される）
        TeleportWand teleportWand = new TeleportWand(this);
        HealingApple healingApple = new HealingApple(this);
        
        // コマンドでアイテムを配布
        getCommand("give").setExecutor(new GiveItemCommand(teleportWand, healingApple));
    }
}

// アイテム配布コマンド
public class GiveItemCommand implements CommandExecutor {
    private final TeleportWand teleportWand;
    private final HealingApple healingApple;
    
    public GiveItemCommand(TeleportWand teleportWand, HealingApple healingApple) {
        this.teleportWand = teleportWand;
        this.healingApple = healingApple;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("プレイヤーのみ実行可能です");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            PluginUX.showError(player, "アイテム名を指定してください: /give <wand|apple>");
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "wand":
                safeGiveItem(player, teleportWand.createItem());
                break;
            case "apple":
                safeGiveItem(player, healingApple.createItem());
                break;
            default:
                PluginUX.showError(player, "不明なアイテム: " + args[0]);
                return true;
        }
        
        return true;
    }
}
```

---

## 📊 レベル3: データ管理とレジストリパターン

### 3.1 シンプルなレジストリシステム

**なぜ必要？**: アイテムやデータが増えても管理しやすくする

```java
// 👍 何でも管理できるシンプルなレジストリ
public class SimpleRegistry<T> {
    private final Map<String, T> items = new HashMap<>();
    
    // アイテムを登録
    public void register(String id, T item) {
        items.put(id, item);
        System.out.println("登録しました: " + id);
    }
    
    // IDでアイテムを取得
    public T get(String id) {
        return items.get(id);
    }
    
    // 全てのアイテムを取得
    public Collection<T> getAll() {
        return items.values();
    }
    
    // 登録されているかチェック
    public boolean has(String id) {
        return items.containsKey(id);
    }
    
    // 全てのIDを取得
    public Set<String> getAllIds() {
        return items.keySet();
    }
}
```

### 3.2 レジストリを使ったアイテム管理

```java
// アイテム管理クラス
public class ItemManager {
    private final SimpleRegistry<CustomItem> items = new SimpleRegistry<>();
    private final JavaPlugin plugin;
    
    public ItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeItems();
    }
    
    private void initializeItems() {
        // アイテムを登録
        register("teleport_wand", new TeleportWand(plugin));
        register("healing_apple", new HealingApple(plugin));
        register("speed_boots", new SpeedBoots(plugin));
        register("fire_sword", new FireSword(plugin));
        
        plugin.getLogger().info("カスタムアイテム " + items.getAllIds().size() + "種類を登録しました");
    }
    
    public void register(String id, CustomItem item) {
        items.register(id, item);
    }
    
    public CustomItem getItem(String id) {
        return items.get(id);
    }
    
    public ItemStack createItem(String id) {
        CustomItem item = items.get(id);
        if (item == null) {
            return null;
        }
        return item.createItem();
    }
    
    // 全てのアイテムIDを取得（コマンド補完で使用）
    public List<String> getAllItemIds() {
        return new ArrayList<>(items.getAllIds());
    }
}

// 改良されたコマンド
public class ImprovedGiveCommand implements CommandExecutor, TabCompleter {
    private final ItemManager itemManager;
    
    public ImprovedGiveCommand(ItemManager itemManager) {
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
            PluginUX.showInfo(player, "利用可能なアイテム: " + String.join(", ", itemManager.getAllItemIds()));
            return true;
        }
        
        String itemId = args[0].toLowerCase();
        ItemStack item = itemManager.createItem(itemId);
        
        if (item == null) {
            PluginUX.showError(player, "アイテムが見つかりません: " + itemId);
            return true;
        }
        
        safeGiveItem(player, item);
        return true;
    }
    
    // Tab補完機能
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

## 🎰 レベル4: 確率システム（ガチャ・ドロップ）

### 4.1 重み付き確率選択システム

**使用場面**: ガチャ、モンスタードロップ、ランダム報酬

```java
// 👍 数学的に正確な確率システム
public class WeightedSelector<T> {
    private final List<WeightedItem<T>> items = new ArrayList<>();
    private final Random random = new Random();
    
    // アイテムと重みを追加
    public void add(T item, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("重みは1以上である必要があります");
        }
        items.add(new WeightedItem<>(item, weight));
    }
    
    // 重み付きランダム選択
    public T selectRandom() {
        if (items.isEmpty()) return null;
        
        // 総重みを計算
        int totalWeight = items.stream().mapToInt(item -> item.weight).sum();
        int randomValue = random.nextInt(totalWeight);
        
        // 重みに基づいて選択
        int currentWeight = 0;
        for (WeightedItem<T> item : items) {
            currentWeight += item.weight;
            if (randomValue < currentWeight) {
                return item.item;
            }
        }
        
        return null; // 通常ここには到達しない
    }
    
    // 確率を表示（デバッグ用）
    public void printProbabilities() {
        int totalWeight = items.stream().mapToInt(item -> item.weight).sum();
        
        System.out.println("=== 確率一覧 ===");
        for (WeightedItem<T> item : items) {
            double probability = (double) item.weight / totalWeight * 100;
            System.out.printf("%s: %.2f%% (重み:%d)\n", 
                item.item.toString(), probability, item.weight);
        }
    }
    
    // 内部クラス
    private static class WeightedItem<T> {
        final T item;
        final int weight;
        
        WeightedItem(T item, int weight) {
            this.item = item;
            this.weight = weight;
        }
    }
}
```

### 4.2 ガチャシステムの実装例

```java
// ガチャのアイテム定義
public enum GachaItem {
    DIRT("土", Material.DIRT, 1000),           // 50% (1000/2000)
    IRON("鉄インゴット", Material.IRON_INGOT, 600),   // 30% (600/2000)
    GOLD("金インゴット", Material.GOLD_INGOT, 300),   // 15% (300/2000)
    DIAMOND("ダイヤモンド", Material.DIAMOND, 90),    // 4.5% (90/2000)
    NETHERITE("ネザライト", Material.NETHERITE_INGOT, 10); // 0.5% (10/2000)
    
    private final String displayName;
    private final Material material;
    private final int weight;
    
    GachaItem(String displayName, Material material, int weight) {
        this.displayName = displayName;
        this.material = material;
        this.weight = weight;
    }
    
    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    // ゲッター
    public String getDisplayName() { return displayName; }
    public int getWeight() { return weight; }
}

// ガチャシステム
public class GachaSystem {
    private final WeightedSelector<GachaItem> selector = new WeightedSelector<>();
    
    public GachaSystem() {
        // 全てのガチャアイテムを登録
        for (GachaItem item : GachaItem.values()) {
            selector.add(item, item.getWeight());
        }
        
        // 確率をコンソールに表示
        selector.printProbabilities();
    }
    
    // ガチャを引く
    public GachaResult drawGacha(Player player) {
        if (!isPlayerValid(player)) {
            return new GachaResult(null, false, "プレイヤーが無効です");
        }
        
        GachaItem selectedItem = selector.selectRandom();
        if (selectedItem == null) {
            return new GachaResult(null, false, "ガチャエラー");
        }
        
        // アイテムを渡す
        ItemStack item = selectedItem.createItemStack();
        safeGiveItem(player, item);
        
        // レアリティに応じてエフェクト
        showGachaEffect(player, selectedItem);
        
        return new GachaResult(selectedItem, true, "ガチャ成功！");
    }
    
    private void showGachaEffect(Player player, GachaItem item) {
        Location loc = player.getLocation();
        
        switch (item) {
            case NETHERITE:
                // 最レア: 派手なエフェクト
                PluginUX.showSuccess(player, "★★★ 超激レア！ " + item.getDisplayName() + " ★★★");
                player.playSound(loc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                loc.getWorld().spawnParticle(Particle.TOTEM, loc.add(0, 1, 0), 50, 1, 1, 1, 0.1);
                break;
                
            case DIAMOND:
                // レア: 中程度のエフェクト
                PluginUX.showSuccess(player, "★★ レア！ " + item.getDisplayName() + " ★★");
                player.playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
                loc.getWorld().spawnParticle(Particle.ENCHANT, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.2);
                break;
                
            default:
                // 通常: シンプルなエフェクト
                PluginUX.showSuccess(player, item.getDisplayName() + " を獲得しました");
                break;
        }
    }
}

// ガチャ結果クラス
public class GachaResult {
    private final GachaItem item;
    private final boolean success;
    private final String message;
    
    public GachaResult(GachaItem item, boolean success, String message) {
        this.item = item;
        this.success = success;
        this.message = message;
    }
    
    // ゲッター
    public GachaItem getItem() { return item; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
```

### 4.3 ガチャアイテムとコマンド

```java
// ガチャチケットアイテム
public class GachaTicket extends CustomItem {
    private final GachaSystem gachaSystem;
    
    public GachaTicket(JavaPlugin plugin, GachaSystem gachaSystem) {
        super(plugin, "ガチャチケット", Material.PAPER);
        this.gachaSystem = gachaSystem;
    }
    
    @Override
    protected void onRightClick(Player player) {
        // ガチャを実行
        GachaResult result = gachaSystem.drawGacha(player);
        
        if (result.isSuccess()) {
            // チケットを1つ消費
            ItemStack ticket = player.getInventory().getItemInMainHand();
            if (ticket.getAmount() > 1) {
                ticket.setAmount(ticket.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(null);
            }
        } else {
            PluginUX.showError(player, result.getMessage());
        }
    }
}
```

---

## 🏗️ レベル5: プラグインの統合管理

### 5.1 プラグインマネージャーパターン

**なぜ必要？**: 機能が増えても整理された初期化を保つ

```java
// 👍 整理されたプラグイン管理
public class PluginManager {
    private final JavaPlugin plugin;
    private ItemManager itemManager;
    private GachaSystem gachaSystem;
    
    public PluginManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        plugin.getLogger().info("プラグイン初期化開始...");
        
        try {
            // 1. 設定ファイルの初期化
            initializeConfig();
            
            // 2. システムの初期化
            initializeSystems();
            
            // 3. アイテムの初期化
            initializeItems();
            
            // 4. コマンドの初期化
            initializeCommands();
            
            // 5. リスナーの初期化（最後）
            initializeListeners();
            
            plugin.getLogger().info("プラグイン初期化完了！");
            
        } catch (Exception e) {
            plugin.getLogger().severe("プラグイン初期化エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeConfig() {
        plugin.saveDefaultConfig();
        plugin.getLogger().info("設定ファイルを読み込みました");
    }
    
    private void initializeSystems() {
        gachaSystem = new GachaSystem();
        plugin.getLogger().info("ガチャシステムを初期化しました");
    }
    
    private void initializeItems() {
        itemManager = new ItemManager(plugin);
        
        // ガチャチケットを追加
        itemManager.register("gacha_ticket", new GachaTicket(plugin, gachaSystem));
        
        plugin.getLogger().info("アイテムシステムを初期化しました");
    }
    
    private void initializeCommands() {
        // Giveコマンド
        PluginCommand giveCommand = plugin.getCommand("give");
        if (giveCommand != null) {
            ImprovedGiveCommand executor = new ImprovedGiveCommand(itemManager);
            giveCommand.setExecutor(executor);
            giveCommand.setTabCompleter(executor);
        }
        
        // Gachaコマンド
        PluginCommand gachaCommand = plugin.getCommand("gacha");
        if (gachaCommand != null) {
            gachaCommand.setExecutor(new GachaCommand(gachaSystem));
        }
        
        plugin.getLogger().info("コマンドを登録しました");
    }
    
    private void initializeListeners() {
        // カスタムアイテムのリスナーは自動登録されているので、
        // ここでは追加のリスナーがあれば登録
        plugin.getLogger().info("リスナーを初期化しました");
    }
    
    // ゲッター
    public ItemManager getItemManager() { return itemManager; }
    public GachaSystem getGachaSystem() { return gachaSystem; }
}
```

### 5.2 メインプラグインクラスの整理

```java
// 👍 整理されたメインクラス
public class MyPlugin extends JavaPlugin {
    private PluginManager pluginManager;
    
    @Override
    public void onEnable() {
        // プラグインマネージャーに全て任せる
        pluginManager = new PluginManager(this);
        pluginManager.initialize();
    }
    
    @Override
    public void onDisable() {
        getLogger().info("プラグインを停止しました");
    }
    
    // 他のクラスからアクセスするためのゲッター
    public PluginManager getPluginManager() {
        return pluginManager;
    }
}
```

---

## 📋 実践演習: 小さなプラグインを作ってみよう

### 演習1: 基礎編「報酬システム」

```java
// ミッション: 毎日ログインした人に報酬を渡すプラグインを作ろう

// 1. プレイヤーデータを保存するクラス
public class PlayerData {
    private final UUID playerId;
    private long lastLoginDate;
    private int loginStreak;
    
    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.lastLoginDate = 0;
        this.loginStreak = 0;
    }
    
    // 今日ログインしたかチェック
    public boolean loginToday() {
        long today = System.currentTimeMillis() / (1000 * 60 * 60 * 24);
        long lastLogin = lastLoginDate / (1000 * 60 * 60 * 24);
        
        if (today == lastLogin) {
            return true; // 今日既にログインしている
        } else if (today == lastLogin + 1) {
            // 連続ログイン
            loginStreak++;
            lastLoginDate = System.currentTimeMillis();
            return false;
        } else {
            // ログインが途切れた
            loginStreak = 1;
            lastLoginDate = System.currentTimeMillis();
            return false;
        }
    }
    
    // ゲッター
    public int getLoginStreak() { return loginStreak; }
}

// 2. 報酬システム
public class DailyRewardSystem {
    private final WeightedSelector<Material> rewards = new WeightedSelector<>();
    private final Map<UUID, PlayerData> playerData = new HashMap<>();
    
    public DailyRewardSystem() {
        // 報酬アイテムを設定
        rewards.add(Material.BREAD, 100);      // 50%
        rewards.add(Material.IRON_INGOT, 60);  // 30%
        rewards.add(Material.GOLD_INGOT, 30);  // 15%
        rewards.add(Material.DIAMOND, 10);     // 5%
    }
    
    public void giveReward(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerData data = playerData.computeIfAbsent(playerId, PlayerData::new);
        
        if (data.loginToday()) {
            PluginUX.showInfo(player, "今日の報酬は既に受け取っています");
            return;
        }
        
        // 報酬を選択
        Material rewardMaterial = rewards.selectRandom();
        int amount = calculateAmount(data.getLoginStreak());
        
        ItemStack reward = new ItemStack(rewardMaterial, amount);
        safeGiveItem(player, reward);
        
        PluginUX.showSuccess(player, String.format("ログイン報酬！ %s x%d (連続%d日目)", 
            rewardMaterial.name(), amount, data.getLoginStreak()));
    }
    
    private int calculateAmount(int streak) {
        // 連続ログインボーナス
        return Math.min(1 + streak / 7, 5); // 最大5個まで
    }
}
```

### 演習2: 応用編「簡易ショップシステム」

```java
// ミッション: アイテムを売買できるショップを作ろう

// 商品情報
public class ShopItem {
    private final Material material;
    private final int buyPrice;
    private final int sellPrice;
    private final String displayName;
    
    public ShopItem(Material material, int buyPrice, int sellPrice, String displayName) {
        this.material = material;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.displayName = displayName;
    }
    
    // ゲッター
    public Material getMaterial() { return material; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public String getDisplayName() { return displayName; }
}

// ショップシステム
public class ShopSystem {
    private final SimpleRegistry<ShopItem> items = new SimpleRegistry<>();
    private final Map<UUID, Integer> playerMoney = new HashMap<>();
    
    public ShopSystem() {
        // 商品を登録
        registerItem("bread", new ShopItem(Material.BREAD, 10, 5, "パン"));
        registerItem("iron", new ShopItem(Material.IRON_INGOT, 50, 25, "鉄インゴット"));
        registerItem("diamond", new ShopItem(Material.DIAMOND, 500, 250, "ダイヤモンド"));
    }
    
    public void registerItem(String id, ShopItem item) {
        items.register(id, item);
    }
    
    public boolean buyItem(Player player, String itemId, int quantity) {
        ShopItem shopItem = items.get(itemId);
        if (shopItem == null) {
            PluginUX.showError(player, "商品が見つかりません: " + itemId);
            return false;
        }
        
        int totalPrice = shopItem.getBuyPrice() * quantity;
        int playerMoney = getMoney(player);
        
        if (playerMoney < totalPrice) {
            PluginUX.showError(player, String.format("お金が足りません。必要: %d, 所持: %d", 
                totalPrice, playerMoney));
            return false;
        }
        
        // お金を減らす
        setMoney(player, playerMoney - totalPrice);
        
        // アイテムを渡す
        ItemStack item = new ItemStack(shopItem.getMaterial(), quantity);
        safeGiveItem(player, item);
        
        PluginUX.showSuccess(player, String.format("%s x%d を購入しました (-%d円)", 
            shopItem.getDisplayName(), quantity, totalPrice));
        
        return true;
    }
    
    public int getMoney(Player player) {
        return playerMoney.getOrDefault(player.getUniqueId(), 1000); // 初期所持金1000円
    }
    
    public void setMoney(Player player, int amount) {
        playerMoney.put(player.getUniqueId(), amount);
    }
    
    public List<String> getItemIds() {
        return new ArrayList<>(items.getAllIds());
    }
}
```

---

## 🎯 まとめ: 段階的なスキルアップ

### 🥉 初級者 (まずはここから)
- [ ] `isPlayerValid()` を使ったプレイヤーチェック
- [ ] `safeGiveItem()` を使った安全なアイテム配布
- [ ] 統一されたメッセージ表示（PluginUX）

### 🥈 中級者 (慣れてきたら)
- [ ] CustomItemを使ったカスタムアイテム作成
- [ ] SimpleRegistryを使ったデータ管理
- [ ] 基本的なコマンド作成

### 🥇 上級者 (応用レベル)
- [ ] WeightedSelectorを使った確率システム
- [ ] PluginManagerを使った統合管理
- [ ] 複雑なシステムの組み合わせ

### 💎 エキスパート (自由に拡張)
- [ ] 独自の設計パターン実装
- [ ] パフォーマンス最適化
- [ ] 他のプラグインとの連携

---

## 📚 さらに学習したい場合

### 参考にできる設計パターン
1. **Bukkit Plugin Tutorial**: 公式ドキュメント
2. **Design Patterns**: ゲーム開発での設計パターン活用
3. **Clean Code**: コードの品質向上

### 実際のプロジェクトで練習
1. 小さなミニゲームプラグイン
2. ユーティリティプラグイン
3. RPG要素のあるプラグイン

**重要**: 一度に全てを覚えようとせず、必要に応じて少しずつスキルアップしていきましょう！

---

*このガイドが皆さんのMinecraftプラグイン開発の助けになれば幸いです。 Happy Coding! 🚀*