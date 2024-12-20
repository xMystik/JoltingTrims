package me.skript.joltingtrims.utilities;

import me.skript.joltingtrims.data.tempdata.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.utilities.enums.ToastType;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JUtil {

    //------------------------------------------------------------------------------------\\
    public static String format(String message) {
        Pattern pattern = Pattern.compile("(&#[a-fA-F0-9]{6})");
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            // Entire matched pattern including &#
            String colorCode = matcher.group(1);
            // Extract just the hex code, excluding &#
            String hexCode = colorCode.substring(2);
            StringBuilder hexBuilder = new StringBuilder("&x");
            for (char c : hexCode.toCharArray()) {
                hexBuilder.append("&").append(c);
            }
            // Replace the entire matched pattern
            message = message.replace(colorCode, hexBuilder.toString());
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }
    //------------------------------------------------------------------------------------\\



    //------------------------------------------------------------------------------------\\
    public static ItemStack buildMaterialItem(ConfigurationSection matSection, List<String> itemLore) {
        ItemStack matItem = new JItemBuilder(Material.getMaterial(matSection.getName()))
                .setAmount(1)
                .setDisplayName(JoltingTrims.getInstance().getMaterialMenuFile().getString("materials-name").replace("%MATERIAL%", JUtil.getDisplayNameOfMaterial(Material.getMaterial(matSection.getName()))))
                .setLore(itemLore)
                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();

        return matItem;
    }

    public static ItemStack buildPatternItem(ConfigurationSection patSection, List<String> itemLore) {
        ItemStack patItem = new JItemBuilder(Material.getMaterial(patSection.getName() + "_ARMOR_TRIM_SMITHING_TEMPLATE"))
                .setAmount(1)
                .setDisplayName(JoltingTrims.getInstance().getPatternMenuFile().getString("patterns-name").replace("%PATTERN%", JUtil.capitalizeWords(patSection.getName())))
                .setLore(itemLore)
                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();

        return patItem;
    }

    public static ItemStack buildItemFromConfigSection(ConfigurationSection itemSection, Player player) {
        PlayerData playerData = JoltingTrims.getInstance().getDataManager().getOrCreatePlayerData(player);
        List<String> itemLore = itemSection.getStringList("lore");

        itemLore = replacePlaceholders(itemLore, playerData);

        return new JItemBuilder(Material.getMaterial(itemSection.getString("material")))
                .setAmount(1)
                .setDisplayName(itemSection.getString("name"))
                .setLore(itemLore)
                .setCustomModelData(itemSection.getInt("model"))
                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static List<String> replacePlaceholders(List<String> itemLore, PlayerData playerData) {
        List<String> modifiedLore = new ArrayList<>();

        for (String line : itemLore) {
            if(line.contains("%UNLOCKED_MATERIALS%")) {
                line = line.replace("%UNLOCKED_MATERIALS%", String.valueOf(JoltingTrims.getInstance().getDataManager().getUnlockedTrimMaterials(playerData.getPlayer())));
            }
            if(line.contains("%UNLOCKED_PATTERNS%")) {
                line = line.replace("%UNLOCKED_PATTERNS%", String.valueOf(JoltingTrims.getInstance().getDataManager().getUnlockedTrimPatterns(playerData.getPlayer())));
            }
            if(line.contains("%SELECTED_MATERIAL%")) {
                //line = line.replace("%SELECTED_MATERIAL%", playerData.getTrimMaterial() != null ? capitalizeWords(playerData.getTrimMaterial().getKey().getKey()) : "None");
                line = line.replace("%SELECTED_MATERIAL%", playerData.getTrimMaterial() != null ? capitalizeWords(Registry.TRIM_MATERIAL.getKey(playerData.getTrimMaterial()).getKey()) : "None");
            }
            if(line.contains("%SELECTED_PATTERN%")) {
                //line = line.replace("%SELECTED_PATTERN%", playerData.getTrimPattern() != null ? capitalizeWords(playerData.getTrimPattern().getKey().getKey()) : "None");
                line = line.replace("%SELECTED_PATTERN%", playerData.getTrimPattern() != null ? capitalizeWords(Registry.TRIM_PATTERN.getKey(playerData.getTrimPattern()).getKey()) : "None");
            }
            if(line.contains("%MAX_MATERIALS%")) {
                line = line.replace("%MAX_MATERIALS%", String.valueOf(JoltingTrims.getInstance().getDataManager().getMaxTrimMaterials()));
            }
            if(line.contains("%MAX_PATTERNS%")) {
                line = line.replace("%MAX_PATTERNS%", String.valueOf(JoltingTrims.getInstance().getDataManager().getMaxTrimPatterns()));
            }
            modifiedLore.add(line);
        }

        return modifiedLore;
    }

    public static ItemStack buildItemFromConfigSection(ConfigurationSection itemSection) {
        return new JItemBuilder(Material.getMaterial(itemSection.getString("material")))
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
                            ItemStack guiItem = buildItemFromConfigSection(itemSection);

                            for (int slot : slots) {
                                inventory.setItem(slot, guiItem);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void setupInventoryLayout(ConfigurationSection layoutSection, Inventory inventory, Player player) {
        if (layoutSection != null) {
            for (String itemName : layoutSection.getKeys(false)) {
                ConfigurationSection itemSection = layoutSection.getConfigurationSection(itemName);

                if (itemSection != null) {
                    String materialName = itemSection.getString("material");
                    List<Integer> slots = itemSection.getIntegerList("slots");

                    if (materialName != null && !materialName.equals("none")) {
                        if (slots != null && !slots.isEmpty()) {
                            ItemStack guiItem = buildItemFromConfigSection(itemSection, player);

                            for (int slot : slots) {
                                inventory.setItem(slot, guiItem);
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean isConfigItem(ItemStack item, ConfigurationSection itemSection) {
        ItemStack configItem = JUtil.buildItemFromConfigSection(itemSection);
        return item != null && item.equals(configItem);
    }

    public static boolean isValidItemSection(String materialName, List<Integer> slots) {
        return materialName != null
                && !materialName.equalsIgnoreCase("none")
                && slots != null
                && !slots.isEmpty();
    }

    public static boolean isValidMaterial(ConfigurationSection matSection) {
        return matSection != null && matSection.getBoolean("enabled", false);
    }
    //------------------------------------------------------------------------------------\\



    //------------------------------------------------------------------------------------\\
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

    public static TrimMaterial convertToTrimMaterial(Material material) {
        TrimMaterial trimMaterial = null;

        switch (material) {
            case AMETHYST_SHARD:
                trimMaterial = TrimMaterial.AMETHYST;
                break;
            case COPPER_INGOT:
                trimMaterial = TrimMaterial.COPPER;
                break;
            case DIAMOND:
                trimMaterial = TrimMaterial.DIAMOND;
                break;
            case GOLD_INGOT:
                trimMaterial = TrimMaterial.GOLD;
                break;
            case EMERALD:
                trimMaterial = TrimMaterial.EMERALD;
                break;
            case IRON_INGOT:
                trimMaterial = TrimMaterial.IRON;
                break;
            case LAPIS_LAZULI:
                trimMaterial = TrimMaterial.LAPIS;
                break;
            case NETHERITE_INGOT:
                trimMaterial = TrimMaterial.NETHERITE;
                break;
            case QUARTZ:
                trimMaterial = TrimMaterial.QUARTZ;
                break;
            case REDSTONE:
                trimMaterial = TrimMaterial.REDSTONE;
                break;
        }
        return trimMaterial;
    }

    public static TrimPattern convertToTrimPattern(Material material) {
        TrimPattern pattern = null;
        switch (material) {
            case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.SENTRY;
                break;
            case VEX_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.VEX;
                break;
            case WILD_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.WILD;
                break;
            case COAST_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.COAST;
                break;
            case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.DUNE;
                break;
            case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.WAYFINDER;
                break;
            case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.RAISER;
                break;
            case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.SHAPER;
                break;
            case HOST_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.HOST;
                break;
            case WARD_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.WARD;
                break;
            case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.SILENCE;
                break;
            case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.TIDE;
                break;
            case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.SNOUT;
                break;
            case RIB_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.RIB;
                break;
            case EYE_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.EYE;
                break;
            case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE:
                pattern = TrimPattern.SPIRE;
                break;
        }
        return pattern;
    }
    //------------------------------------------------------------------------------------\\



    //------------------------------------------------------------------------------------\\
    public static String getDisplayNameOfMaterial(Material material) {
        String name = material.name().toLowerCase();
        name = name.replace("_", " ");
        name = capitalizeWords(name);

        return name;
    }

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
    //------------------------------------------------------------------------------------\\



    //------------------------------------------------------------------------------------\\
    public static void playSound(Player player, FileConfiguration file, String soundVariable) {
        if(JoltingTrims.getInstance().getConfigurationFile().getBoolean("sounds-enabled")) {
            Sound sound = Sound.valueOf(file.getString(soundVariable));

            player.playSound(player, sound, 1.0f, 1.0f);
        }
    }

    public static void playSound(Player player, String soundVariable) {
        if(JoltingTrims.getInstance().getConfigurationFile().getBoolean("sounds-enabled")) {
            Sound sound = Sound.valueOf(soundVariable);

            player.playSound(player, sound, 1.0f, 1.0f);
        }
    }

    public static void playSound(Player player, Sound sound) {
        if(JoltingTrims.getInstance().getConfigurationFile().getBoolean("sounds-enabled")) {
            player.playSound(player, sound, 1.0f, 1.0f);
        }
    }

    public static void playSound(Player player, Sound sound, Float volume, Float pitch) {
        if(JoltingTrims.getInstance().getConfigurationFile().getBoolean("sounds-enabled")) {
            player.playSound(player, sound, volume, pitch);
        }
    }

    public static void showToast(Player player) {
        if(JoltingTrims.getInstance().getConfigurationFile().getBoolean("toast-enabled")) {
            JToast.showTo(player, ToastType.TASK, "smithing_table", "Successfully finished|the trimming process!");
        }
    }
    //------------------------------------------------------------------------------------\\

    public static void removeEnchantmentGlow(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.removeEnchant(Enchantment.PROTECTION_ENVIRONMENTAL); // Change the enchantment type if needed
        item.setItemMeta(itemMeta);
    }

}
