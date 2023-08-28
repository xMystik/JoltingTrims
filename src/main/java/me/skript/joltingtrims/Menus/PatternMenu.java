package me.skript.joltingtrims.Menus;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.JItem;
import me.skript.joltingtrims.Utilities.JLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;

public class PatternMenu {

    private JoltingTrims plugin = JoltingTrims.getInstance();

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(player, plugin.getPatternMenuFile().getInt("menu-size"), JLib.format(plugin.getPatternMenuFile().getString("menu-title")));

        ConfigurationSection layoutSection = plugin.getPatternMenuFile().getConfigurationSection("Layout");

        JLib.setupInventoryLayout(layoutSection, inv);

        ConfigurationSection patternsSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");

        if (patternsSection != null) {

            PlayerData playerData = DataManager.getOrCreatePlayerData(player);
            TrimPattern selectedTrimPattern = playerData.getTrimPattern();

            for (String patName : patternsSection.getKeys(false)) {
                ConfigurationSection patSection = patternsSection.getConfigurationSection(patName);
                if (patSection != null) {
                    boolean isEnabled = patSection.getBoolean("enabled", false);

                    if (isEnabled) {

                        List<String> itemLore = plugin.getPatternMenuFile().getStringList("patterns-lore");

                        for(int i = 0; i < itemLore.size(); i++) {
                            String loreLine = itemLore.get(i);

                            if(loreLine.contains("%PERMISSION%")) {
                                boolean hasPerm = player.hasPermission(patSection.getString("permission"));
                                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getPatternMenuFile().getString("pattern-unlocked") : plugin.getPatternMenuFile().getString("pattern-locked"));
                                itemLore.set(i, loreLine);
                            }
                        }

                        ItemStack patItem = new JItem.ItemBuilder(Material.getMaterial(patName + "_ARMOR_TRIM_SMITHING_TEMPLATE"))
                                .setAmount(1)
                                .setDisplayName(plugin.getPatternMenuFile().getString("patterns-name").replace("%PATTERN%", JLib.capitalizeWords(patName)))
                                .setLore(itemLore)
                                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                                .build();

                        // Check if this pattern matches the selected TrimPattern
                        if (selectedTrimPattern != null && JLib.convertToTrimPattern(Material.getMaterial(patName + "_ARMOR_TRIM_SMITHING_TEMPLATE")) == selectedTrimPattern) {
                            // Add the glowing enchantment
                            patItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        }

                        // Add the trim item to the GUI
                        inv.addItem(patItem);
                    }
                }
            }
        }

        player.openInventory(inv);
    }

}
