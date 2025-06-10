package magia.box.example.hardCoreTest20250608.effects.lucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.LuckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class AnimalFriendEffect extends LuckyEffectBase {

    public AnimalFriendEffect(JavaPlugin plugin) {
        super(plugin, "誰も近寄るな", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        int knockedCount = 0;
        
        // 半径20ブロック以内のモブ全てを押し飛ばす
        for (Entity entity : center.getWorld().getNearbyEntities(center, 20, 20, 20)) {
            if (entity instanceof LivingEntity && entity != player) {
                LivingEntity mob = (LivingEntity) entity;
                
                // プレイヤーから遠ざける方向を計算
                Vector direction = mob.getLocation().toVector().subtract(center.toVector()).normalize();
                
                // 上方向にも少し押し上げる（Y軸の威力を半減）
                direction.setY(Math.max(direction.getY(), 0.25));
                
                // 強力なノックバック
                direction.multiply(3.0);
                mob.setVelocity(direction);
                
                knockedCount++;
                
                // ノックバックエフェクト
                mob.getLocation().getWorld().spawnParticle(
                    Particle.EXPLOSION,
                    mob.getLocation().add(0, mob.getHeight() / 2, 0),
                    5, 0.3, 0.3, 0.3, 0.1
                );
            }
        }
        
        if (knockedCount > 0) {
            player.sendMessage(ChatColor.RED + "誰も近寄るな！周囲の " + knockedCount + " 体のモブを押し飛ばしました！");
        } else {
            player.sendMessage(ChatColor.YELLOW + "周囲にモブがいませんでした。威圧オーラを放ちます。");
        }
        
        // 威圧的な音とエフェクト
        player.playSound(center, Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.8f);
        
        // 威圧オーラのエフェクト
        center.getWorld().spawnParticle(
            Particle.DUST,
            center.add(0, 1, 0),
            50, 2, 1, 2, 0.1,
            new Particle.DustOptions(org.bukkit.Color.RED, 2.0f)
        );
        
        // 衝撃波エフェクト
        for (int i = 1; i <= 20; i++) {
            final int radius = i;
            center.getWorld().spawnParticle(
                Particle.DUST,
                center.clone().add(radius, 0.1, 0),
                1, 0, 0, 0, 0,
                new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
            );
            center.getWorld().spawnParticle(
                Particle.DUST,
                center.clone().add(-radius, 0.1, 0),
                1, 0, 0, 0, 0,
                new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
            );
            center.getWorld().spawnParticle(
                Particle.DUST,
                center.clone().add(0, 0.1, radius),
                1, 0, 0, 0, 0,
                new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
            );
            center.getWorld().spawnParticle(
                Particle.DUST,
                center.clone().add(0, 0.1, -radius),
                1, 0, 0, 0, 0,
                new Particle.DustOptions(org.bukkit.Color.ORANGE, 1.5f)
            );
        }
        
        return getDescription();
    }
}