package magia.box.example.hardCoreTest20250608.effects.unlucky;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GroundLiquefactionEffect extends UnluckyEffectBase {

    public GroundLiquefactionEffect(JavaPlugin plugin) {
        super(plugin, "地面液化", EffectRarity.RARE);
    }

    @Override
    public String apply(Player player) {
        Location center = player.getLocation();
        List<Block> liquidBlocks = new ArrayList<>();
        
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                Block block = center.getWorld().getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z);
                if (block.getType().isSolid() && block.getType() != Material.BEDROCK) {
                    liquidBlocks.add(block);
                    block.setType(Material.WATER);
                }
            }
        }
        
        player.sendMessage(ChatColor.BLUE + "足元の地面が液化しました！30秒後に元に戻ります。");
        player.playSound(center, Sound.ITEM_BUCKET_FILL, 1.0f, 1.0f);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : liquidBlocks) {
                    if (block.getType() == Material.WATER) {
                        block.setType(Material.STONE);
                    }
                }
                player.sendMessage(ChatColor.GRAY + "地面が固まりました。");
            }
        }.runTaskLater(plugin, 600L);
        
        return getDescription();
    }
}