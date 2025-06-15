package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * オールフォーワン
 * 発動者にタグを付与し、次にダメージを受けた際に全プレイヤーが同じダメージを受ける
 */
public class AllForOneEffect extends UnluckyEffectBase implements Listener {

    // タグ付きプレイヤーを管理
    private static final Map<UUID, AllForOneTag> taggedPlayers = new HashMap<>();
    private static AllForOneEffect instance;

    public AllForOneEffect(JavaPlugin plugin) {
        super(plugin, "オールフォーワン", EffectRarity.EPIC);
        instance = this;
    }

    @Override
    public String apply(Player player) {
        // 既存のタグがある場合は削除
        if (taggedPlayers.containsKey(player.getUniqueId())) {
            removeTag(player.getUniqueId());
        }

        // 新しいタグを付与
        AllForOneTag tag = new AllForOneTag(player.getUniqueId());
        taggedPlayers.put(player.getUniqueId(), tag);

        // イベントリスナーを登録（まだ登録されていない場合）
        if (!isListenerRegistered()) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        // メッセージとエフェクト
        player.sendMessage(ChatColor.DARK_RED + "💀 オールフォーワンのタグが付与されました！");
        player.sendMessage(ChatColor.RED + "⚠ 次にダメージを受けると、全プレイヤーが同じダメージを受けます！");
        player.sendMessage(ChatColor.YELLOW + "🏷 このタグは時間制限がありません...");

        // 不吉な音とエフェクト
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 1.0f, 0.8f);
        player.getWorld().spawnParticle(
            Particle.DUST,
            player.getLocation().add(0, 2, 0),
            30, 1, 1, 1, 0.2,
            new Particle.DustOptions(org.bukkit.Color.fromRGB(128, 0, 0), 2.0f)
        );

        // サーバー全体への警告
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💀 オールフォーワンが発動！");
        Bukkit.broadcastMessage(ChatColor.RED + "🎯 " + player.getName() + " にタグが付与されました");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "⚠ " + player.getName() + " が次にダメージを受けると、全員が同じダメージを受けます！");
        Bukkit.broadcastMessage("");

        return getDescription();
    }

    /**
     * ダメージイベントを監視
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damagedPlayer = (Player) event.getEntity();
        UUID playerId = damagedPlayer.getUniqueId();

        // タグ付きプレイヤーがダメージを受けた場合
        if (taggedPlayers.containsKey(playerId)) {
            AllForOneTag tag = taggedPlayers.get(playerId);
            double damage = event.getFinalDamage();

            // タグを削除
            removeTag(playerId);

            // 爆発効果を実行
            executeAllForOneDamage(damagedPlayer, damage);
        }
    }

    /**
     * オールフォーワン効果を実行
     */
    private void executeAllForOneDamage(Player originalVictim, double damage) {
        // 他の全プレイヤーにダメージを与える
        int affectedPlayers = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(originalVictim)) {
                // 同じダメージを与える
                player.damage(damage);
                affectedPlayers++;

                // 個別メッセージ
                player.sendMessage(ChatColor.DARK_RED + "💥 " + originalVictim.getName() + " のオールフォーワンでダメージを受けました！");
                player.sendMessage(ChatColor.RED + "⚡ 受けたダメージ: " + String.format("%.1f", damage / 2) + "❤");

                // エフェクト
                player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_HURT, 1.0f, 0.8f);
                player.getWorld().spawnParticle(
                    Particle.DUST,
                    player.getLocation().add(0, 1, 0),
                    15, 0.5, 0.5, 0.5, 0.1,
                    new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 1.5f)
                );
            }
        }

        // 発動者へのメッセージ
        originalVictim.sendMessage(ChatColor.DARK_RED + "💀 オールフォーワンが爆発しました！");
        originalVictim.sendMessage(ChatColor.YELLOW + "🎯 " + affectedPlayers + "人のプレイヤーが同じダメージを受けました");

        // サーバー全体への通知
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "💥 オールフォーワンが爆発！");
        Bukkit.broadcastMessage(ChatColor.RED + "💀 " + originalVictim.getName() + " が " + String.format("%.1f", damage / 2) + "❤ のダメージを受けました");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "⚡ " + affectedPlayers + "人の他のプレイヤーも同じダメージを受けました！");
        Bukkit.broadcastMessage("");

        // 爆発エフェクト
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
            player.getWorld().spawnParticle(
                Particle.EXPLOSION,
                originalVictim.getLocation(),
                5, 2, 2, 2, 0.1
            );
        }

        // 全てのタグが処理されたらリスナーを解除
        if (taggedPlayers.isEmpty()) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * タグを削除
     */
    private void removeTag(UUID playerId) {
        taggedPlayers.remove(playerId);
        Player player = Bukkit.getPlayer(playerId);
        if (player != null && player.isOnline()) {
            player.sendMessage(ChatColor.GRAY + "🏷 オールフォーワンのタグが削除されました");
        }
    }

    /**
     * リスナーが登録されているかチェック
     */
    private boolean isListenerRegistered() {
        return HandlerList.getRegisteredListeners(plugin).stream()
                .anyMatch(listener -> listener.getListener() instanceof AllForOneEffect);
    }

    /**
     * プラグイン終了時のクリーンアップ
     */
    public static void cleanup() {
        if (instance != null) {
            HandlerList.unregisterAll(instance);
            taggedPlayers.clear();
        }
    }

    /**
     * タグ情報を管理するクラス
     */
    private static class AllForOneTag {
        private final UUID playerId;
        private final long createdTime;

        public AllForOneTag(UUID playerId) {
            this.playerId = playerId;
            this.createdTime = System.currentTimeMillis();
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public long getCreatedTime() {
            return createdTime;
        }
    }
}