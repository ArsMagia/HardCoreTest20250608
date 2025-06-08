package magia.box.example.hardCoreTest20250608.items;

import magia.box.example.hardCoreTest20250608.items.effects.*;
import magia.box.example.hardCoreTest20250608.items.effects.lucky.*;
import magia.box.example.hardCoreTest20250608.items.effects.unlucky.*;
import static magia.box.example.hardCoreTest20250608.items.effects.unlucky.SimpleUnluckyEffects.*;
import static magia.box.example.hardCoreTest20250608.items.effects.unlucky.MoreUnluckyEffects.*;
import static magia.box.example.hardCoreTest20250608.items.effects.unlucky.FinalUnluckyEffects.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class LuckyBoxItem extends AbstractCustomItemV2 {

    private static final String USE_MESSAGE = ChatColor.YELLOW + "ラッキーボックスを開きました！";
    private static final String LUCKY_MESSAGE = ChatColor.GREEN + "ラッキー！";
    private static final String UNLUCKY_MESSAGE = ChatColor.RED + "アンラッキー...";

    private final Random random = new Random();
    private final EffectRegistry effectRegistry;

    public LuckyBoxItem(JavaPlugin plugin) {
        super(plugin, builder("lucky_box", "ラッキーボックス")
                .material(Material.NETHER_STAR)
                .rarity(ItemRarity.EPIC)
                .addLore("右クリックで運試し！")
                .addLore("50%の確率でラッキー判定")
                .addHint("運が良ければ特別な効果が..."));
        
        this.effectRegistry = new EffectRegistry(plugin);
        initializeEffects();
    }

    public LuckyBoxItem(JavaPlugin plugin, ItemBuilder builder) {
        super(plugin, builder);
        this.effectRegistry = new EffectRegistry(plugin);
        initializeEffects();
    }

    private void initializeEffects() {
        // 時空間系ラッキー効果
        effectRegistry.registerEffect("time_stop", new TimeStopEffect(plugin));
        effectRegistry.registerEffect("safe_teleport", new SafeTeleportEffect(plugin));
        effectRegistry.registerEffect("time_rewind", new TimeRewindEffect(plugin));
        effectRegistry.registerEffect("future_vision", new FutureVisionEffect(plugin));
        
        // 環境操作系ラッキー効果
        effectRegistry.registerEffect("golden_path", new GoldenPathEffect(plugin));
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
        effectRegistry.registerEffect("luckybox_distribution", new LuckyBoxDistributionEffect(plugin));
        
        // 追加ラッキー効果
        effectRegistry.registerEffect("experience_magnet", new ExperienceMagnetEffect(plugin));
        effectRegistry.registerEffect("instant_mining", new InstantMiningEffect(plugin));
        effectRegistry.registerEffect("auto_build", new AutoBuildEffect(plugin));
        effectRegistry.registerEffect("multi_drop", new MultiDropEffect(plugin));
        
        // ポーション系ラッキー効果
        effectRegistry.registerEffect("random_potion_lucky", new RandomPotionLuckyEffect(plugin));
        effectRegistry.registerEffect("random_buff_potion", new RandomBuffPotionEffect(plugin));
        effectRegistry.registerEffect("multi_buff_combination", new MultiBuffCombinationEffect(plugin));
        effectRegistry.registerEffect("permanent_luck", new PermanentLuckEffect(plugin));
        
        // 体力増減系ラッキー効果
        effectRegistry.registerEffect("health_boost", new HealthBoostEffect(plugin));
        
        // 基本ラッキー効果
        effectRegistry.registerEffect("speed", new SpeedLuckyEffect(plugin));
        effectRegistry.registerEffect("temporary_flight", new TemporaryFlightEffect(plugin));
        effectRegistry.registerEffect("dimension_pocket", new DimensionPocketEffect(plugin));
        
        // 時空間系アンラッキー効果
        effectRegistry.registerEffect("spacetime_distortion", new SpaceTimeDistortionEffect(plugin));
        
        // 環境操作系アンラッキー効果
        effectRegistry.registerEffect("cursed_fog", new CursedFogEffect(plugin));
        
        // 変身・妨害系アンラッキー効果
        effectRegistry.registerEffect("chicken_transform", new ChickenTransformEffect(plugin));
        
        // アイテム・資源系アンラッキー効果
        effectRegistry.registerEffect("item_scatter", new ItemScatterEffect(plugin));
        
        // ポーション系アンラッキー効果
        effectRegistry.registerEffect("random_potion_unlucky", new RandomPotionUnluckyEffect(plugin));
        
        // 体力増減系アンラッキー効果
        effectRegistry.registerEffect("health_reduction", new HealthReductionEffect(plugin));
        
        // 基本アンラッキー効果
        effectRegistry.registerEffect("slowness", new SlownessUnluckyEffect(plugin));
        effectRegistry.registerEffect("gravity_flip", new GravityFlipEffect(plugin));
        effectRegistry.registerEffect("cursed_sounds", new CursedSoundsEffect(plugin));
        
        // 追加アンラッキー効果 (SimpleUnluckyEffects)
        effectRegistry.registerEffect("upside_down_vision", new UpsideDownVisionEffect(plugin));
        effectRegistry.registerEffect("ground_liquefaction", new GroundLiquefactionEffect(plugin));
        effectRegistry.registerEffect("giant_curse", new GiantCurseEffect(plugin));
        effectRegistry.registerEffect("food_decay", new FoodDecayEffect(plugin));
        effectRegistry.registerEffect("tool_breakage", new ToolBreakageEffect(plugin));
        effectRegistry.registerEffect("weight_curse", new WeightCurseEffect(plugin));
        effectRegistry.registerEffect("color_blindness", new ColorBlindnessEffect(plugin));
        effectRegistry.registerEffect("compass_confusion", new CompassConfusionEffect(plugin));
        effectRegistry.registerEffect("hallucination_sounds", new HallucinationSoundsEffect(plugin));
        effectRegistry.registerEffect("fake_info", new FakeInfoEffect(plugin));
        effectRegistry.registerEffect("shame_mark", new ShameMarkEffect(plugin));
        effectRegistry.registerEffect("curse_infection", new CurseInfectionEffect(plugin));
        effectRegistry.registerEffect("time_acceleration", new TimeAccelerationEffect(plugin));
        effectRegistry.registerEffect("dimension_rift", new DimensionRiftEffect(plugin));
        effectRegistry.registerEffect("falling_blocks", new FallingBlocksEffect(plugin));
        effectRegistry.registerEffect("weather_storm", new WeatherStormEffect(plugin));
        
        // SimpleUnluckyEffects のネストクラス
        effectRegistry.registerEffect("reverse_controls", new ReverseControlsEffect(plugin));
        effectRegistry.registerEffect("fake_death", new FakeDeathEffect(plugin));
        effectRegistry.registerEffect("random_teleport", new RandomTeleportEffect(plugin));
        effectRegistry.registerEffect("item_weight", new ItemWeightEffect(plugin));
        effectRegistry.registerEffect("hunger_curse", new HungerCurseEffect(plugin));
        effectRegistry.registerEffect("memory_loss", new MemoryLossEffect(plugin));
        effectRegistry.registerEffect("fire_rain", new FireRainEffect(plugin));
        effectRegistry.registerEffect("exp_leak", new ExpLeakEffect(plugin));
        effectRegistry.registerEffect("weapon_curse", new WeaponCurseEffect(plugin));
        effectRegistry.registerEffect("armor_vanish", new ArmorVanishEffect(plugin));
        
        // MoreUnluckyEffects のネストクラス
        effectRegistry.registerEffect("drowning_feeling", new DrowningFeelingEffect(plugin));
        effectRegistry.registerEffect("darkphobia", new DarkphobiaEffect(plugin));
        effectRegistry.registerEffect("hand_tremor", new HandTremorEffect(plugin));
        effectRegistry.registerEffect("leg_cramp", new LegCrampEffect(plugin));
        effectRegistry.registerEffect("sleep_deprivation", new SleepDeprivationEffect(plugin));
        effectRegistry.registerEffect("confusion_mist", new ConfusionMistEffect(plugin));
        effectRegistry.registerEffect("electric_shock", new ElectricShockEffect(plugin));
        effectRegistry.registerEffect("frozen_state", new FrozenStateEffect(plugin));
        effectRegistry.registerEffect("backward_walking", new BackwardWalkingEffect(plugin));
        effectRegistry.registerEffect("tool_addiction", new ToolAddictionEffect(plugin));
        effectRegistry.registerEffect("metal_allergy", new MetalAllergyEffect(plugin));
        effectRegistry.registerEffect("gravity_sickness", new GravitySicknessEffect(plugin));
        effectRegistry.registerEffect("hyperacusis", new HyperacusisEffect(plugin));
        effectRegistry.registerEffect("number_amnesia", new NumberAmnesiaEffect(plugin));
        effectRegistry.registerEffect("spell_failure", new SpellFailureEffect(plugin));
        
        // FinalUnluckyEffects のネストクラス
        effectRegistry.registerEffect("mirror_world", new MirrorWorldEffect(plugin));
        effectRegistry.registerEffect("evaporation", new EvaporationEffect(plugin));
        effectRegistry.registerEffect("petrification", new PetrificationEffect(plugin));
        effectRegistry.registerEffect("double_vision", new DoubleVisionEffect(plugin));
        effectRegistry.registerEffect("clock_malfunction", new ClockMalfunctionEffect(plugin));
        effectRegistry.registerEffect("shadow_binding", new ShadowBindingEffect(plugin));
        effectRegistry.registerEffect("oxygen_deprivation", new OxygenDeprivationEffect(plugin));
        effectRegistry.registerEffect("radio_interference", new RadioInterferenceEffect(plugin));
        effectRegistry.registerEffect("magnetic_anomaly", new MagneticAnomalyEffect(plugin));
        effectRegistry.registerEffect("phantom_pain", new PhantomPainEffect(plugin));
        effectRegistry.registerEffect("item_shuffle", new ItemShuffleEffect(plugin));
        effectRegistry.registerEffect("sound_spam", new SoundSpamEffect(plugin));
        effectRegistry.registerEffect("fake_damage", new FakeDamageEffect(plugin));
        
        // QuickDebuff バリエーション
        effectRegistry.registerEffect("heavy_feet", createSlownessVariant(plugin));
        effectRegistry.registerEffect("power_drain", createWeaknessVariant(plugin));
        effectRegistry.registerEffect("mining_fatigue_v2", createMiningFatigueVariant(plugin));
        effectRegistry.registerEffect("intense_hunger", createHungerVariant(plugin));
        effectRegistry.registerEffect("dizziness", createNauseaVariant(plugin));
        effectRegistry.registerEffect("temporary_blindness", createBlindnessVariant(plugin));
        effectRegistry.registerEffect("mild_poison", createPoisonVariant(plugin));
        effectRegistry.registerEffect("withering", createWitherVariant(plugin));
        effectRegistry.registerEffect("light_levitation", createLevitationVariant(plugin));
        effectRegistry.registerEffect("misfortune", createUnluckVariant(plugin));
        effectRegistry.registerEffect("darkness_veil", createDarknessVariant(plugin));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (!event.getAction().toString().contains("RIGHT_CLICK")) {
            return;
        }

        if (!isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);

        // アイテムを1つ消費
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }

        // 50%の確率でラッキー/アンラッキー判定
        boolean isLucky = random.nextBoolean();

        player.sendMessage(USE_MESSAGE);

        if (isLucky) {
            handleLuckyEffect(player);
        } else {
            handleUnluckyEffect(player);
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
            
            Bukkit.broadcastMessage(ChatColor.GOLD + "✨ " + ChatColor.YELLOW + player.getName() + 
                    ChatColor.GRAY + " がラッキーボックスで " + ChatColor.GREEN + effectDescription + 
                    ChatColor.GRAY + " を引き当てました！");
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
            
            Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "💀 " + ChatColor.YELLOW + player.getName() + 
                    ChatColor.GRAY + " がラッキーボックスで " + ChatColor.RED + effectDescription + 
                    ChatColor.GRAY + " を引いてしまいました...");
        }
    }
}
