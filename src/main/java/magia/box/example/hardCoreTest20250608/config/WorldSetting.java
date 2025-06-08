package magia.box.example.hardCoreTest20250608.config;

import org.bukkit.*;

public class WorldSetting {

    private static World hardcoreWorld = Bukkit.getWorlds().getFirst();

    public static void setUp() {
        setupHardcoreWorld();
        Bukkit.getLogger().info(ChatColor.BOLD + "" + ChatColor.DARK_RED + "Hardcore Setup起動" + ChatColor.RESET);
    }


    public static void setupHardcoreWorld() {
        World hardcore = hardcoreWorld;

        //TODO 死亡後に鯖側の後処理タイムを設ける 必要なら

        hardcore.setHardcore(false);
        hardcore.setDifficulty(Difficulty.HARD);
        hardcore.setPVP(true);

        hardcore.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        hardcore.setGameRule(GameRule.DISABLE_PLAYER_MOVEMENT_CHECK, true);
        hardcore.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
        hardcore.setGameRule(GameRule.NATURAL_REGENERATION, false);




    }
}
