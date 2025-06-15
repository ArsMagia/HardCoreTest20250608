# CLAUDE.md

必ず日本語で回答してください。ビルドは俺がやる
--dangerously-skip-permissions

## 🔨 最重要ルール - 新しいルールの追加プロセス

ユーザーから今回限りではなく常に対応が必要だと思われる指示を受けた場合：

1. 「これを標準のルールにしますか？」と質問する
2. YESの回答を得た場合、CLAUDE.mdに追加ルールとして記載する
3. 以降は標準ルールとして常に適用する

このプロセスにより、プロジェクトのルールを継続的に改善していきます。

## 🛠️ 重要な設計パターン

**Future Guarantee System**: 
- FutureGuaranteeManagerクラスで将来の効果を保証
- プレイヤーごとの保証タイプ管理（STABLE_FUTURE, UNLUCKY_CALAMITY など）
- LuckyBoxItem使用時に最優先でチェック

**Cooldown Management**: 
- 複数回発動防止のため、各カスタムアイテムにクールダウン実装
- UUIDベースの時間管理で重複実行を防止

**Weighted Selection**: 
- 累積重み方式による確率選択
- レアリティによる自動重み付け（LEGENDARY:5 → COMMON:70）

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft Spigot plugin for hardcore multiplayer gameplay, implementing a comprehensive lucky/unlucky effect system with 70+ different effects triggered by LuckyBox items. The plugin uses Java 21 and targets Spigot API 1.21.4.

## Build and Development Commands

- **Build**: `mvn clean package` - Compiles and packages the plugin JAR with Maven Shade plugin
- **Quick Build**: `mvn package` - Packages without cleaning
- **Compile Only**: `mvn compile` - Compiles source code without packaging
- **Clean**: `mvn clean` - Removes build artifacts
- **Test**: `mvn test` - Runs unit tests (EffectPerformanceTest, WeightedEffectSelectorTest)
- **Integration Test**: `mvn verify` - Runs integration tests (PluginManagerIntegrationTest)
- **Performance Test**: `mvn test -Pperformance` - Runs performance tests only
- **Production Build**: `mvn package -Pproduction` - Builds without tests for production
- **Security Check**: `mvn dependency-check:check` - OWASP vulnerability scanning

Note: Quality check plugins (JaCoCo, Checkstyle, SpotBugs) are temporarily disabled due to Java 21 compatibility issues and MockBukkit dependency conflicts.

The shaded plugin JAR will be generated in the `target/` directory after building.

## Architecture Overview

### Core System Components

1. **Plugin Manager** (`core/PluginManager.java`): Central orchestrator that initializes all subsystems:
   - Item registry and custom item listener registration
   - Command registration (plo, lucky, custom)
   - Event listener management

2. **Item Registry** (`core/ItemRegistry.java`): Manages custom items and their event listeners
   - Automatic registration of AbstractCustomItemV2 implementations
   - Listener aggregation for plugin manager

3. **Effect System**: Multi-tier weighted probability architecture:
   - **Effect Registry**: Central repository storing 70+ effects by ID with separate lucky/unlucky selectors
   - **Weighted Effect Selector**: Implements cumulative weight-based random selection
   - **Rarity System**: COMMON(70) → UNCOMMON(50) → RARE(20) → EPIC(10) → LEGENDARY(5) weight distribution
   - **Auto Registration**: Effects are automatically discovered and registered using @EffectRegistration annotation

4. **Scheduler Manager** (`core/SchedulerManager.java`): Manages long-running tasks and recurring operations

5. **Automation System** (`automation/AutoImprovementAgent.java`): Self-improving system that analyzes performance and suggests optimizations

6. **Monitoring System** (`monitoring/AutoMonitoringSystem.java`): Real-time system monitoring and performance tracking

### Effect System Architecture

**LuckyEffect Interface** - Core contract for all effects:
```java
String apply(Player player);    // Effect execution
String getDescription();        // Display text
EffectType getType();          // LUCKY or UNLUCKY
EffectRarity getRarity();      // Weight tier
int getWeight();               // Selection probability
```

**Base Classes**:
- `LuckyEffectBase`: Positive effects (EffectType.LUCKY)
- `UnluckyEffectBase`: Negative effects (EffectType.UNLUCKY)

