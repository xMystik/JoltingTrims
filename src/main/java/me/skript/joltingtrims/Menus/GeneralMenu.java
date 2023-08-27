package me.skript.joltingtrims.Menus;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.JLib;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GeneralMenu {

    private JoltingTrims plugin = JoltingTrims.getInstance();

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(player, plugin.getGeneralMenuFile().getInt("menu-size"), JLib.format(plugin.getGeneralMenuFile().getString("menu-title")));

        ConfigurationSection layoutSection = plugin.getGeneralMenuFile().getConfigurationSection("Layout");

        JLib.setupInventoryLayout(layoutSection, inv);

        DataManager.getOrCreatePlayerData(player);
        player.openInventory(inv);
    }

}
