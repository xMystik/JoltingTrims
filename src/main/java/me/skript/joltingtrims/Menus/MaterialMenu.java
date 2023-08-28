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

import java.util.List;

public class MaterialMenu {

    private JoltingTrims plugin = JoltingTrims.getInstance();

    public void openMenu(Player player) {
        Inventory inv = Bukkit.createInventory(player, plugin.getMaterialMenuFile().getInt("menu-size"), JLib.format(plugin.getMaterialMenuFile().getString("menu-title")));

        ConfigurationSection layoutSection = plugin.getMaterialMenuFile().getConfigurationSection("Layout");

        JLib.setupInventoryLayout(layoutSection, inv);

        ConfigurationSection materialSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

        if (materialSection != null) {

            PlayerData playerData = DataManager.getOrCreatePlayerData(player);
            TrimMaterial selectedTrimMaterial = playerData.getTrimMaterial();

            for (String matName : materialSection.getKeys(false)) {
                ConfigurationSection matSection = materialSection.getConfigurationSection(matName);
                if (matSection != null) {
                    boolean isEnabled = matSection.getBoolean("enabled", false);
                    if (isEnabled) {

                        List<String> itemLore = plugin.getMaterialMenuFile().getStringList("materials-lore");

                        for(int i = 0; i < itemLore.size(); i++) {
                            String loreLine = itemLore.get(i);

                            if(loreLine.contains("%PERMISSION%")) {
                                boolean hasPerm = player.hasPermission(matSection.getString("permission"));
                                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getMaterialMenuFile().getString("material-unlocked") : plugin.getMaterialMenuFile().getString("material-locked"));
                                itemLore.set(i, loreLine);
                            }
                        }

                        ItemStack matItem = new JItem.ItemBuilder(Material.getMaterial(matName))
                                .setAmount(1)
                                .setDisplayName(plugin.getMaterialMenuFile().getString("materials-name").replace("%MATERIAL%", JLib.getDisplayNameOfMaterial(Material.getMaterial(matName))))
                                .setLore(itemLore)
                                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                                .build();

                        // Check if this item's material matches the selected TrimMaterial
                        if (selectedTrimMaterial != null && JLib.convertToTrimMaterial(Material.getMaterial(matName)) == selectedTrimMaterial) {
                            // Add the glowing enchantment
                            matItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        }

                        // Adds the material item to the GUI
                        inv.addItem(matItem);
                    }
                }
            }
        }

        player.openInventory(inv);
    }
}
