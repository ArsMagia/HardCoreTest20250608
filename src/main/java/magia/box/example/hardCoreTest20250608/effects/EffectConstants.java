package magia.box.example.hardCoreTest20250608.effects;

import org.bukkit.ChatColor;

/**
 * 効果システム全体で使用される定数定義
 * 
 * @author HardCoreTest20250608
 * @since 1.0
 */
public final class EffectConstants {
    
    // ========== 時間関連定数 ==========
    
    /** ラッキーボックスのクールダウン時間（ミリ秒） */
    public static final long LUCKY_BOX_COOLDOWN_MS = 1000L;
    
    /** その他アイテムのクールダウン時間（ミリ秒） */
    public static final long ITEM_COOLDOWN_MS = 100L;
    
    /** ポーション雨のサイクル間隔（秒） */
    public static final int POTION_RAIN_CYCLE_SECONDS = 10;
    
    /** ポーション雨の警告時間（秒） */
    public static final int POTION_RAIN_WARNING_SECONDS = 3;
    
    /** ポーション雨の継続サイクル数 */
    public static final int POTION_RAIN_TOTAL_CYCLES = 2;
    
    /** 卵タクシーの移動時間（秒） */
    public static final int EGG_TAXI_DURATION_SECONDS = 15;
    
    /** 爆弾投擲の予告時間（秒） */
    public static final int BOMB_THROW_WARNING_SECONDS = 5;
    
    /** 爆弾のヒューズ時間（tick） */
    public static final int BOMB_FUSE_TICKS = 80;
    
    /** 炎上地帯の継続時間（秒） */
    public static final int FLAME_FIELD_DURATION_SECONDS = 10;
    
    /** モブ速度強化の継続時間（tick） */
    public static final int MOB_SPEED_BOOST_DURATION_TICKS = 1200;
    
    // ========== 体力・ダメージ関連定数 ==========
    
    /** 最小体力（ハート数） */
    public static final int MIN_HEALTH_HEARTS = 2;
    
    /** 最大体力（ハート数） */
    public static final int MAX_HEALTH_HEARTS = 16;
    
    /** 1ハートあたりの体力値 */
    public static final double HEALTH_PER_HEART = 2.0;
    
    /** 最小体力値（HP） */
    public static final double MIN_HEALTH_VALUE = MIN_HEALTH_HEARTS * HEALTH_PER_HEART;
    
    /** 最大体力値（HP） */
    public static final double MAX_HEALTH_VALUE = MAX_HEALTH_HEARTS * HEALTH_PER_HEART;
    
    /** LoneSwordの回復量 */
    public static final int LONE_SWORD_HEAL_AMOUNT = 4;
    
    /** LoneSwordの耐久力消費 */
    public static final int LONE_SWORD_DURABILITY_COST = 50;
    
    /** PhantomBladeの耐久力消費 */
    public static final int PHANTOM_BLADE_DURABILITY_COST = 10;
    
    /** PhantomBladeのダッシュ力 */
    public static final double PHANTOM_BLADE_DASH_POWER = 2.0;
    
    // ========== 距離・範囲関連定数 ==========
    
    /** モブ速度強化の効果範囲（ブロック） */
    public static final int MOB_SPEED_BOOST_RADIUS = 50;
    
    /** モブ速度強化の復旧確認範囲（ブロック） */
    public static final int MOB_SPEED_BOOST_RECOVERY_RADIUS = 100;
    
    /** 爆弾投下高度（ブロック） */
    public static final int BOMB_DROP_HEIGHT = 20;
    
    /** 爆弾危険範囲表示半径（ブロック） */
    public static final int BOMB_DANGER_RADIUS = 4;
    
    /** 炎上地帯の最大半径（ブロック） */
    public static final int FLAME_FIELD_MAX_RADIUS = 8;
    
    /** 炎上地帯の初期炎数 */
    public static final int FLAME_FIELD_INITIAL_FIRES = 15;
    
    /** 炎上地帯の最大炎数 */
    public static final int FLAME_FIELD_MAX_FIRES = 30;
    
    /** ポーション雨の高度（ブロック） */
    public static final int POTION_RAIN_HEIGHT = 20;
    
    /** マルファイトUltの突進距離（ブロック） */
    public static final int MALPHITE_ULT_DISTANCE = 12;
    
