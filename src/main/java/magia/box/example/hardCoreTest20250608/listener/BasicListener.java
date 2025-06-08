package magia.box.example.hardCoreTest20250608.listener;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.items.custom.PlayerTrackingCompassItem;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BasicListener implements Listener {
    private static boolean isHardcoreHere = true;

    private boolean changeHardcore(boolean set) {
        isHardcoreHere = set;
        return isHardcoreHere;
    }

    @EventHandler
    public void onFoodEat(PlayerItemConsumeEvent event) {
        final Player player = event.getPlayer();
        Material item = event.getItem().getType();
        if (!(item == Material.ROTTEN_FLESH || item == Material.SPIDER_EYE)) {
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            double newHealth = Math.min(player.getHealth() + 1, maxHealth);
            player.setHealth(newHealth);
        }
    }

    @EventHandler
    public void onHardcoreDeath(PlayerDeathEvent event) {
        Player criminal = event.getEntity();
        Location deathLoc = criminal.getLocation();
        int locX = deathLoc.getBlockX();
        int locY = deathLoc.getBlockY();
        int locZ = deathLoc.getBlockZ();
        String plo = locX + "," + locY + "," + locZ;
        String worldName = deathLoc.getWorld().getName();

        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "####################################");
        
        // 通常のメッセージを送信
        for (Player player : Bukkit.getOnlinePlayers()) {
            // 基本メッセージ
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "ハードコア失敗： " +
                    ChatColor.RESET + criminal.getName() + ChatColor.GRAY + "が死亡したのでやり直し！！");
            
            // クリック可能なテレポートメッセージ
            TextComponent tpMessage = new TextComponent(ChatColor.YELLOW + "[クリックして死亡地点(" + plo + ")にテレポート]");
            tpMessage.setColor(net.md_5.bungee.api.ChatColor.GOLD);
            tpMessage.setBold(true);
            tpMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
                "/tp " + player.getName() + " " + locX + " " + locY + " " + locZ));
            tpMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                new ComponentBuilder(ChatColor.GREEN + "クリックでテレポート\n" + 
                    ChatColor.GRAY + "座標: " + worldName + " " + plo).create()));
            
            player.spigot().sendMessage(tpMessage);
        }
        
        Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "####################################");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" ");
        Bukkit.broadcastMessage(" ");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.ENTITY_PLAYER_HURT, 1, 1);
            player.playSound(player, Sound.ENTITY_PLAYER_DEATH, 1, 1);
            player.playSound(player, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.5F, 1);
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 0.5F, 1);
            player.playSound(player, Sound.BLOCK_GLASS_BREAK, 1.0F, 1);
            player.spawnParticle(Particle.CLOUD,
                    player.getLocation().getX(),
                    player.getLocation().getY() + 1,
                    player.getLocation().getZ(),
                    100, 1.0, 0.5, 1.0, 0.1);
            player.spawnParticle(Particle.EXPLOSION,
                    player.getLocation().getX(),
                    player.getLocation().getY() + 1,
                    player.getLocation().getZ(),
                    20, 1.0, 1.0, 1.0, 1.0);

            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    // 連打攻撃とグラップル配布
    @EventHandler
    public void onJoinAttachOldPvP(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(16);
        
        // グラップルアイテムを持っていない場合は配布
        boolean hasGrapple = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && ItemRegistryAccessor.getGrappleItem().isCustomItem(item)) {
                hasGrapple = true;
                break;
            }
        }
        
        if (!hasGrapple) {
            ItemStack grappleItem = ItemRegistryAccessor.getGrappleItem().createItem();
            player.getInventory().addItem(grappleItem);
            player.sendMessage(ChatColor.GREEN + "グラップルアイテムを受け取りました！");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // プレイヤー追跡コンパスのクリーンアップ
        PlayerTrackingCompassItem.cleanupPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onAttackWithAxe(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker) {
            Entity entity = event.getEntity();

            Material isAxe = attacker.getInventory().getItemInMainHand().getType();

            if (isAxe == Material.DIAMOND_AXE || isAxe == Material.IRON_AXE
                    || isAxe == Material.STONE_AXE || isAxe == Material.NETHERITE_AXE || isAxe == Material.WOODEN_AXE) {
                event.setDamage(1);
            }


        }
    }
}
