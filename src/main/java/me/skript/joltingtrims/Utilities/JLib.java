package me.skript.joltingtrims.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JLib {

    public static String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String formatHex(String text) {
        if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18") || Bukkit.getVersion().contains("1.19") || Bukkit.getVersion().contains("1.20")) {
            final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

            Matcher match = pattern.matcher(text);

            while(match.find()) {
                String color = text.substring(match.start(), match.end());
                text = text.replace(color, ChatColor.valueOf(color) + "");
                match = pattern.matcher(text);
            }

        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private static ItemStack buildItemFromSection(ConfigurationSection itemSection) {
        return new JItem.ItemBuilder(Material.getMaterial(itemSection.getString("material")))
                .setAmount(1)
                .setDisplayName(itemSection.getString("name"))
                .setLore(itemSection.getStringList("lore"))
                .setCustomModelData(itemSection.getInt("model"))
                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static void setupInventoryLayout(ConfigurationSection layoutSection, Inventory inventory) {
        if (layoutSection != null) {
            for (String itemName : layoutSection.getKeys(false)) {
                ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                if (itemSection != null) {
                    String materialName = itemSection.getString("material");
                    List<Integer> slots = itemSection.getIntegerList("slots");

                    if (materialName != null && !materialName.equals("none")) {
                        if (slots != null && !slots.isEmpty()) {
                            ItemStack guiItem = buildItemFromSection(itemSection);

                            for (int slot : slots) {
                                inventory.setItem(slot, guiItem);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isArmorPiece(Material material) {
        switch (material) {
            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case CHAINMAIL_BOOTS:
            case CHAINMAIL_LEGGINGS:
            case CHAINMAIL_CHESTPLATE:
            case CHAINMAIL_HELMET:
            case IRON_BOOTS:
            case IRON_LEGGINGS:
            case IRON_CHESTPLATE:
            case IRON_HELMET:
            case GOLDEN_BOOTS:
            case GOLDEN_LEGGINGS:
            case GOLDEN_CHESTPLATE:
            case GOLDEN_HELMET:
            case DIAMOND_BOOTS:
            case DIAMOND_LEGGINGS:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_HELMET:
            case NETHERITE_BOOTS:
            case NETHERITE_LEGGINGS:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_HELMET:
                return true;
            default:
                return false;
        }
    }

    public static String getDisplayNameOfMaterial(Material material) {
        String name = material.name().toLowerCase(); // Convert to lowercase
        name = name.replace("_", " "); // Replace underscores with spaces
        name = capitalizeWords(name); // Capitalize each word

        return name;
    }

    // Helper method to capitalize each word
    public static String capitalizeWords(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
            } else {
                c = Character.toLowerCase(c);
            }

            result.append(c);
        }

        return result.toString();
    }

}
