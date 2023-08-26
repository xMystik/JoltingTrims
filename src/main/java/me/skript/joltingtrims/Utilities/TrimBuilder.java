package me.skript.joltingtrims.Utilities;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.Enums.ToastType;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

public class TrimBuilder {

    private static JoltingTrims plugin = JoltingTrims.getInstance();

    public static ItemStack setupItem(PlayerData playerData) {
        // Get the editing item
        ItemStack tempItem = DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem().clone();
        ArmorTrim tempTrim;

        Sound applyChangesSuccess = Sound.valueOf(plugin.getGeneralMenuFile().getString("apply-changes-success-sound"));
        Sound applyChangesFailure = Sound.valueOf(plugin.getGeneralMenuFile().getString("apply-changes-failure-sound"));

        // Check if the item exists, has an item meta and its meta is an armor meta
        if(playerData.getEditingItem() != null) {

            ArmorMeta tempArmorMeta = (ArmorMeta) tempItem.getItemMeta();

            // Checks if the item has meta
            if(tempItem.hasItemMeta()) {
                if(tempArmorMeta != null && tempArmorMeta.hasTrim()) {

                    // Check if either the pattern or the material are not null
                    if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() == null) {
                        tempTrim = new ArmorTrim(playerData.getTrimMaterial(), tempArmorMeta.getTrim().getPattern());
                        tempArmorMeta.setTrim(tempTrim);

                        if(plugin.getConfigurationFile().getBoolean("toast-enabled")) {
                            Toast.showTo(playerData.getPlayer(), "smithing_table", "Successfully finished|the trimming process!", ToastType.GOAL);
                        }

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesSuccess,1.0f, 1.0f);
                        }
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                        tempTrim = new ArmorTrim(tempArmorMeta.getTrim().getMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);

                        if(plugin.getConfigurationFile().getBoolean("toast-enabled")) {
                            Toast.showTo(playerData.getPlayer(), "smithing_table", "Successfully finished|the trimming process!", ToastType.GOAL);
                        }

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesSuccess,1.0f, 1.0f);
                        }
                    }
                    else if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() != null) {
                        tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);

                        if(plugin.getConfigurationFile().getBoolean("toast-enabled")) {
                            Toast.showTo(playerData.getPlayer(), "smithing_table", "Successfully finished|the trimming process!", ToastType.GOAL);
                        }

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesSuccess,1.0f, 1.0f);
                        }
                    }
                    else {
                        playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-mat-or-pat")));

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                        }
                    }
                    tempItem.setItemMeta(tempArmorMeta);
                }
                else {
                    if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() == null) {
                        playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-pattern")));

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                        }
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                        playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-material")));

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                        }
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() == null) {
                        playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-mat-and-pat")));

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                        }
                    }
                    else {
                        tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);
                        tempItem.setItemMeta(tempArmorMeta);

                        if(plugin.getConfigurationFile().getBoolean("toast-enabled")) {
                            Toast.showTo(playerData.getPlayer(), "smithing_table", "Successfully finished|the trimming process!", ToastType.GOAL);
                        }

                        if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                            playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesSuccess,1.0f, 1.0f);
                        }
                    }
                }
            }
            else {
                if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() == null) {
                    playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-pattern")));

                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                        playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                    }
                }
                else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                    playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-material")));

                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                        playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                    }
                }
                else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() == null) {
                    playerData.getPlayer().sendMessage(JLib.format(plugin.getMessagesFile().getString("select-mat-and-pat")));

                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                        playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesFailure,1.0f, 1.0f);
                    }
                }
                else {
                    tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                    tempArmorMeta.setTrim(tempTrim);
                    tempItem.setItemMeta(tempArmorMeta);

                    if(plugin.getConfigurationFile().getBoolean("toast-enabled")) {
                        Toast.showTo(playerData.getPlayer(), "smithing_table", "Successfully finished|the trimming process!", ToastType.GOAL);
                    }

                    if(plugin.getConfigurationFile().getBoolean("sounds-enabled")) {
                        playerData.getPlayer().playSound(playerData.getPlayer(), applyChangesSuccess,1.0f, 1.0f);
                    }
                }
            }
        }

        DataManager.clearPlayerData(playerData.getPlayer());
        playerData.getPlayer().closeInventory();
        return tempItem;
    }

    public static void resetPattern(PlayerData playerData) {
        if(DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem() != null) {
            ItemStack tempItem = DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem().clone();

            if(playerData.getEditingItem() != null) {

                ArmorMeta tempArmorMeta = (ArmorMeta) tempItem.getItemMeta();
                tempArmorMeta.setTrim(null);
                tempItem.setItemMeta(tempArmorMeta);

                playerData.setEditingItem(tempItem);
            }
        }
    }

}
