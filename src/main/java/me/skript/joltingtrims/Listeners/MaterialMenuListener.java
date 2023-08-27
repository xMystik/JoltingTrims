package me.skript.joltingtrims.Listeners;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.GeneralMenu;
import me.skript.joltingtrims.Utilities.*;
import me.skript.joltingtrims.Utilities.Enums.ItemType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
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
        //Inventory clickedInventory = event.getClickedInventory();
        //String inventoryTitle = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (!(event.getClickedInventory() != null
                && event.getCurrentItem() != null
                && event.getClickedInventory().getType() != InventoryType.CREATIVE
                && event.getView().getTitle().equals(JLib.format(plugin.getMaterialMenuFile().getString("menu-title"))))) {
            return;
        }

        event.setCancelled(true);

        // Handle the Layout Item clicks
        handleLayoutItemClick(player, clickedItem);

        // Handle the Material Item clicks
        handleMaterialItemClick(player, clickedItem);



        /*if(clickedInventory != null && clickedItem != null && clickedInventory.getType() != InventoryType.CREATIVE && inventoryTitle.equals(JLib.format(plugin.getMaterialMenuFile().getString("menu-title")))) {
            event.setCancelled(true);

            ConfigurationSection layoutSection = plugin.getMaterialMenuFile().getConfigurationSection("Layout");

            if(layoutSection != null) {
                for (String itemName : layoutSection.getKeys(false)) {
                    // Getting the rest information of each item such as material/name/lore etc.
                    ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                    if(itemSection != null) {
                        String type = itemSection.getString("type");
                        // Getting the material
                        String materialName = itemSection.getString("material");
                        List<Integer> slots = itemSection.getIntegerList("slots");

                        // In case of the material being invalid or being named as "none" then return without setting an item on that slot
                        if(materialName != null && materialName != "none") {

                            if(slots != null && !slots.isEmpty()) {

                                // Create the actual item
                                ItemStack configItem = new JItem.ItemBuilder(Material.getMaterial(materialName))
                                        .setAmount(1)
                                        .setDisplayName(itemSection.getString("name"))
                                        .setLore(itemSection.getStringList("lore"))
                                        .setCustomModelData(itemSection.getInt("model"))
                                        .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                                        .build();

                                if(clickedItem.equals(configItem)) {
                                    if(type.equals(ItemType.GENERAL_MENU_OPENER.getString())) {

                                        new GeneralMenu().openMenu(Bukkit.getPlayer(event.getView().getPlayer().getName()));

                                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                            Sound buttonSound = Sound.valueOf(plugin.getMaterialMenuFile().getString("button-click-sound"));
                                            player.playSound(player, buttonSound, 1.0f, 1.0f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ConfigurationSection materialSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

            if (materialSection != null) {
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


                            if(clickedItem.equals(matItem)) {
                                if(player.hasPermission(matSection.getString("permission"))) {
                                    DataManager.getOrCreatePlayerData(player).setTrimMaterial(clickedItem.getType());
                                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                        Sound unlockedSound = Sound.valueOf(plugin.getMaterialMenuFile().getString("material-unlocked-sound"));
                                        player.playSound(player, unlockedSound, 1.0f, 1.0f);
                                    }
                                }
                                else {
                                    player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-material")));
                                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                        Sound lockedSound = Sound.valueOf(plugin.getMaterialMenuFile().getString("material-locked-sound"));
                                        player.playSound(player, lockedSound, 1.0f, 1.0f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/
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

    private void handleMaterialItemClick(Player player, ItemStack clickedItem) {
        ConfigurationSection materialSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

        // Check if the materials section exists
        if (materialSection != null) {
            // Get each child of the Materials section
            for (String matName : materialSection.getKeys(false)) {
                // Get the children name
                ConfigurationSection matSection = materialSection.getConfigurationSection(matName);

                // Check if the attributes of the Children exist
                if (JLib.isValidMaterial(matSection)) {
                    handleMaterialClick(player, clickedItem, matSection);
                }
            }
        }
    }

    private void handleMaterialClick(Player player, ItemStack clickedItem, ConfigurationSection matSection) {
        List<String> itemLore = plugin.getMaterialMenuFile().getStringList("materials-lore");
        // Check if the clicked item is the selected material
        boolean isSelected = DataManager.getOrCreatePlayerData(player).getTrimMaterial() == JLib.convertToTrimMaterial(clickedItem.getType());

        for (int i = 0; i < itemLore.size(); i++) {
            String loreLine = itemLore.get(i);

            if (loreLine.contains("%PERMISSION%")) {
                boolean hasPerm = player.hasPermission(matSection.getString("permission"));
                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getMaterialMenuFile().getString("material-unlocked") : plugin.getMaterialMenuFile().getString("material-locked"));
                itemLore.set(i, loreLine);
            }
        }

        ItemStack matItem = JLib.buildMaterialItem(matSection, itemLore, isSelected);

        if (clickedItem.equals(matItem)) {
            if (player.hasPermission(matSection.getString("permission"))) {
                DataManager.getOrCreatePlayerData(player).setTrimMaterial(clickedItem.getType());
                JLib.playSound(player, plugin.getMaterialMenuFile().getString("material-unlocked-sound"));
            } else {
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
