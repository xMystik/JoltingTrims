package me.skript.joltingtrims.Listeners;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.GeneralMenu;
import me.skript.joltingtrims.Utilities.*;
import me.skript.joltingtrims.Utilities.Enums.ItemType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MaterialMenuListener implements Listener {

    private JoltingTrims plugin;

    public MaterialMenuListener(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMaterialMenuClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (!(event.getClickedInventory() != null && event.getCurrentItem() != null && event.getClickedInventory().getType() != InventoryType.CREATIVE
                && event.getView().getTitle().equals(JLib.format(plugin.getMaterialMenuFile().getString("menu-title"))))) {
            return;
        }

        event.setCancelled(true);

        // Handle the Layout Item clicks
        handleLayoutItemClick(player, clickedItem);

        // Handle the Material Item clicks
        handleMaterialItemClick(player, clickedItem, event);
    }

    private void handleLayoutItemClick(Player player, ItemStack clickedItem) {
        // Get the Layout section
        ConfigurationSection layoutSection = plugin.getMaterialMenuFile().getConfigurationSection("Layout");

        // Check if it exists
        if (layoutSection != null) {
            // Get the children of Layout
            for (String itemName : layoutSection.getKeys(false)) {
                // Get their name
                ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                // Check if they exist
                if (itemSection != null) {
                    // If they do then get their type, material and slots
                    String type = itemSection.getString("type");
                    String materialName = itemSection.getString("material");
                    List<Integer> slots = itemSection.getIntegerList("slots");

                    // Check if it's a valid item section and the clicked item is a config item
                    if (JLib.isValidItemSection(materialName, slots) && JLib.isConfigItem(clickedItem, itemSection)) {
                        if (type.equals(ItemType.GENERAL_MENU_OPENER.getString())) {
                            new GeneralMenu().openMenu(player);
                            JLib.playSound(player, plugin.getMaterialMenuFile().getString("button-click-sound"));
                        }
                    }
                }
            }
        }
    }

    private void handleMaterialItemClick(Player player, ItemStack clickedItem, InventoryClickEvent event) {
        ConfigurationSection materialSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

        // Check if the Materials section exists
        if (materialSection != null) {
            // Get each child of the Materials section
            for (String matName : materialSection.getKeys(false)) {
                // Get the children name
                ConfigurationSection matSection = materialSection.getConfigurationSection(matName);

                // Check if the attributes of the Children exist
                if (JLib.isValidMaterial(matSection)) {
                    handleMaterialClick(player, clickedItem, matSection, event);
                }
            }
        }
    }

    private void handleMaterialClick(Player player, ItemStack clickedItem, ConfigurationSection matSection, InventoryClickEvent event) {
        List<String> itemLore = plugin.getMaterialMenuFile().getStringList("materials-lore");
        PlayerData playerData = DataManager.getOrCreatePlayerData(player);

        for (int i = 0; i < itemLore.size(); i++) {
            String loreLine = itemLore.get(i);

            if (loreLine.contains("%PERMISSION%")) {
                boolean hasPerm = player.hasPermission(matSection.getString("permission"));
                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getMaterialMenuFile().getString("material-unlocked") : plugin.getMaterialMenuFile().getString("material-locked"));
                itemLore.set(i, loreLine);
            }
        }

        ItemStack matItem = JLib.buildMaterialItem(matSection, itemLore);

        if (clickedItem.equals(matItem)) {
            // If player has permission to use that Material
            if (player.hasPermission(matSection.getString("permission"))) {

                // Set the previous TrimMaterial
                if (playerData.getTrimMaterial() != null) {
                    playerData.setPreviousTrimMaterial(playerData.getTrimMaterial());
                    // Disenchant the previous TrimMaterial
                    InventoryView inventoryView = player.getOpenInventory();
                    for (int slot = 0; slot < inventoryView.getTopInventory().getSize(); slot++) {
                        ItemStack itemInSlot = inventoryView.getTopInventory().getItem(slot);
                        if (itemInSlot != null && JLib.convertToTrimMaterial(itemInSlot.getType()) == playerData.getPreviousTrimMaterial()) {
                            itemInSlot.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
                        }
                    }
                }

                // Set the new TrimMaterial as the material that the player clicked
                playerData.setTrimMaterial(clickedItem.getType());
                JLib.playSound(player, plugin.getMaterialMenuFile().getString("material-unlocked-sound"));

                // Add an enchantment to make the item glow
                matItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                // Update the clicked item in the Material Menu with the new glowing item
                int clickedSlot = event.getRawSlot();
                player.getOpenInventory().getTopInventory().setItem(clickedSlot, matItem);
            }
            // If player does not have permission to use that Material
            else {
                player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-material")));
                JLib.playSound(player, plugin.getMaterialMenuFile().getString("material-locked-sound"));
            }
        }
    }

    @EventHandler
    public void onMaterialMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(!event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW) && DataManager.getOrCreatePlayerData(player).getEditingItem() != null) {
            player.getInventory().addItem(DataManager.getOrCreatePlayerData(player).getEditingItem());
            DataManager.clearPlayerData(player);
        }
    }
}
