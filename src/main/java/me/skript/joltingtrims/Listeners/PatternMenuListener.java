package me.skript.joltingtrims.Listeners;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Menus.GeneralMenu;
import me.skript.joltingtrims.Utilities.Enums.ItemType;
import me.skript.joltingtrims.Utilities.JItem;
import me.skript.joltingtrims.Utilities.JLib;
import me.skript.joltingtrims.Utilities.TrimBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
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
                                        //TODO - ADD SOUND
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
    }

    @EventHandler
    public void onPatternMenuClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        if(!event.getReason().equals(InventoryCloseEvent.Reason.OPEN_NEW) && DataManager.getOrCreatePlayerData(player).getEditingItem() != null) {
            player.getInventory().addItem(DataManager.getOrCreatePlayerData(player).getEditingItem());
            DataManager.clearPlayerData(player);
        }
    }
}
