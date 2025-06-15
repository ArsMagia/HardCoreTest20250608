package magia.box.example.hardCoreTest20250608.effects.unlucky.individual;

import magia.box.example.hardCoreTest20250608.effects.EffectRarity;
import magia.box.example.hardCoreTest20250608.effects.EffectType;
import magia.box.example.hardCoreTest20250608.effects.base.UnluckyEffectBase;
import magia.box.example.hardCoreTest20250608.effects.core.EffectRegistration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

@EffectRegistration(
    id = "random_teleport",
    type = EffectType.UNLUCKY,
    rarity = EffectRarity.UNCOMMON,
    description = "ランダムな場所にテレポートしてしまう",
    category = "spatial"
)
public class RandomTeleportEffect extends UnluckyEffectBase {
    
    private final Random random = new Random();
    
    public RandomTeleportEffect(JavaPlugin plugin) {
        super(plugin, "ランダムテレポート", EffectRarity.UNCOMMON);
    }
    
    @Override
    public String apply(Player player) {
        Location loc = player.getLocation();
        int x = loc.getBlockX() + random.nextInt(40) - 20;
        int z = loc.getBlockZ() + random.nextInt(40) - 20;
        int y = loc.getWorld().getHighestBlockYAt(x, z);
        
        Location newLoc = new Location(loc.getWorld(), x + 0.5, y + 1, z + 0.5);
        player.teleport(newLoc);
        player.sendMessage(ChatColor.LIGHT_PURPLE + "ランダムな場所にテレポートしました！");
        return getDescription();
    }
}