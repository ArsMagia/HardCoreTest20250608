package magia.box.example.hardCoreTest20250608.items.custom;

import magia.box.example.hardCoreTest20250608.items.core.AbstractCustomItemV2;
import magia.box.example.hardCoreTest20250608.items.core.ItemRarity;
import magia.box.example.hardCoreTest20250608.effects.*;
import magia.box.example.hardCoreTest20250608.effects.lucky.*;
import magia.box.example.hardCoreTest20250608.effects.lucky.legendary.AdrenalineRushEffect;
import magia.box.example.hardCoreTest20250608.effects.lucky.common.SpeedLuckyEffect;
import magia.box.example.hardCoreTest20250608.effects.unlucky.individual.*;
import magia.box.example.hardCoreTest20250608.effects.FutureGuaranteeManager;
import magia.box.example.hardCoreTest20250608.effects.unlucky.*;
import static magia.box.example.hardCoreTest20250608.effects.unlucky.SimpleUnluckyEffects.*;
import static magia.box.example.hardCoreTest20250608.effects.unlucky.MoreUnluckyEffects.*;
import static magia.box.example.hardCoreTest20250608.effects.unlucky.FinalUnluckyEffects.*;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class LuckyBoxItem extends AbstractCustomItemV2 {

    private static final String USE_MESSAGE = ChatColor.YELLOW + "ラッキーボックスを開きました！";
    private static final String LUCKY_MESSAGE = ChatColor.GREEN + "ラッキー！";
    private static final String UNLUCKY_MESSAGE = ChatColor.RED + "アンラッキー...";

    private final Random random = new Random();
    private final EffectRegistry effectRegistry;
    private static final Map<UUID, Long> lastActivation = new HashMap<>();

    public LuckyBoxItem(JavaPlugin plugin) {
        super(plugin, builder("lucky_box", "ラッキーボックス")
                .material(Material.NETHER_STAR)
                .rarity(ItemRarity.EPIC)
                .addLore("右クリックで運試し！")
                .addLore("50%の確率でラッキー判定")
                .addHint("運が良ければ特別な効果が..."));
        
        this.effectRegistry = new EffectRegistry(plugin);
        // FutureGuaranteeManagerを初期化
        FutureGuaranteeManager.initialize(plugin);
        initializeEffects();
    }

    public LuckyBoxItem(JavaPlugin plugin, ItemBuilder builder) {
        super(plugin, builder);
        this.effectRegistry = new EffectRegistry(plugin);
        // FutureGuaranteeManagerを初期化
        FutureGuaranteeManager.initialize(plugin);
        initializeEffects();
    }

    private void initializeEffects() {
        // 時空間系ラッキー効果
        effectRegistry.registerEffect("safe_teleport", new SafeTeleportEffect(plugin));
        effectRegistry.registerEffect("time_rewind", new TimeRewindEffect(plugin));
        effectRegistry.registerEffect("future_vision", new FutureVisionEffect(plugin));
        
        // 環境操作系ラッキー効果
        effectRegistry.registerEffect("cloud_bridge", new CloudBridgeEffect(plugin));
        effectRegistry.registerEffect("gravity_well", new GravityWellEffect(plugin));
        effectRegistry.registerEffect("plant_growth_wave", new PlantGrowthWaveEffect(plugin));
        
        // 変身・能力系ラッキー効果
        effectRegistry.registerEffect("invisibility_master", new InvisibilityMasterEffect(plugin));
        effectRegistry.registerEffect("giant_form", new GiantFormEffect(plugin));
        effectRegistry.registerEffect("animal_friend", new AnimalFriendEffect(plugin));
        
        // アイテム・資源系ラッキー効果
        effectRegistry.registerEffect("treasure_rain", new TreasureRainEffect(plugin));
        effectRegistry.registerEffect("item_duplication", new ItemDuplicationEffect(plugin));
        
        // カスタムアイテム報酬系ラッキー効果
        effectRegistry.registerEffect("grapple_reward", new GrappleRewardEffect(plugin));
        effectRegistry.registerEffect("lone_sword_reward", new LoneSwordRewardEffect(plugin));
        effectRegistry.registerEffect("phantom_blade_reward", new PhantomBladeRewardEffect(plugin));
        effectRegistry.registerEffect("shuffle_item_reward", new ShuffleItemRewardEffect(plugin));
        effectRegistry.registerEffect("enhanced_pickaxe_reward", new EnhancedPickaxeRewardEffect(plugin));
        effectRegistry.registerEffect("player_tracking_compass_reward", new PlayerTrackingCompassRewardEffect(plugin));
        effectRegistry.registerEffect("special_multi_tool_reward", new SpecialMultiToolRewardEffect(plugin));
        effectRegistry.registerEffect("heal_kit_reward", new HealKitRewardEffect(plugin));
        effectRegistry.registerEffect("luckybox_distribution", new LuckyBoxDistributionEffect(plugin));
        
        // 追加ラッキー効果
        effectRegistry.registerEffect("experience_magnet", new ExperienceMagnetEffect(plugin));
        effectRegistry.registerEffect("instant_mining", new InstantMiningEffect(plugin));
        effectRegistry.registerEffect("auto_build", new AutoBuildEffect(plugin));
        effectRegistry.registerEffect("multi_drop", new MultiDropEffect(plugin));
        effectRegistry.registerEffect("hotbar_tool_enchant_rare", new HotbarToolEnchantRareEffect(plugin));
        effectRegistry.registerEffect("hotbar_tool_enchant_epic", new HotbarToolEnchantEpicEffect(plugin));
        
        // ポーション系ラッキー効果
        effectRegistry.registerEffect("random_potion_lucky", new RandomPotionLuckyEffect(plugin));
        effectRegistry.registerEffect("random_buff_potion", new RandomBuffPotionEffect(plugin));
        effectRegistry.registerEffect("multi_buff_combination", new MultiBuffCombinationEffect(plugin));
        effectRegistry.registerEffect("debuff_cleanse", new DebuffCleanseEffect(plugin));
        
        // 体力増減系ラッキー効果
        effectRegistry.registerEffect("health_boost", new HealthBoostEffect(plugin));
        effectRegistry.registerEffect("life_breath", new LifeBreathEffect(plugin));
        
        // 特殊移動系ラッキー効果
        effectRegistry.registerEffect("malphite_ult", new MalphiteUltEffect(plugin));
        effectRegistry.registerEffect("egg_taxi", new EggTaxiEffect(plugin));
        
        // アイテム獲得系ラッキー効果
        effectRegistry.registerEffect("item_theft", new ItemTheftEffect(plugin));
        
        // 基本ラッキー効果
        effectRegistry.registerEffect("speed", new SpeedLuckyEffect(plugin));
        effectRegistry.registerEffect("temporary_flight", new TemporaryFlightEffect(plugin));
        effectRegistry.registerEffect("dimension_pocket", new DimensionPocketEffect(plugin));
        effectRegistry.registerEffect("weak_recovery", new WeakRecoveryEffect(plugin));
        
        // 新しいLEGENDARY効果
        effectRegistry.registerEffect("stable_future", new StableFutureEffect(plugin));
        effectRegistry.registerEffect("rush_addiction", new RushAddictionEffect(plugin));
        effectRegistry.registerEffect("time_leap", new TimeLeapEffect(plugin));
        effectRegistry.registerEffect("adrenaline_rush", new AdrenalineRushEffect(plugin));
        effectRegistry.registerEffect("future_selection", new FutureSelectionEffect(plugin));
        effectRegistry.registerEffect("building_god", new BuildingGodEffect(plugin));
        
        // 時空間系アンラッキー効果
        effectRegistry.registerEffect("spacetime_distortion", new SpaceTimeDistortionEffect(plugin));
        
        // 変身・妨害系アンラッキー効果
        effectRegistry.registerEffect("chicken_transform", new ChickenTransformEffect(plugin));
        
        // アイテム・資源系アンラッキー効果
        effectRegistry.registerEffect("item_scatter", new ItemScatterEffect(plugin));
        effectRegistry.registerEffect("item_scatter_chaos", new ItemScatterChaosEffect(plugin));
        
        // ポーション系アンラッキー効果
        effectRegistry.registerEffect("random_potion_unlucky", new RandomPotionUnluckyEffect(plugin));
        
        // 体力増減系アンラッキー効果
        effectRegistry.registerEffect("health_reduction", new HealthReductionEffect(plugin));
        
        // 危険系アンラッキー効果
        effectRegistry.registerEffect("potion_rain", new PotionRainEffect(plugin));
        effectRegistry.registerEffect("mob_speed_boost", new MobSpeedBoostEffect(plugin));
        effectRegistry.registerEffect("flame_field", new FlameFieldEffect(plugin));
        effectRegistry.registerEffect("spider_web_powder_snow_trap", new SpiderWebPowderSnowTrapEffect(plugin));
        effectRegistry.registerEffect("bomb_throw", new BombThrowEffect(plugin));
        
        // 基本アンラッキー効果
        effectRegistry.registerEffect("gravity_flip", new GravityFlipEffect(plugin));
        effectRegistry.registerEffect("arrow_rain", new ArrowRainEffect(plugin));
        
        // 追加アンラッキー効果 (SimpleUnluckyEffects)
        effectRegistry.registerEffect("ground_liquefaction", new GroundLiquefactionEffect(plugin));
        effectRegistry.registerEffect("food_decay", new FoodDecayEffect(plugin));
        effectRegistry.registerEffect("tool_breakage", new ToolBreakageEffect(plugin));
        effectRegistry.registerEffect("color_blindness", new ColorBlindnessEffect(plugin));
        effectRegistry.registerEffect("curse_infection", new CurseInfectionEffect(plugin));
        effectRegistry.registerEffect("time_acceleration", new TimeAccelerationEffect(plugin));
        effectRegistry.registerEffect("dimension_rift", new DimensionRiftEffect(plugin));
        effectRegistry.registerEffect("falling_blocks", new FallingBlocksEffect(plugin));
        
        // 新しいunlucky効果
        effectRegistry.registerEffect("curse_proxy", new CurseProxyEffect(plugin));
        effectRegistry.registerEffect("annoying_youtuber", new AnnoyingYoutuberEffect(plugin));
        effectRegistry.registerEffect("worst_building", new WorstBuildingEffect(plugin));
        effectRegistry.registerEffect("item_transfer", new ItemTransferEffect(plugin));
        effectRegistry.registerEffect("follow_me", new FollowMeEffect(plugin));
        effectRegistry.registerEffect("brought_calamity", new BroughtCalamityEffect(plugin));
        effectRegistry.registerEffect("abyss_interference", new AbyssInterferenceEffect(plugin));
        effectRegistry.registerEffect("all_for_one", new AllForOneEffect(plugin));
        
        // Individual unlucky effects (migrated from compound files)
        effectRegistry.registerEffect("random_teleport", new magia.box.example.hardCoreTest20250608.effects.unlucky.individual.RandomTeleportEffect(plugin));
        effectRegistry.registerEffect("item_weight", new magia.box.example.hardCoreTest20250608.effects.unlucky.individual.ItemWeightEffect(plugin));
        
        // SimpleUnluckyEffects のネストクラス (remaining)
        effectRegistry.registerEffect("memory_loss", new MemoryLossEffect(plugin));
        effectRegistry.registerEffect("fire_rain", new FireRainEffect(plugin));
        effectRegistry.registerEffect("exp_leak", new ExpLeakEffect(plugin));
        effectRegistry.registerEffect("weapon_curse", new WeaponCurseEffect(plugin));
        effectRegistry.registerEffect("armor_vanish", new ArmorVanishEffect(plugin));
        
        // Individual unlucky effects (migrated from MoreUnluckyEffects)
        effectRegistry.registerEffect("hand_tremor", new magia.box.example.hardCoreTest20250608.effects.unlucky.individual.HandTremorEffect(plugin));
        
        // MoreUnluckyEffects のネストクラス (remaining)
        effectRegistry.registerEffect("darkphobia", new DarkphobiaEffect(plugin));
        effectRegistry.registerEffect("confusion_mist", new ConfusionMistEffect(plugin));
        effectRegistry.registerEffect("electric_shock", new ElectricShockEffect(plugin));
        effectRegistry.registerEffect("frozen_state", new FrozenStateEffect(plugin));
        effectRegistry.registerEffect("backward_walking", new BackwardWalkingEffect(plugin));
        effectRegistry.registerEffect("tool_addiction", new ToolAddictionEffect(plugin));
        effectRegistry.registerEffect("metal_allergy", new MetalAllergyEffect(plugin));
        effectRegistry.registerEffect("gravity_sickness", new GravitySicknessEffect(plugin));
        effectRegistry.registerEffect("hyperacusis", new HyperacusisEffect(plugin));
        
        // FinalUnluckyEffects のネストクラス
        effectRegistry.registerEffect("mirror_world", new MirrorWorldEffect(plugin));
        effectRegistry.registerEffect("petrification", new PetrificationEffect(plugin));
        effectRegistry.registerEffect("shadow_binding", new ShadowBindingEffect(plugin));
        effectRegistry.registerEffect("magnetic_anomaly", new MagneticAnomalyEffect(plugin));
        effectRegistry.registerEffect("item_shuffle", new ItemShuffleEffect(plugin));
        
        // QuickDebuff バリエーション
        effectRegistry.registerEffect("power_drain", createWeaknessVariant(plugin));
        effectRegistry.registerEffect("intense_hunger", createHungerVariant(plugin));
        effectRegistry.registerEffect("dizziness", createNauseaVariant(plugin));
        effectRegistry.registerEffect("light_levitation", createLevitationVariant(plugin));
        
    }
    
    public EffectRegistry getEffectRegistry() {
        return effectRegistry;
    }
    
    /**
     * ラッキー/アンラッキー判定を行う
     * @return ラッキーの場合true
     */
    private boolean determineIsLucky() {
        return random.nextDouble() < (EffectConstants.LUCKY_CHANCE_PERCENT / 100.0);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_AIR && 
            event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // カスタムアイテムチェックを先に実行
        if (!isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);
        
        // クールダウンチェック（複数回発動防止）
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Long lastUse = lastActivation.get(playerId);
        
        if (lastUse != null && (currentTime - lastUse) < EffectConstants.LUCKY_BOX_COOLDOWN_MS) {
            // クールダウン中は無言でリターン（メッセージ重複防止）
            return;
        }
        
        // クールダウン設定（複数回発動防止のため早期設定）
        lastActivation.put(playerId, currentTime);

        // アイテムを1つ消費
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        player.sendMessage(USE_MESSAGE);

        // 将来保証システムを最優先でチェック
        FutureGuaranteeManager manager = FutureGuaranteeManager.getInstance();
        if (manager == null) {
            // マネージャーが初期化されていない場合は再初期化
            FutureGuaranteeManager.initialize(plugin);
            manager = FutureGuaranteeManager.getInstance();
        }
        
        if (manager != null && manager.hasGuarantee(player)) {
            handleGuaranteedEffect(player, manager);
            return;
        }

        // 通常のラッキー/アンラッキー判定
        boolean isLucky = determineIsLucky();

        if (isLucky) {
            handleLuckyEffect(player);
        } else {
            handleUnluckyEffect(player);
        }
    }

    private void handleGuaranteedEffect(Player player, FutureGuaranteeManager manager) {
        FutureGuaranteeManager.GuaranteeType guaranteeType = manager.getGuaranteeType(player);
        manager.consumeGuarantee(player);
        
        // アンラッキー災厄保証の場合は特別処理
        if (guaranteeType == FutureGuaranteeManager.GuaranteeType.UNLUCKY_CALAMITY) {
            player.sendMessage(ChatColor.DARK_RED + "💀 災厄の保証が発動！ " + UNLUCKY_MESSAGE);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0f, 0.8f);
            player.getWorld().spawnParticle(
                    Particle.SMOKE,
                    player.getLocation().add(0, 1, 0),
                    20, 0.5, 0.5, 0.5, 0.1
            );
            
            // 強制的にアンラッキー効果を実行
            LuckyEffect unluckyEffect = effectRegistry.getRandomUnlucky();
            if (unluckyEffect != null) {
                String effectDescription = unluckyEffect.apply(player);
                EffectUtils.broadcastEffectMessage(player, effectDescription, unluckyEffect.getRarity(), false);
            }
            return;
        }
        
        // 通常のラッキー保証
        player.sendMessage(ChatColor.GOLD + "✨ 将来保証が発動！ " + LUCKY_MESSAGE);
        
        // 保証された効果を実行
        LuckyEffect guaranteedEffect = getGuaranteedEffect(guaranteeType);
        if (guaranteedEffect != null) {
            player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
            player.getWorld().spawnParticle(
                    Particle.TOTEM_OF_UNDYING,
                    player.getLocation().add(0, 1, 0),
                    30, 1, 1, 1, 0.1
            );
            
            String effectDescription = guaranteedEffect.apply(player);
            EffectUtils.broadcastEffectMessage(player, effectDescription, guaranteedEffect.getRarity(), true);
        } else {
            // フォールバック：保証された効果が見つからない場合は通常のラッキー効果
            player.sendMessage(ChatColor.YELLOW + "⚠ 保証された効果が見つかりません。通常のラッキー効果を実行します。");
            handleLuckyEffect(player);
        }
    }

    private void handleLuckyEffect(Player player) {
        player.sendMessage(LUCKY_MESSAGE);

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
        player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                20, 0.5, 0.5, 0.5, 0.1
        );

        LuckyEffect effect = effectRegistry.getRandomLucky();
        if (effect != null) {
            String effectDescription = effect.apply(player);
            EffectUtils.broadcastEffectMessage(player, effectDescription, effect.getRarity(), true);
        }
    }
    
    private LuckyEffect getGuaranteedEffect(FutureGuaranteeManager.GuaranteeType guaranteeType) {
        switch (guaranteeType) {
            case STABLE_FUTURE:
                // 安定した将来の場合はランダムな良い効果（EPIC以上優先）
                return effectRegistry.getRandomLucky();
            case RUSH_ADDICTION:
                return effectRegistry.getEffect("malphite_ult");
            case TIME_LEAP:
                return effectRegistry.getEffect("time_rewind");
            case ADRENALINE_RUSH:
                return effectRegistry.getEffect("multi_buff_combination");
            case FUTURE_VISION:
                return effectRegistry.getEffect("future_vision");
            case MALPHITE_ULT:
                return effectRegistry.getEffect("malphite_ult");
            default:
                return effectRegistry.getRandomLucky();
        }
    }

    private void handleUnluckyEffect(Player player) {
        player.sendMessage(UNLUCKY_MESSAGE);

        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
        player.getWorld().spawnParticle(
                Particle.SMOKE,
                player.getLocation().add(0, 1, 0),
                15, 0.3, 0.3, 0.3, 0.05
        );

        LuckyEffect effect = effectRegistry.getRandomUnlucky();
        if (effect != null) {
            String effectDescription = effect.apply(player);
            EffectUtils.broadcastEffectMessage(player, effectDescription, effect.getRarity(), false);
        }
    }
}
