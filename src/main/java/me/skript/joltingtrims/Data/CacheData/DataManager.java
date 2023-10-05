package me.skript.joltingtrims.Data.CacheData;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.JUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
        // Loop through all the online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Get their open inventory if there is any
            InventoryView openInventory = onlinePlayer.getOpenInventory();

            // Check if the opened inventory is one of the plugin's menus
            if (openInventory.getTitle().equals(JUtil.format(plugin.getGeneralMenuFile().getString("menu-title"))) ||
                    openInventory.getTitle().equals(JUtil.format(plugin.getMaterialMenuFile().getString("menu-title"))) ||
                    openInventory.getTitle().equals(JUtil.format(plugin.getPatternMenuFile().getString("menu-title")))) {

                // Close the opened inventory
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

    public static int getUnlockedTrimMaterials(Player player) {
        int counter = 0;

        ConfigurationSection materialsSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

        if (materialsSection != null) {
            for (String materialKey : materialsSection.getKeys(false)) {
                ConfigurationSection materialSettings = materialsSection.getConfigurationSection(materialKey);

                if (materialSettings != null && materialSettings.getBoolean("enabled") && player.hasPermission(materialSettings.getString("permission"))) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public static int getUnlockedTrimPatterns(Player player) {
        int counter = 0;

        ConfigurationSection patternsSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");

        if (patternsSection != null) {
            for (String patternKey : patternsSection.getKeys(false)) {
                ConfigurationSection patternSettings = patternsSection.getConfigurationSection(patternKey);

                if (patternSettings != null && patternSettings.getBoolean("enabled") && player.hasPermission(patternSettings.getString("permission"))) {
                    counter++;
                }
            }
        }

        return counter;
    }

}
