package me.skript.joltingtrims.listeners;

import me.skript.joltingtrims.data.tempdata.DataManager;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.data.tempdata.PlayerData;
import me.skript.joltingtrims.menus.GeneralMenu;
import me.skript.joltingtrims.menus.MaterialMenu;
import me.skript.joltingtrims.menus.PatternMenu;
import me.skript.joltingtrims.utilities.*;
import me.skript.joltingtrims.utilities.enums.ItemType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GeneralMenuListener implements Listener {

    private final JoltingTrims plugin;
    private final DataManager dataManager;

    public GeneralMenuListener(JoltingTrims plugin) {
        this.plugin = plugin;
        dataManager = plugin.getDataManager();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onGeneralMenuClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null || !(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if(event.getView().getTopInventory().getHolder() instanceof GeneralMenu && event.getView().getBottomInventory().getHolder() instanceof Player) {
            event.setCancelled(true);

            dataManager.getOrCreatePlayerData(player);

            // Handle all the Item movement clicks
            handleItemMovement(event, player);

            // Handle all the other Items on the menu
            handleOtherInteractions(event, player, event.getView().getTopInventory());
        }
    }

    @EventHandler
    public void onGeneralMenuOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getPlayer();

        if(inventory.getHolder() instanceof GeneralMenu) {
            ItemStack savedEditItem = dataManager.getOrCreatePlayerData(player).getEditingItem();
            if (savedEditItem != null) {
                inventory.setItem(plugin.getGeneralMenuFile().getInt("item-slot"), savedEditItem);
            }
        }
    }

    @EventHandler
    public void onGeneralMenuClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if(inventory.getHolder() instanceof GeneralMenu) {

            Player player = (Player) event.getPlayer();
            ItemStack editItem = event.getInventory().getItem(plugin.getGeneralMenuFile().getInt("item-slot"));

            // Check if InventoryClose was caused to open a new Inventory
            if(event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) {
                dataManager.getOrCreatePlayerData(player).setEditingItem(editItem);
            }
            // Check if the reason is the plugin /reload command
            else if(event.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) {
                // Check if the files are reloading
                if(dataManager.isReloading()) {
                    // If they are then check if the player's saved item is not null
                    if(dataManager.getOrCreatePlayerData(player).getEditingItem() != null) {
                        // If its not null then give the item back to the player
                        player.getInventory().addItem(dataManager.getOrCreatePlayerData(player).getEditingItem());
                    }
                }
                // Clear the player data
                dataManager.clearPlayerData(player);
            }
            else {
                dataManager.clearPlayerData(player);
                inventory.setItem(plugin.getGeneralMenuFile().getInt("item-slot"), new ItemStack(Material.AIR));
                if(editItem != null) {
                    player.getInventory().addItem(editItem);
                }
            }

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
                dataManager.clearPlayerData(player);
                player.getInventory().addItem(currentSlotItem);
                topInventory.setItem(guiSlot, null);
            }
        } else {
            player.getInventory().removeItem(clickedItem);

            ItemStack currentSlotItem = topInventory.getItem(guiSlot);

            if (currentSlotItem != null) {
                dataManager.clearPlayerData(player);
                player.getInventory().addItem(currentSlotItem);
            }

            dataManager.getOrCreatePlayerData(player).setEditingItem(clickedItem);
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

            new MaterialMenu(player).openMenu();
            JUtil.playSound(player, buttonSound);

        } else if (itemType.equals(ItemType.PATTERN_MENU_OPENER.getString())) {

            new PatternMenu(player).openMenu();
            JUtil.playSound(player, buttonSound);

        } else if (itemType.equals(ItemType.FINALIZE_CHANGES.getString())) {
            // Gets the item on the specified item slot
            ItemStack guiSlotItem = topInventory.getItem(plugin.getGeneralMenuFile().getInt("item-slot"));

            // Checks if an item exists on the specified slot. If it doesn't then throw an error message
            if (guiSlotItem == null) {
                player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("insert-item")));
                return;
            }

            PlayerData playerData = dataManager.getOrCreatePlayerData(player);
            player.getInventory().addItem(JTrimFactory.setupItem(playerData));
        }
    }

}
