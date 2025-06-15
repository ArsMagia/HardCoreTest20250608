# Minecraftプラグイン セキュリティ分析レポート

**分析対象**: HardCoreTest20250608 Plugin  
**分析日時**: 2025年6月12日  
**分析者**: セキュリティ専門家 (Claude Code)  
**重要度**: HIGH（Productionレベルのセキュリティ脆弱性を検出）  

---

## 📋 エグゼクティブサマリー

本Minecraftプラグインの包括的セキュリティ監査により、**重大な権限管理の脆弱性と複数のセキュリティリスク**を特定しました。特に、`--dangerously-skip-permissions`設定下での権限チェックの不備、エフェクト実行時のセキュリティ検証不足、およびインジェクション攻撃に対する脆弱性が確認されています。

### 🚨 主要リスク
- **権限エスカレーション可能性**: 高
- **データ整合性リスク**: 中
- **コマンドインジェクション脆弱性**: 中
- **プライバシー保護**: 低

---

## 🔍 詳細分析結果

### 1. 権限管理とアクセス制御

#### 🔴 CRITICAL: 不十分な権限チェック体系

**発見場所**: 全コマンドクラス

```java
// CustomItemsCMD.java:45 - 単純なOPチェックのみ
if (!player.isOp()) {
    player.sendMessage(EffectConstants.PERMISSION_DENIED_MESSAGE);
    return true;
}

// LuckyCMD.java:60 - list以外の機能でOP権限要求
if (!player.isOp()) {
    player.sendMessage(EffectConstants.PERMISSION_DENIED_MESSAGE);
    return true;
}
```

**脆弱性**:
1. **バイナリ権限体系**: OP/非OPの2段階のみで、細粒度権限制御なし
2. **権限グループ不在**: 管理者、モデレーター、プレイヤーの役割分離なし
3. **動的権限チェック欠如**: 効果発動時の再検証なし
4. **--dangerously-skip-permissions影響**: 権限バイパスの可能性

#### 🟡 MEDIUM: PloCMDの権限制御不備

```java
// PloCMD.java:16-48 - 権限チェック完全欠如
public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;
    // 権限チェックなし！
    if (args.length >= 4 && "tp".equalsIgnoreCase(args[0])) {
        return handleTeleportCommand(player, args);
    }
}
```

**リスク**: 任意座標へのテレポート機能が全プレイヤーに開放

### 2. コマンドインジェクションとバリデーション

#### 🟠 MEDIUM: 入力検証の不備

**PloCMDテレポート機能**:
```java
// PloCMD.java:57-69 - 数値変換のみ、範囲チェックなし
double x = Double.parseDouble(args[1]);
double y = Double.parseDouble(args[2]);
double z = Double.parseDouble(args[3]);
// 座標範囲、ワールド境界チェック不在
```

**LuckyCMD数量指定**:
```java
// LuckyCMD.java:80-86 - 基本的範囲チェックのみ
int amount = Integer.parseInt(args[0]);
if (amount <= 0 || amount > 64) {
    // エラーログ取得不足、異常値追跡なし
}
```

**脆弱性**:
1. **境界値チェック不足**: 極値での予期しない動作
2. **ログ記録不備**: 不正アクセス試行の追跡不可
3. **レート制限なし**: コマンドスパムによるDoS攻撃可能性

### 3. エフェクトシステムのセキュリティリスク

#### 🔴 HIGH: 危険なエフェクトの無制限実行

**ItemTransferEffect**: 
```java
// ItemTransferEffect.java:42-87 - アイテム強制転送
private void transferItems(Player sender, List<Player> potentialReceivers) {
    // 受信者の同意なし、転送拒否機能なし
    // 価値あるアイテムの意図しない喪失リスク
}
```

**BombThrowEffect**:
```java
// BombThrowEffect.java:141-218 - 爆発ダメージ制御
TNTPrimed mainTnt = target.getWorld().spawn(bombSpawnLoc, TNTPrimed.class);
mainTnt.setYield(2.0f); // 建物破壊可能
// ブロック破壊防止機能が不完全
```

**HealthReductionEffect**:
```java
// HealthReductionEffect.java:20-59 - 最大体力操作
if (currentMaxHealth > 4) {
    healthAttr.setBaseValue(currentMaxHealth - 2);
    // 体力回復機能なし、永続的なデバフ
}
```

#### 🟠 MEDIUM: エフェクト実行時の検証不足

```java
// LuckyTestGUI.java:247-268 - 管理者専用機能だが検証不十分
private void executeEffect(Player player, String effectDisplayName, boolean isLucky) {
    // プレイヤー状態検証不足
    // 連続実行防止機能なし
    String result = effect.apply(player);
}
```