    /** マルファイトUltの突進時間（tick） */
    public static final int MALPHITE_ULT_DURATION_TICKS = 15;
    
    // ========== 確率・重み関連定数 ==========
    
    /** ラッキー判定確率（%） */
    public static final double LUCKY_CHANCE_PERCENT = 50.0;
    
    /** 炎上地帯の足元回避確率 */
    public static final double FLAME_FIELD_FOOT_AVOIDANCE_CHANCE = 0.7;
    
    /** モブ速度強化倍率 */
    public static final double MOB_SPEED_MULTIPLIER = 2.0;
    
    // ========== アイテム価値算出定数 ==========
    
    /** アイテム価値: ダイヤモンド */
    public static final int ITEM_VALUE_DIAMOND = 100;
    
    /** アイテム価値: ネザライト */
    public static final int ITEM_VALUE_NETHERITE = 150;
    
    /** アイテム価値: エメラルド */
    public static final int ITEM_VALUE_EMERALD = 80;
    
    /** アイテム価値: 金 */
    public static final int ITEM_VALUE_GOLD = 50;
    
    /** アイテム価値: 鉄 */
    public static final int ITEM_VALUE_IRON = 30;
    
    /** アイテム価値: 武器・ツール */
    public static final int ITEM_VALUE_WEAPON_TOOL = 40;
    
    /** アイテム価値: 防具 */
    public static final int ITEM_VALUE_ARMOR = 35;
    
    /** アイテム価値: エンチャント1つあたり */
    public static final int ITEM_VALUE_ENCHANT = 20;
    
    /** アイテム価値: カスタム名 */
    public static final int ITEM_VALUE_CUSTOM_NAME = 25;
    
    /** アイテム価値: スタック数1つあたり */
    public static final int ITEM_VALUE_PER_STACK = 2;
    
    /** アイテム価値: 特別アイテム */
    public static final int ITEM_VALUE_SPECIAL = 120;
    
    // ========== メッセージテンプレート ==========
    
    /** クールダウンメッセージ */
    public static final String COOLDOWN_MESSAGE_TEMPLATE = ChatColor.RED + "%sのクールダウン中です！残り: " + ChatColor.YELLOW + "%.1f秒";
    
    /** 効果発動ブロードキャストメッセージ（ラッキー） */
    public static final String LUCKY_BROADCAST_TEMPLATE = ChatColor.GOLD + "✨ " + ChatColor.YELLOW + "%s" + 
            ChatColor.GRAY + " がラッキーボックスで %s" + ChatColor.GRAY + " の " + 
            ChatColor.GREEN + "%s" + ChatColor.GRAY + " を引き当てました！";
    
    /** 効果発動ブロードキャストメッセージ（アンラッキー） */
    public static final String UNLUCKY_BROADCAST_TEMPLATE = ChatColor.DARK_GRAY + "💀 " + ChatColor.YELLOW + "%s" + 
            ChatColor.GRAY + " がラッキーボックスで %s" + ChatColor.GRAY + " の " + 
            ChatColor.RED + "%s" + ChatColor.GRAY + " を引いてしまいました...";
    
    /** 権限不足メッセージ */
    public static final String PERMISSION_DENIED_MESSAGE = ChatColor.RED + "権限がありません。このコマンドはOP権限が必要です。";
    
    /** プレイヤー限定メッセージ */
    public static final String PLAYER_ONLY_MESSAGE = ChatColor.RED + "このコマンドはプレイヤーのみ実行できます。";
    
    // ========== サウンド・パーティクル関連定数 ==========
    
    /** 標準音量 */
    public static final float STANDARD_VOLUME = 1.0f;
    
    /** 標準ピッチ */
    public static final float STANDARD_PITCH = 1.0f;
    
    /** 警告音ピッチ */
    public static final float WARNING_PITCH = 0.5f;
    
    /** 成功音ピッチ */
    public static final float SUCCESS_PITCH = 1.5f;
    
    /** パーティクル標準数 */
    public static final int STANDARD_PARTICLE_COUNT = 10;
    
    /** パーティクル大量数 */
    public static final int LARGE_PARTICLE_COUNT = 50;
    
    // ========== プライベートコンストラクタ ==========
    
    /**
     * インスタンス化を防ぐためのプライベートコンストラクタ
     */
    private EffectConstants() {
        throw new AssertionError("EffectConstants should not be instantiated");
    }
}