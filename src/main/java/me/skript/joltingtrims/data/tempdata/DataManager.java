package me.skript.joltingtrims.data.tempdata;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.menus.GeneralMenu;
import me.skript.joltingtrims.menus.MaterialMenu;
import me.skript.joltingtrims.menus.PatternMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.HashMap;
import java.util.UUID;

public class DataManager {

    private final JoltingTrims plugin;
    private boolean reloadFlag = false;
    private final HashMap<UUID, PlayerData> managerMap = new HashMap<>();

    public DataManager(JoltingTrims plugin) {
        this.plugin = plugin;
    }

    public PlayerData getOrCreatePlayerData(Player player) {
        if(managerMap.containsKey(player.getUniqueId())) {
            return managerMap.get(player.getUniqueId());
        }
        else {
            PlayerData data = new PlayerData(player.getUniqueId());
            managerMap.put(player.getUniqueId(), data);
            return data;
        }
    }

    public void clearPlayerData(Player player) {
        managerMap.remove(player.getUniqueId());
    }

    public void closeAllMenus() {
        // Loop through all the online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Get their open inventory if there is any
            InventoryView openInventory = onlinePlayer.getOpenInventory();

            // Check if the opened inventory is one of the plugin's menus
            if(openInventory.getTopInventory().getHolder() instanceof GeneralMenu
                    || openInventory.getTopInventory().getHolder() instanceof MaterialMenu
                    || openInventory.getTopInventory().getHolder() instanceof PatternMenu) {
                // Close the opened inventory
                onlinePlayer.closeInventory();
            }
        }
    }

    public void startReloading() {
        reloadFlag = true;
    }
    public boolean isReloading() {
        return reloadFlag;
    }

    public int getUnlockedTrimMaterials(Player player) {
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

    public int getUnlockedTrimPatterns(Player player) {
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

    public int getMaxTrimPatterns() {
        ConfigurationSection patternsSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");
        int counter = 0;

        if (patternsSection != null) {
            for (String patternKey : patternsSection.getKeys(false)) {
                ConfigurationSection patternConfig = patternsSection.getConfigurationSection(patternKey);
                if (patternConfig != null && patternConfig.getBoolean("enabled")) {
                    counter++;
                }
            }
        }

        return counter;
    }

    public int getMaxTrimMaterials() {
        ConfigurationSection materialsSection = plugin.getConfigurationFile().getConfigurationSection("Materials");
        int counter = 0;

        if (materialsSection != null) {
            for (String materialKey : materialsSection.getKeys(false)) {
                ConfigurationSection materialConfig = materialsSection.getConfigurationSection(materialKey);
                if (materialConfig != null && materialConfig.getBoolean("enabled")) {
                    counter++;
                }
            }
        }

        return counter;
    }

}