### 4. データ整合性とプライバシー

#### 🟡 MEDIUM: プレイヤーデータの保護不備

**追跡機能**:
```java
// PlayerTrackingCompassItem - プレイヤー位置情報の無制限取得
// プライバシー同意機能なし
// 位置情報ログ保持期間未定義
```

**インベントリ操作**:
```java
// ItemTransferEffect, ItemScatterEffect等
// プレイヤーアイテムの無通知操作
// バックアップ・復旧機能不在
```

### 5. セッション管理とクールダウン

#### 🟡 LOW: クールダウン機能の不十分性

```java
// EffectUtils.java:176-190 - 基本的なクールダウンのみ
public static boolean checkCooldown(Player player, Long lastActivationTime, long cooldownMs, String itemName) {
    // セッション間での永続化なし
    // プレイヤーリログでリセット
    // 複数アイテム間の統合制限なし
}
```

---

## 🛡️ セキュリティ修正提案

### Phase 1: 緊急修正（高優先度）

#### 1.1 権限システムの強化

```java
// 新しい権限管理クラス
public class PermissionManager {
    public enum Permission {
        ADMIN_FULL("hardcoretest.admin.full"),
        ADMIN_ITEMS("hardcoretest.admin.items"),
        ADMIN_EFFECTS("hardcoretest.admin.effects"),
        MODERATOR_TELEPORT("hardcoretest.mod.teleport"),
        PLAYER_LIST("hardcoretest.player.list");
        
        private final String node;
        
        public boolean hasPermission(Player player) {
            return player.hasPermission(this.node) || player.isOp();
        }
    }
    
    public static boolean checkPermission(Player player, Permission permission) {
        boolean hasPermission = permission.hasPermission(player);
        if (!hasPermission) {
            logUnauthorizedAccess(player, permission);
        }
        return hasPermission;
    }
    
    private static void logUnauthorizedAccess(Player player, Permission permission) {
        // セキュリティログ記録
        Bukkit.getLogger().warning(String.format(
            "Unauthorized access attempt: Player=%s, Permission=%s, IP=%s",
            player.getName(), permission.name(), player.getAddress()
        ));
    }
}
```

#### 1.2 入力検証の強化

```java
// セキュアな入力検証クラス
public class SecureInputValidator {
    private static final double MAX_COORDINATE = 30000000;
    private static final int MAX_AMOUNT = 64;
    private static final Pattern VALID_WORLD_NAME = Pattern.compile("^[a-zA-Z0-9_-]{1,16}$");
    
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        private final Object value;
        
        // Constructor and getters
    }
    
    public static ValidationResult validateCoordinate(String input) {
        try {
            double value = Double.parseDouble(input);
            if (Math.abs(value) > MAX_COORDINATE) {
                return new ValidationResult(false, "座標値が範囲外です", null);
            }
            return new ValidationResult(true, null, value);
        } catch (NumberFormatException e) {
            return new ValidationResult(false, "無効な数値形式です", null);
        }
    }
    
    public static ValidationResult validateWorldName(String worldName) {
        if (!VALID_WORLD_NAME.matcher(worldName).matches()) {
            return new ValidationResult(false, "無効なワールド名です", null);
        }
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return new ValidationResult(false, "ワールドが存在しません", null);
        }
        return new ValidationResult(true, null, world);
    }
}
```

#### 1.3 危険エフェクトの安全化

```java
// エフェクト実行前の安全チェック
public class EffectSafetyManager {
    private static final Set<String> DESTRUCTIVE_EFFECTS = Set.of(
        "爆弾投擲", "このアイテムをお前に託す", "最大体力減少"
    );
    
    public static boolean canExecuteEffect(Player player, LuckyEffect effect) {
        // 1. プレイヤー状態チェック
        if (!EffectUtils.isPlayerValid(player)) {
            return false;
        }
        
        // 2. 破壊的エフェクトの特別確認
        if (DESTRUCTIVE_EFFECTS.contains(effect.getDescription())) {
            return hasDestructiveEffectPermission(player);
        }
        
        // 3. クールダウンチェック
        return !isOnGlobalCooldown(player);
    }
    
    private static boolean hasDestructiveEffectPermission(Player player) {
        // 管理者または明示的な同意が必要
        return player.hasPermission("hardcoretest.effects.destructive") || 
               hasPlayerConsent(player);
    }
    
    private static boolean hasPlayerConsent(Player player) {
        // プレイヤーの事前同意確認システム
        // 実装: ファイルベースまたはデータベース
        return ConsentManager.hasConsent(player.getUniqueId(), "destructive_effects");
    }
}
```

### Phase 2: 中期改善（中優先度）

