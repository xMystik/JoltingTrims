package me.skript.joltingtrims.Listeners;


import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.MaterialMenu;
import me.skript.joltingtrims.Menus.PatternMenu;
import me.skript.joltingtrims.Utilities.*;
import me.skript.joltingtrims.Utilities.Enums.ItemType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GeneralMenuListener implements Listener {

    private JoltingTrims plugin;

    public GeneralMenuListener(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onGeneralMenuClick(InventoryClickEvent event) {

        if (event.getClickedInventory() == null || event.getCurrentItem() == null) {
            return;
        }

        if (event.getClickedInventory().getType() != InventoryType.CREATIVE && event.getView().getTitle().equals(JUtil.format(plugin.getGeneralMenuFile().getString("menu-title")))) {
            Player player = (Player) event.getWhoClicked();

            event.setCancelled(true);

            // Handle all the Item movement clicks
            handleItemMovement(event, player);

            // Handle all the other Items on the menu
            handleOtherInteractions(event, player, event.getView().getTopInventory());
        }
    }

    private void handleItemMovement(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        Inventory topInventory = event.getView().getTopInventory();
        int guiSlot = plugin.getGeneralMenuFile().getInt("item-slot");

        if (!JUtil.isArmorPiece(clickedItem.getType())) {
            return;
        }

        if (event.getRawSlot() == guiSlot) {
            ItemStack currentSlotItem = topInventory.getItem(guiSlot);

            if (currentSlotItem != null) {
                DataManager.clearPlayerData(player);
                player.getInventory().addItem(currentSlotItem);
                topInventory.setItem(guiSlot, null);
            }
        } else {
            player.getInventory().removeItem(clickedItem);

            ItemStack currentSlotItem = topInventory.getItem(guiSlot);

            if (currentSlotItem != null) {
                DataManager.clearPlayerData(player);
                player.getInventory().addItem(currentSlotItem);
            }

            DataManager.getOrCreatePlayerData(player).setEditingItem(clickedItem);
            topInventory.setItem(guiSlot, clickedItem);
        }
    }


    private void handleOtherInteractions(InventoryClickEvent event, Player player, Inventory topInventory) {
        ConfigurationSection layoutSection = plugin.getGeneralMenuFile().getConfigurationSection("Layout");

        //Get the children of the Layout section
        if (layoutSection != null) {
            // Loop through each child
            for (String itemName : layoutSection.getKeys(false)) {
                // Get the section of each child name containing its attributes
                ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                // Check if the child exists, if it does get his type, material and slots
                if (itemSection != null) {
                    String type = itemSection.getString("type");
                    String materialName = itemSection.getString("material");
                    List<Integer> slots = itemSection.getIntegerList("slots");

                    // Checks if the material is anything else but none
                    // Checks if the slots of the specified Item are the ones that were triggered on the InventoryClickEvent
                    if (!"none".equals(materialName) && slots.contains(event.getSlot())) {
                        handleItemTypeInteraction(player, type, topInventory);
                    }
                }
            }
        }
    }

    private void handleItemTypeInteraction(Player player, String itemType, Inventory topInventory) {
        // Checks the Item's interaction type
        Sound buttonSound = Sound.valueOf(plugin.getGeneralMenuFile().getString("button-click-sound"));

        if (itemType.equals(ItemType.MATERIAL_MENU_OPENER.getString())) {

            new MaterialMenu().openMenu(player);
            JUtil.playSound(player, buttonSound);

        } else if (itemType.equals(ItemType.PATTERN_MENU_OPENER.getString())) {

            new PatternMenu().openMenu(player);
            JUtil.playSound(player, buttonSound);

        } else if (itemType.equals(ItemType.FINALIZE_CHANGES.getString())) {
            // Gets the item on the specified item slot
            ItemStack guiSlotItem = topInventory.getItem(plugin.getGeneralMenuFile().getInt("item-slot"));

            // Checks if an item exists on the specified slot. If it doesn't then throw an error message
            if (guiSlotItem == null) {
                player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("insert-item")));
                return;
            }

            PlayerData playerData = DataManager.getOrCreatePlayerData(player);
            player.getInventory().addItem(TrimBuilder.setupItem(playerData));
        }
    }

    @EventHandler
    public void onGeneralMenuOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        String inventoryTitle = event.getView().getTitle();
        Player player = (Player) event.getPlayer();

        if (inventoryTitle.equals(JUtil.format(plugin.getGeneralMenuFile().getString("menu-title")))) {
            ItemStack savedEditItem = DataManager.getOrCreatePlayerData(player).getEditingItem();
            if (savedEditItem != null) {
                inventory.setItem(plugin.getGeneralMenuFile().getInt("item-slot"), savedEditItem);
            }
        }
    }

    @EventHandler
    public void onGeneralMenuClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        String inventoryTitle = event.getView().getTitle();
        Player player = (Player) event.getPlayer();
        ItemStack editItem = event.getInventory().getItem(plugin.getGeneralMenuFile().getInt("item-slot"));

        // Check if InventoryClose was caused to open a new Inventory
        if(event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) {
            if(inventoryTitle.equals(JUtil.format(plugin.getGeneralMenuFile().getString("menu-title"))) && editItem != null && JUtil.isArmorPiece(editItem.getType())) {
                DataManager.getOrCreatePlayerData(player).setEditingItem(editItem);
            }
        }
        // Check if the reason is the plugin /reload command
        else if(event.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) {
            // Check if the files are reloading
            if(DataManager.isReloading()) {
                // If they are then check if the player's saved item is not null
                if(DataManager.getOrCreatePlayerData(player).getEditingItem() != null) {
                    // If its not null then give the item back to the player
                    player.getInventory().addItem(DataManager.getOrCreatePlayerData(player).getEditingItem());
                }
            }
            // Clear the player data
            DataManager.clearPlayerData(player);
        }
        else {
            if(inventoryTitle.equals(JUtil.format(plugin.getGeneralMenuFile().getString("menu-title"))) && editItem != null && JUtil.isArmorPiece(editItem.getType())) {
                DataManager.clearPlayerData(player);
                inventory.setItem(plugin.getGeneralMenuFile().getInt("item-slot"), new ItemStack(Material.AIR));
                player.getInventory().addItem(editItem);
            }
        }
    }

}
