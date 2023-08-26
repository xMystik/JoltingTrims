package me.skript.joltingtrims.Data.CacheData;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.JLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;

public class DataManager {

    private static JoltingTrims plugin;
    private static boolean reloadFlag = false;
    private static HashMap<Player, PlayerData> managerMap = new HashMap<>();

    public DataManager(JoltingTrims plugin) {
        this.plugin = plugin;
    }

    public static PlayerData getOrCreatePlayerData(Player player) {
        if(managerMap.containsKey(player)) {
            return managerMap.get(player);
        }
        else {
            PlayerData data = new PlayerData(player);
            managerMap.put(player, data);
            return data;
        }
    }

    public static void clearPlayerData(Player player) {
        managerMap.remove(player);
    }

    public static void closeAllMenus() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            InventoryView openInventory = onlinePlayer.getOpenInventory();

            // Check if the open inventory is related to your plugin
            if (openInventory.getTitle().equals(JLib.format(plugin.getGeneralMenuFile().getString("menu-title"))) ||
                    openInventory.getTitle().equals(JLib.format(plugin.getMaterialMenuFile().getString("menu-title"))) ||
                    openInventory.getTitle().equals(JLib.format(plugin.getPatternMenuFile().getString("menu-title")))) {

                // Close the open inventory
                onlinePlayer.closeInventory();
            }
        }
    }

    public static boolean startReloading() {
        reloadFlag = true;
        return true;
    }
    public static boolean isReloading() {
        if(reloadFlag) {
            return true;
        }
        else {
            return false;
        }
    }

}
