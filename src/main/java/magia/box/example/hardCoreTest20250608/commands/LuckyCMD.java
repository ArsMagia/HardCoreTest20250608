package magia.box.example.hardCoreTest20250608.commands;

import magia.box.example.hardCoreTest20250608.core.ItemRegistryAccessor;
import magia.box.example.hardCoreTest20250608.items.custom.LuckyBoxItem;
import magia.box.example.hardCoreTest20250608.effects.EffectConstants;
import magia.box.example.hardCoreTest20250608.effects.EffectUtils;
import magia.box.example.hardCoreTest20250608.effects.LuckyEffect;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LuckyCMD implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final LuckyBoxItem luckyBoxItem;
    private final LuckyTestGUI testGUI;

    public LuckyCMD(JavaPlugin plugin) {
        this.plugin = plugin;
        this.luckyBoxItem = ItemRegistryAccessor.getLuckyBoxItem();
        this.testGUI = new LuckyTestGUI(plugin, luckyBoxItem.getEffectRegistry());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(EffectConstants.PLAYER_ONLY_MESSAGE);
            return true;
        }

        Player player = (Player) sender;

        // listコマンドは誰でも使用可能
        if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
            if (args.length == 1) {
                showEffectsMenu(player);
            } else if (args[1].equalsIgnoreCase("lucky")) {
                showLuckyEffects(player);
            } else if (args[1].equalsIgnoreCase("unlucky")) {
                showUnluckyEffects(player);
            } else {
                showEffectsMenu(player);
            }
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage(EffectConstants.PERMISSION_DENIED_MESSAGE);
            return true;
        }

        // 引数がある場合はテストGUIを開く
        if (args.length > 0) {
            String type = args[0].toLowerCase();
            
            if (type.equals("lucky")) {
                testGUI.openLuckyEffectsGUI(player, 1);
                player.sendMessage(ChatColor.GREEN + "ラッキー効果テストGUIを開きました。");
                return true;
            } else if (type.equals("unlucky")) {
                testGUI.openUnluckyEffectsGUI(player, 1);
                player.sendMessage(ChatColor.RED + "アンラッキー効果テストGUIを開きました。");
                return true;
            } else {
                // 数値として解釈（従来の動作）
                try {
                    int amount = Integer.parseInt(args[0]);
                    if (amount <= 0 || amount > 64) {
                        player.sendMessage(ChatColor.RED + "数量は1から64の間で指定してください。");
                        return true;
                    }
                    giveItems(player, amount);
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "無効な引数です。'lucky' または 'unlucky' か数値を指定してください。");
                    return true;
                }
            }
        }

        // 引数がない場合は1個配布
        giveItems(player, 1);
        return true;
    }
    
    /**
     * プレイヤーにラッキーボックスを与える
     * @param player 対象プレイヤー
     * @param amount 数量
     */
    private void giveItems(Player player, int amount) {
        ItemStack luckyBox = luckyBoxItem.createItem();
        luckyBox.setAmount(amount);

        boolean success = EffectUtils.safeGiveItem(player, luckyBox);
        
        if (success) {
            player.sendMessage(ChatColor.GREEN + "ラッキーボックス x" + amount + " を取得しました！");
        } else {
            player.sendMessage(ChatColor.YELLOW + "インベントリが満杯のため、ラッキーボックスを足元にドロップしました。");
        }
    }
    
    private void showEffectsMenu(Player player) {
        List<LuckyEffect> luckyEffects = testGUI.getEffectRegistry().getAllLuckyEffects();
        List<LuckyEffect> unluckyEffects = testGUI.getEffectRegistry().getAllUnluckyEffects();
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "ラッキーボックス効果一覧" + ChatColor.GOLD + " ===");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "表示したい効果タイプをクリックしてください：");
        player.sendMessage("");
        
        // ラッキー効果ボタン
        TextComponent luckyButton = new TextComponent("[ラッキー効果を見る]");
        luckyButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        luckyButton.setBold(true);
        luckyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lucky list lucky"));
        luckyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ChatColor.GREEN + "ラッキー効果 (" + luckyEffects.size() + "種類)\n" + 
                ChatColor.GRAY + "クリックで一覧を表示").create()));
        
        player.spigot().sendMessage(luckyButton);
        player.sendMessage("");
        
        // アンラッキー効果ボタン
        TextComponent unluckyButton = new TextComponent("[アンラッキー効果を見る]");
        unluckyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
        unluckyButton.setBold(true);
        unluckyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lucky list unlucky"));
        unluckyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ChatColor.RED + "アンラッキー効果 (" + unluckyEffects.size() + "種類)\n" + 
                ChatColor.GRAY + "クリックで一覧を表示").create()));
        
        player.spigot().sendMessage(unluckyButton);
        player.sendMessage("");
        
        player.sendMessage(ChatColor.YELLOW + "合計: " + (luckyEffects.size() + unluckyEffects.size()) + "種類の効果");
        player.sendMessage(ChatColor.GRAY + "※ 右クリックで50%の確率でラッキー/アンラッキー判定");
        player.sendMessage("");
    }
    
    private void showLuckyEffects(Player player) {
        List<LuckyEffect> luckyEffects = testGUI.getEffectRegistry().getAllLuckyEffects();
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "=== ラッキー効果一覧 (" + luckyEffects.size() + "種類) ===");
        player.sendMessage("");
        
        for (LuckyEffect effect : luckyEffects) {
            String rarityColor = effect.getRarity().getColor().toString();
            player.sendMessage(ChatColor.GRAY + "• " + rarityColor + effect.getDescription() + 
                ChatColor.GRAY + " [" + effect.getRarity().getDisplayName() + "]");
        }
        
        player.sendMessage("");
        
        // 戻るボタン
        TextComponent backButton = new TextComponent("[メニューに戻る]");
        backButton.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        backButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lucky list"));
        backButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ChatColor.YELLOW + "効果選択メニューに戻る").create()));
        
        player.spigot().sendMessage(backButton);
        player.sendMessage("");
    }
    
    private void showUnluckyEffects(Player player) {
        List<LuckyEffect> unluckyEffects = testGUI.getEffectRegistry().getAllUnluckyEffects();
        
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "=== アンラッキー効果一覧 (" + unluckyEffects.size() + "種類) ===");
        player.sendMessage("");
        
        for (LuckyEffect effect : unluckyEffects) {
            String rarityColor = effect.getRarity().getColor().toString();
            player.sendMessage(ChatColor.GRAY + "• " + rarityColor + effect.getDescription() + 
                ChatColor.GRAY + " [" + effect.getRarity().getDisplayName() + "]");
        }
        
        player.sendMessage("");
        
        // 戻るボタン
        TextComponent backButton = new TextComponent("[メニューに戻る]");
        backButton.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        backButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lucky list"));
        backButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder(ChatColor.YELLOW + "効果選択メニューに戻る").create()));
        
        player.spigot().sendMessage(backButton);
        player.sendMessage("");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            
            // listオプション（誰でも使用可能）
            if ("list".startsWith(input)) {
                suggestions.add("list");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("list")) {
            String input = args[1].toLowerCase();
            
            if ("lucky".startsWith(input)) {
                suggestions.add("lucky");
            }
            if ("unlucky".startsWith(input)) {
                suggestions.add("unlucky");
            }
            
            // テストオプション（OPのみ表示）
            if (sender instanceof Player && ((Player) sender).isOp()) {
                if ("lucky".startsWith(input)) {
                    suggestions.add("lucky");
                }
                if ("unlucky".startsWith(input)) {
                    suggestions.add("unlucky");
                }
                
                // 数量オプション（1-64）
                for (int i = 1; i <= 10; i++) {
                    String num = String.valueOf(i);
                    if (num.startsWith(input)) {
                        suggestions.add(num);
                    }
                }
            } else {
                // 非OPでもlucky/unluckyのTab補完は表示（ただし実行時にOP権限チェック）
                if ("lucky".startsWith(input)) {
                    suggestions.add("lucky");
                }
                if ("unlucky".startsWith(input)) {
                    suggestions.add("unlucky");
                }
            }
        }
        
        return suggestions;
    }
}