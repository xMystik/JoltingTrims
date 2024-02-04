package me.skript.joltingtrims.commands;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.menus.GeneralMenu;
import me.skript.joltingtrims.utilities.JUtil;
import me.skript.joltingtrims.data.tempdata.DataManager;
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

    private final JoltingTrims plugin;
    private final DataManager dataManager;
    private final String cmd = "trim";

    public TrimCommand(JoltingTrims plugin) {
        this.plugin = plugin;

        this.dataManager = plugin.getDataManager();

        plugin.getCommand(cmd).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("menu")) {
                    if ((sender.hasPermission("joltingtrims.menu") && plugin.getConfigurationFile().getBoolean("trim-command")) || sender.isOp()) {
                        new GeneralMenu(player).openMenu();
                    }
                    else {
                        player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("no-permission-command")));
                    }
                    return true;
                }
                else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("joltingtrims.reload") || sender.isOp()) {
                        dataManager.startReloading();
                        dataManager.closeAllMenus();
                        plugin.reloadConfigurationFile();
                        plugin.reloadGeneralMenuFile();
                        plugin.reloadMessagesFile();
                        plugin.reloadPatternMenuFile();
                        plugin.reloadMaterialMenuFile();
                        player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("files-reload")));
                    }
                    else {
                        player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("no-permission-command")));
                    }
                    return true;
                }
                else if(args[0].equalsIgnoreCase("version")) {
                    player.sendMessage(JUtil.format("&3&l[JoltingTrims] &7Current plugin version: &e&l" + plugin.getPluginMeta().getVersion()));
                    return true;
                }
                else {
                    player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("command-usage")));
                    return true;
                }
            }
            else {
                player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("command-usage")));
                return true;
            }
        }
        else {
            sender.sendMessage(JUtil.format(plugin.getMessagesFile().getString("only-players")));
            return true;
        }

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
