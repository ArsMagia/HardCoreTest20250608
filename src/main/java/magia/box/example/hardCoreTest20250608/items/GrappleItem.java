package magia.box.example.hardCoreTest20250608.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class GrappleItem extends
        AbstractCustomItemV2 {

    private final ArrayList<UUID> grappling = new ArrayList<>();
    private static final int POWER = 5;

    public GrappleItem(JavaPlugin plugin) {
        super(plugin, builder("grapple", "グラップル")
                .material(Material.FISHING_ROD)
                .rarity(ItemRarity.UNCOMMON)
                .addLore("釣り竿でグラップリング！")
                .addLore("地面や壁に向かって使用")
                .addHint("パワー: " + POWER));
    }

    @EventHandler
    public void onGrappleThrow(PlayerFishEvent event) {
        final Player player = event.getPlayer();
        final UUID uuid = player.getUniqueId();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (!isCustomItem(itemInHand)) {
            return;
        }

        Vector grapple = event.getHook().getLocation().subtract(player.getLocation()).toVector().normalize().multiply(POWER);
        Entity hookedEntity = event.getHook().getHookedEntity();

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            if (event.getHand() == null) return;
            grappling.add(uuid);
        } else if (grappling.contains(uuid)) {
            switch (event.getState()) {
                case REEL_IN:
                    if (!event.getHook().isOnGround() && !event.getHook().isInWater()) {
                        player.sendMessage(ChatColor.RED + "グラップリングできません");
                        break;
                    }
                    player.setVelocity(player.getVelocity().add(grapple));
                case IN_GROUND:
                    player.setVelocity(player.getVelocity().add(grapple));
                    break;
                case CAUGHT_ENTITY:
                    if (hookedEntity == null || hookedEntity.getType() != EntityType.PLAYER) break;
                    if (hookedEntity.getType() == EntityType.PLAYER) {
                        player.setVelocity(player.getVelocity().add(grapple));
                    }
                    break;
                default:
                    break;
            }
            grappling.remove(uuid);
        }
    }
}