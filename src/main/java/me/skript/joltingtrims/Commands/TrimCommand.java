package me.skript.joltingtrims.Commands;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.GeneralMenu;
import me.skript.joltingtrims.Utilities.JLib;
import me.skript.joltingtrims.Data.CacheData.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class TrimCommand implements CommandExecutor, TabCompleter {

    private JoltingTrims plugin;
    private final String cmd = "trim";

    public TrimCommand(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getCommand(cmd).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(command.getName().equalsIgnoreCase(cmd)) {

            if (sender instanceof Player) {

                Player player = (Player) sender;

                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("menu")) {
                        if ((sender.hasPermission("joltingtrims.menu") && plugin.getConfigurationFile().getBoolean("trim-command")) || sender.isOp()) {
                            new GeneralMenu().openMenu(player);
                        }
                        else {
                            player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-command")));
                        }
                        return true;
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if (sender.hasPermission("joltingtrims.reload") || sender.isOp()) {
                            DataManager.startReloading();
                            DataManager.closeAllMenus();
                            plugin.reloadConfigurationFile();
                            plugin.reloadGeneralMenuFile();
                            plugin.reloadMessagesFile();
                            plugin.reloadPatternMenuFile();
                            plugin.reloadMaterialMenuFile();
                            player.sendMessage(JLib.format(plugin.getMessagesFile().getString("files-reload")));
                        }
                        else {
                            player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-command")));
                        }
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("version")) {
                        player.sendMessage(JLib.format("&3&l[JoltingTrims] &7Current plugin version: &e&l1.0.1"));
                        return true;
                    }
                    else {
                        player.sendMessage(JLib.format(plugin.getMessagesFile().getString("command-usage")));
                        return true;
                    }
                }
                else {
                    player.sendMessage(JLib.format(plugin.getMessagesFile().getString("command-usage")));
                    return true;
                }
            }
            else {
                sender.sendMessage(JLib.format(plugin.getMessagesFile().getString("only-players")));
                return true;
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) {

            if(args.length == 1 && plugin.getConfigurationFile().getBoolean("trim-command")) {
                return Arrays.asList("menu", "reload", "version");
            }

        }

        return null;
    }
}
