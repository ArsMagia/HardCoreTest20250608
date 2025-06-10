# CLAUDE.md

必ず日本語で回答してください。ビルドは俺がやる

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Minecraft Spigot plugin for hardcore multiplayer gameplay, implementing a comprehensive lucky/unlucky effect system with 70+ different effects triggered by LuckyBox items. The plugin uses Java 21 and targets Spigot API 1.21.4.

## Build and Development Commands

- **Build**: `mvn clean package` - Compiles and packages the plugin JAR with Maven Shade plugin
- **Quick Build**: `mvn package` - Packages without cleaning
- **Compile Only**: `mvn compile` - Compiles source code without packaging
- **Clean**: `mvn clean` - Removes build artifacts

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

4. **Scheduler Manager** (`core/SchedulerManager.java`): Manages long-running tasks and recurring operations

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
2. Implement apply() method with effect logic
3. Register in EffectRegistry.initializeEffects()
4. Use EffectUtils for validation and EffectConstants for parameters

**Custom Item Development**:
1. Extend AbstractCustomItemV2
2. Implement required event handlers
3. ItemRegistry automatically handles registration

**Code Conventions**:
- All user-facing text in Japanese
- Use EffectUtils.isPlayerValid() for player state checking
- Apply consistent particle/sound effects using established patterns
- Handle edge cases with graceful fallbacks