package me.skript.joltingtrims.Utilities;

import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Data.CacheData.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimBuilder {

    private static JoltingTrims plugin = JoltingTrims.getInstance();

    public static ItemStack setupItem(PlayerData playerData) {
        // Get the editing item
        ItemStack tempItem = DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem().clone();
        ArmorTrim tempTrim;
        Player player = playerData.getPlayer();

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

                        JUtil.showToast(player);
                        JUtil.playSound(player, applyChangesSuccess);
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                        tempTrim = new ArmorTrim(tempArmorMeta.getTrim().getMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);

                        JUtil.showToast(player);
                        JUtil.playSound(player, applyChangesSuccess);
                    }
                    else if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() != null) {
                        tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);

                        JUtil.showToast(player);
                        JUtil.playSound(player, applyChangesSuccess);
                    }
                    else {
                        playerData.getPlayer().sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-mat-or-pat")));

                        JUtil.playSound(player, applyChangesFailure);
                    }
                    tempItem.setItemMeta(tempArmorMeta);
                }
                else {
                    if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() == null) {
                        playerData.getPlayer().sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-pattern")));

                        JUtil.playSound(player, applyChangesFailure);
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                        playerData.getPlayer().sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-material")));

                        JUtil.playSound(player, applyChangesFailure);
                    }
                    else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() == null) {
                        playerData.getPlayer().sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-mat-and-pat")));

                        JUtil.playSound(player, applyChangesFailure);
                    }
                    else {
                        tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                        tempArmorMeta.setTrim(tempTrim);
                        tempItem.setItemMeta(tempArmorMeta);

                        JUtil.showToast(player);
                        JUtil.playSound(player, applyChangesSuccess);
                    }
                }
            }
            else {
                if(playerData.getTrimMaterial() != null && playerData.getTrimPattern() == null) {
                    player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-pattern")));

                    JUtil.playSound(player, applyChangesFailure);
                }
                else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() != null) {
                    player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-material")));

                    JUtil.playSound(player, applyChangesFailure);
                }
                else if(playerData.getTrimMaterial() == null && playerData.getTrimPattern() == null) {
                    player.sendMessage(JUtil.format(plugin.getMessagesFile().getString("select-mat-and-pat")));

                    JUtil.playSound(player, applyChangesFailure);
                }
                else {
                    tempTrim = new ArmorTrim(playerData.getTrimMaterial(), playerData.getTrimPattern());
                    tempArmorMeta.setTrim(tempTrim);
                    tempItem.setItemMeta(tempArmorMeta);

                    JUtil.showToast(player);
                    JUtil.playSound(player, applyChangesSuccess);
                }
            }
        }

        DataManager.clearPlayerData(player);
        player.closeInventory();
        return tempItem;
    }

    public static void resetPattern(PlayerData playerData) {
        if(DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem() != null) {
            ItemStack tempItem = DataManager.getOrCreatePlayerData(playerData.getPlayer()).getEditingItem().clone();

            if(playerData.getEditingItem() != null) {

                ArmorMeta tempArmorMeta = (ArmorMeta) tempItem.getItemMeta();
                tempArmorMeta.setTrim(null);
                tempItem.setItemMeta(tempArmorMeta);

                playerData.setTrimPattern((TrimPattern) null);
                playerData.setEditingItem(tempItem);
            }
        }
    }

}