**Selection Probability** (based on rarity weights):
- COMMON: ~45% (70/155 total weight)
- UNCOMMON: ~32% (50/155)
- RARE: ~13% (20/155)
- EPIC: ~6% (10/155)
- LEGENDARY: ~3% (5/155)

**Effect Categories**:
- **Lucky Effects** (`effects/lucky/`): 35+ beneficial effects including teleportation, item rewards, buffs
- **Unlucky Effects** (`effects/unlucky/`): 35+ challenging effects including debuffs, environmental hazards, mob spawning
  - **New Creative Effects** (`effects/unlucky/common/`): Recently added innovative effects like fake UI messages, reflex delays, guitar soundtracks, shadow followers, etc.

### Custom Item System

Items extend `AbstractCustomItemV2` with automatic integration:
- Event listener registration through ItemRegistry
- NBT persistence for custom properties
- Material-based item identification
- Rarity-based display formatting

**Key Custom Items**:
- `LuckyBoxItem`: Primary trigger for effect system (50/50 lucky/unlucky chance)
- `GrappleItem`: Grappling hook with physics-based movement
- `EnhancedPickaxeItem`: Mining tool with special properties
- `SpecialMultiToolItem`: Multi-purpose utility item
- `HealKitItem`: Healing item with area effects
- `PhantomBladeItem`: Special weapon with unique properties
- `LoneSwordItem`: Unique sword with special abilities
- `PlayerTrackingCompassItem`: Compass that tracks nearby players
- `ShuffleItem`: Item with random effect properties

### Commands and Features

- `/lucky [amount|lucky|unlucky|list]` - Lucky box management and effect testing
- `/custom` - Custom item catalog display
- `/plo` - Administrative functions

## Key Integration Patterns

**Effect-Item Integration**: LuckyBoxItem → EffectRegistry → WeightedEffectSelector → Effect.apply()

**Scheduler Integration**: Long-running effects use BukkitRunnable for state management and cleanup

**Utility Systems**:
- `EffectConstants`: Centralized configuration values (durations, ranges, limits)
- `EffectUtils`: Common operations (validation, health management, broadcasting)

## Development Guidelines

**Adding New Effects**:
1. Extend LuckyEffectBase or UnluckyEffectBase
2. Add @EffectRegistration annotation with id, type, and rarity
3. Implement apply() method with effect logic
4. Effects are automatically registered via EffectAutoRegistry
5. Use EffectUtils for validation and EffectConstants for parameters

**Custom Item Development**:
1. Extend AbstractCustomItemV2
2. Implement required event handlers
3. ItemRegistry automatically handles registration

**Code Conventions**:
- All user-facing text in Japanese
- Use EffectUtils.isPlayerValid() for player state checking
- Apply consistent particle/sound effects using established patterns
- Handle edge cases with graceful fallbacks

## Annotation-Based Effect Registration

**@EffectRegistration Annotation**:
```java
@EffectRegistration(
    id = "unique_effect_id",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.COMMON,
    enabled = true,
    description = "Effect description",
    category = "gameplay"
)
public class MyCustomEffect extends UnluckyEffectBase {
    // Effect implementation
}
```

**Auto-Discovery Process**:
- Effects are automatically scanned at startup using EffectAutoRegistry
- Package scanning works in both development and JAR environments using reflection
- Supports both file system (development) and JAR file (production) scanning
- Failed registrations are logged with detailed error information
- Registration statistics are displayed on startup
- Effects require JavaPlugin constructor parameter for instantiation
- Disabled effects (enabled=false in annotation) are automatically skipped

## Quality Assurance

**Testing Framework**:
- JUnit 5 for unit testing
- Mockito for mocking dependencies (with lenient mode for MockBukkit compatibility)
- AssertJ for fluent assertions
- Performance testing with EffectPerformanceTest

**Build Configuration**:
- Maven Shade plugin for dependency shading and JAR packaging
- Caffeine cache and Apache Commons Lang3 are relocated to prevent conflicts
- OWASP dependency check for security vulnerability scanning

**Logging Configuration**:
- Logback with logback-spring.xml configuration
- Micrometer metrics integration for performance monitoring
- Structured logging with SLF4J throughout the codebase
- Multiple log appenders: Console, File, JSON, Error-specific, Performance, Security
- Log rotation with size and time-based policies
- Specialized loggers for monitoring, performance, and security events