#### 2.1 監査ログシステム

```java
public class SecurityAuditLogger {
    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger("SECURITY");
    
    public static void logCommand(Player player, String command, String[] args) {
        SECURITY_LOGGER.info("COMMAND_EXECUTION: Player={}, Command={}, Args={}, IP={}, World={}",
            player.getName(), command, Arrays.toString(args), 
            player.getAddress(), player.getWorld().getName());
    }
    
    public static void logEffectExecution(Player player, LuckyEffect effect, String result) {
        SECURITY_LOGGER.info("EFFECT_EXECUTION: Player={}, Effect={}, Result={}, Health={}, Location={}",
            player.getName(), effect.getDescription(), result,
            player.getHealth(), player.getLocation());
    }
    
    public static void logSuspiciousActivity(Player player, String activity, String details) {
        SECURITY_LOGGER.warn("SUSPICIOUS_ACTIVITY: Player={}, Activity={}, Details={}, IP={}",
            player.getName(), activity, details, player.getAddress());
    }
}
```

#### 2.2 レート制限システム

```java
public class RateLimitManager {
    private static final Map<UUID, CommandRateTracker> playerTrackers = new HashMap<>();
    
    public static class CommandRateTracker {
        private final Queue<Long> commandTimes = new LinkedList<>();
        private static final int MAX_COMMANDS_PER_MINUTE = 20;
        private static final long MINUTE_MS = 60000;
        
        public boolean canExecuteCommand() {
            long now = System.currentTimeMillis();
            
            // 古いエントリを削除
            while (!commandTimes.isEmpty() && 
                   (now - commandTimes.peek()) > MINUTE_MS) {
                commandTimes.poll();
            }
            
            if (commandTimes.size() >= MAX_COMMANDS_PER_MINUTE) {
                return false;
            }
            
            commandTimes.offer(now);
            return true;
        }
    }
    
    public static boolean checkRateLimit(Player player) {
        return playerTrackers.computeIfAbsent(player.getUniqueId(), 
            k -> new CommandRateTracker()).canExecuteCommand();
    }
}
```

### Phase 3: 長期改善（低優先度）

#### 3.1 プライバシー保護システム

```java
public class PrivacyManager {
    public static boolean canTrackPlayer(Player tracker, Player target) {
        // 相互同意システム
        if (hasTrackingConsent(target, tracker)) {
            return true;
        }
        
        // 管理者は常に追跡可能
        return tracker.hasPermission("hardcoretest.admin.track");
    }
    
    public static void requestTrackingPermission(Player requester, Player target) {
        target.sendMessage(String.format(
            "%s があなたを追跡する権限を要求しています。",
            requester.getName()
        ));
        // インタラクティブな同意システム実装
    }
}
```

#### 3.2 設定可能なセキュリティ設定

```yaml
# config.yml セキュリティセクション
security:
  enable_permission_system: true
  enable_audit_logging: true
  enable_rate_limiting: true
  destructive_effects:
    require_consent: true
    admin_override: true
  tracking:
    require_mutual_consent: true
    admin_exempt: false
  cooldowns:
    global_effect_cooldown: 5000  # 5秒
    command_cooldown: 1000        # 1秒
```

---

## 🎯 実装優先順位

### 即座実装（24時間以内）
1. **PloCMDの権限チェック追加**
2. **座標入力の境界値検証**
3. **基本的なセキュリティログ実装**

### 短期実装（1週間以内）
1. **権限システムの細分化**
2. **危険エフェクトの同意システム**
3. **レート制限機能**

### 中期実装（1ヶ月以内）
1. **包括的監査ログシステム**
2. **プライバシー保護機能**
3. **設定可能なセキュリティポリシー**

---

## 📊 リスク評価マトリックス

| 脆弱性 | 重要度 | 影響度 | 発生確率 | 検出容易性 | 総合リスク |
|--------|--------|--------|----------|------------|------------|
| PloCMD権限不備 | HIGH | HIGH | HIGH | HIGH | **CRITICAL** |
| 破壊的エフェクト | HIGH | MEDIUM | MEDIUM | LOW | **HIGH** |
| 入力検証不足 | MEDIUM | MEDIUM | HIGH | MEDIUM | **MEDIUM** |
| プライバシー問題 | LOW | MEDIUM | LOW | LOW | **LOW** |

---

## 🔗 関連セキュリティ基準

- **Minecraft Server Security Best Practices**
- **OWASP Application Security Guidelines**
- **Java Secure Coding Standards**
- **Plugin Development Security Framework**

---

**分析完了**: このレポートは、プロダクションデプロイ前の必須セキュリティ修正項目を特定しています。特に権限管理の脆弱性は緊急対応が必要です。