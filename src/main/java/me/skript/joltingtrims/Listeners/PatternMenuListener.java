package me.skript.joltingtrims.Listeners;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.GeneralMenu;
import me.skript.joltingtrims.Utilities.Enums.ItemType;
import me.skript.joltingtrims.Utilities.JLib;
import me.skript.joltingtrims.Utilities.TrimBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

public class PatternMenuListener implements Listener {

    private JoltingTrims plugin;

    public PatternMenuListener(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPatternMenuClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if(!(event.getClickedInventory() != null && clickedItem != null && event.getClickedInventory().getType() != InventoryType.CREATIVE
                && event.getView().getTitle().equals(JLib.format(plugin.getPatternMenuFile().getString("menu-title"))))) {
            return;
        }

        event.setCancelled(true);

        // Handle the Layout Item clicks
        handleLayoutItemClick(player, clickedItem);

        // Handle the Pattern Item clicks
        handlePatternItemClick(player, clickedItem, event);
    }

    private void handleLayoutItemClick(Player player, ItemStack clickedItem) {
        // Get the Layout section
        ConfigurationSection layoutSection = plugin.getPatternMenuFile().getConfigurationSection("Layout");

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
                        if(type.equals(ItemType.GENERAL_MENU_OPENER.getString())) {
                            new GeneralMenu().openMenu(player);
                            JLib.playSound(player, plugin.getPatternMenuFile().getString("button-click-sound"));
                        }
                        else if(type.equals(ItemType.CLEAR_PATTERN.getString())) {
                            TrimBuilder.resetPattern(DataManager.getOrCreatePlayerData(player));
                            JLib.playSound(player, plugin.getPatternMenuFile().getString("clear-pattern-sound"));
                        }
                    }
                }
            }
        }
    }

    private void handlePatternItemClick(Player player, ItemStack clickedItem, InventoryClickEvent event) {
        ConfigurationSection patternSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");

        // Check if the Patterns section exists
        if (patternSection != null) {
            // Get each child of the Patterns section
            for (String patName : patternSection.getKeys(false)) {
                // Get the children name
                ConfigurationSection patSection = patternSection.getConfigurationSection(patName);

                // Check if the attributes of the Children exist
                if (JLib.isValidMaterial(patSection)) {
                    handlePatternClick(player, clickedItem, patSection, event);
                }
            }
        }
    }

    private void handlePatternClick(Player player, ItemStack clickedItem, ConfigurationSection patSection, InventoryClickEvent event) {
        List<String> itemLore = plugin.getPatternMenuFile().getStringList("patterns-lore");
        PlayerData playerData = DataManager.getOrCreatePlayerData(player);

        for (int i = 0; i < itemLore.size(); i++) {
            String loreLine = itemLore.get(i);

            if (loreLine.contains("%PERMISSION%")) {
                boolean hasPerm = player.hasPermission(patSection.getString("permission"));
                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getPatternMenuFile().getString("pattern-unlocked") : plugin.getPatternMenuFile().getString("pattern-locked"));
                itemLore.set(i, loreLine);
            }
        }

        ItemStack patItem = JLib.buildPatternItem(patSection, itemLore);

        if (clickedItem.equals(patItem)) {
            // If player has permission to use that Pattern
            if (player.hasPermission(patSection.getString("permission"))) {

                // Set the previous TrimPattern
                if (playerData.getTrimPattern() != null) {
                    playerData.setPreviousTrimPattern(playerData.getTrimPattern());
                    // Disenchant the previous TrimPattern
                    InventoryView inventoryView = player.getOpenInventory();
                    for (int slot = 0; slot < inventoryView.getTopInventory().getSize(); slot++) {
                        ItemStack itemInSlot = inventoryView.getTopInventory().getItem(slot);
                        if (itemInSlot != null && JLib.convertToTrimPattern(itemInSlot.getType()) == playerData.getPreviousTrimPattern()) {
                            itemInSlot.removeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL);
                        }
                    }
                }

                // Set the new TrimPattern as the pattern that the player clicked
                playerData.setTrimPattern(clickedItem.getType());
                JLib.playSound(player, plugin.getPatternMenuFile().getString("pattern-unlocked-sound"));

                // Add an enchantment to make the item glow
                patItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

                // Update the clicked item in the Pattern Menu with the new glowing item
                int clickedSlot = event.getRawSlot();
                player.getOpenInventory().getTopInventory().setItem(clickedSlot, patItem);
            }
            // If player does not have permission to use that Material
            else {
                player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-material")));
                JLib.playSound(player, plugin.getPatternMenuFile().getString("pattern-locked-sound"));
            }
        }
    }

    /*@EventHandler
    public void onPatternMenuClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();
        String inventoryTitle = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();

        if(clickedInventory != null && clickedItem != null && clickedInventory.getType() != InventoryType.CREATIVE && inventoryTitle.equals(JLib.format(plugin.getPatternMenuFile().getString("menu-title")))) {
            event.setCancelled(true);

            ConfigurationSection layoutSection = plugin.getPatternMenuFile().getConfigurationSection("Layout");

            if(layoutSection != null) {
                for (String itemName : layoutSection.getKeys(false)) {
                    // Getting the rest information of each item such as material/name/lore etc.
                    ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                    if(itemSection != null) {
                        String type = itemSection.getString("type");
                        // Getting the material
                        String patternName = itemSection.getString("material");
                        List<Integer> slots = itemSection.getIntegerList("slots");

                        // In case of the material being invalid or being named as "none" then return without setting an item on that slot
                        if(patternName != null && patternName != "none") {

                            if(slots != null && !slots.isEmpty()) {

                                // Create the actual item
                                ItemStack configItem = new JItem.ItemBuilder(Material.getMaterial(patternName))
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
                                            Sound buttonSound = Sound.valueOf(plugin.getPatternMenuFile().getString("button-click-sound"));
                                            player.playSound(player, buttonSound, 1.0f, 1.0f);
                                        }
                                    }
                                    else if(type.equals(ItemType.CLEAR_PATTERN.getString())) {
                                        TrimBuilder.resetPattern(DataManager.getOrCreatePlayerData(player));

                                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                            Sound clearPatternSound = Sound.valueOf(plugin.getPatternMenuFile().getString("clear-pattern-sound"));
                                            player.playSound(player, clearPatternSound, 1.0f, 1.0f);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ConfigurationSection patternSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");

            if (patternSection != null) {
                for (String patName : patternSection.getKeys(false)) {
                    ConfigurationSection patSection = patternSection.getConfigurationSection(patName);
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


                            if(clickedItem.equals(patItem)) {
                                if(player.hasPermission(patSection.getString("permission"))) {
                                    DataManager.getOrCreatePlayerData(player).setTrimPattern(clickedItem.getType());
                                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                        Sound unlockedSound = Sound.valueOf(plugin.getPatternMenuFile().getString("pattern-unlocked-sound"));
                                        player.playSound(player, unlockedSound, 1.0f, 1.0f);
                                    }
                                }
                                else {
                                    player.sendMessage(JLib.format(plugin.getMessagesFile().getString("no-permission-pattern")));
                                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                                        Sound lockedSound = Sound.valueOf(plugin.getPatternMenuFile().getString("pattern-locked-sound"));
                                        player.playSound(player, lockedSound, 1.0f, 1.0f);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }*/

    @EventHandler
    public void onPatternMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(!event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW)) {
            if(DataManager.getOrCreatePlayerData(player).getEditingItem() != null) {
                player.getInventory().addItem(DataManager.getOrCreatePlayerData(player).getEditingItem());
            }
            DataManager.clearPlayerData(player);
        }
    }
}
