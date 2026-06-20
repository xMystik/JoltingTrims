package me.skript.joltingtrims.utilities;

import me.skript.joltingtrims.data.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltinglib.items.JItemBuilder;
import me.skript.joltinglib.toasts.JToastBuilder;
import me.skript.joltinglib.toasts.JToastType;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JUtil {

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

    public static ItemStack buildMaterialItem(ConfigurationSection matSection, List<String> itemLore) {
        return new JItemBuilder(Material.getMaterial(matSection.getName()))
                .setAmount(1)
                .setDisplayName(JoltingTrims.getInstance().getMaterialMenuFile().getString("materials-name").replace("%MATERIAL%", JUtil.getDisplayNameOfMaterial(Material.getMaterial(matSection.getName()))))
                .setLoreFromStringList(itemLore)
                .addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack buildPatternItem(ConfigurationSection patSection, List<String> itemLore) {
        return new JItemBuilder(Material.getMaterial(patSection.getName() + "_ARMOR_TRIM_SMITHING_TEMPLATE"))
                .setAmount(1)
                .setDisplayName(JoltingTrims.getInstance().getPatternMenuFile().getString("patterns-name").replace("%PATTERN%", JUtil.capitalizeWords(patSection.getName())))
                .setLoreFromStringList(itemLore)
                .addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    public static ItemStack buildItemFromConfigSection(ConfigurationSection itemSection, Player player) {
        PlayerData playerData = JoltingTrims.getInstance().getDataManager().getPlayerData(player);
        List<String> itemLore = itemSection.getStringList("lore");

        itemLore = replacePlaceholders(itemLore, playerData);

        return new JItemBuilder(Material.getMaterial(itemSection.getString("material", "STONE")))
                .setAmount(1)
                .setDisplayName(itemSection.getString("name"))
                .setLoreFromStringList(itemLore)
                .setCustomModelData(itemSection.getInt("model"))
                .addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
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
        return new JItemBuilder(Material.getMaterial(itemSection.getString("material", "STONE")))
                .setAmount(1)
                .setDisplayName(itemSection.getString("name"))
                .setLoreFromStringList(itemSection.getStringList("lore"))
                .setCustomModelData(itemSection.getInt("model"))
                .addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
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
                        if (!slots.isEmpty()) {
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
                        if (!slots.isEmpty()) {
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

    public static boolean isArmorPiece(Material material) {
        return switch (material) {
            case LEATHER_BOOTS,
                 LEATHER_LEGGINGS,
                 LEATHER_CHESTPLATE,
                 LEATHER_HELMET,
                 CHAINMAIL_BOOTS,
                 CHAINMAIL_LEGGINGS,
                 CHAINMAIL_CHESTPLATE,
                 CHAINMAIL_HELMET,
                 IRON_BOOTS,
                 IRON_LEGGINGS,
                 IRON_CHESTPLATE,
                 IRON_HELMET,
                 GOLDEN_BOOTS,
                 GOLDEN_LEGGINGS,
                 GOLDEN_CHESTPLATE,
                 GOLDEN_HELMET,
                 DIAMOND_BOOTS,
                 DIAMOND_LEGGINGS,
                 DIAMOND_CHESTPLATE,
                 DIAMOND_HELMET,
                 NETHERITE_BOOTS,
                 NETHERITE_LEGGINGS,
                 NETHERITE_CHESTPLATE,
                 NETHERITE_HELMET -> true;
            default -> false;
        };
    }

    public static TrimMaterial convertToTrimMaterial(Material material) {
        return switch (material) {
            case AMETHYST_SHARD -> TrimMaterial.AMETHYST;
            case COPPER_INGOT -> TrimMaterial.COPPER;
            case DIAMOND -> TrimMaterial.DIAMOND;
            case GOLD_INGOT -> TrimMaterial.GOLD;
            case EMERALD -> TrimMaterial.EMERALD;
            case IRON_INGOT -> TrimMaterial.IRON;
            case LAPIS_LAZULI -> TrimMaterial.LAPIS;
            case NETHERITE_INGOT -> TrimMaterial.NETHERITE;
            case QUARTZ -> TrimMaterial.QUARTZ;
            case REDSTONE -> TrimMaterial.REDSTONE;
            case RESIN_BRICK -> TrimMaterial.RESIN;
            default -> null;
        };
    }

    public static TrimPattern convertToTrimPattern(Material material) {
        if (isVersionAtLeast("1.21")){
            return switch (material) {
                case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SENTRY;
                case VEX_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.VEX;
                case WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WILD;
                case COAST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.COAST;
                case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.DUNE;
                case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WAYFINDER;
                case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RAISER;
                case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SHAPER;
                case HOST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.HOST;
                case WARD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WARD;
                case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SILENCE;
                case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.TIDE;
                case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SNOUT;
                case RIB_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RIB;
                case EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.EYE;
                case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SPIRE;
                case FLOW_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.FLOW;
                case BOLT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.BOLT;
                default -> null;
            };
        }
        else {
            return switch (material) {
                case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SENTRY;
                case VEX_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.VEX;
                case WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WILD;
                case COAST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.COAST;
                case DUNE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.DUNE;
                case WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WAYFINDER;
                case RAISER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RAISER;
                case SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SHAPER;
                case HOST_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.HOST;
                case WARD_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.WARD;
                case SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SILENCE;
                case TIDE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.TIDE;
                case SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SNOUT;
                case RIB_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.RIB;
                case EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.EYE;
                case SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE -> TrimPattern.SPIRE;
                default -> null;
            };
        }
    }

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
            new JToastBuilder()
                    .setPlayer(player)
                    .setType(JToastType.TASK)
                    .setIcon(Material.SMITHING_TABLE)
                    .setMessage("Successfully finished|the trimming process!")
                    .send();
        }
    }

    public static String getServerVersion() {
        return Bukkit.getBukkitVersion().split("-")[0];
    }

    public static boolean isVersionAtLeast(String targetVersion) {
        String[] current = getServerVersion().split("\\.");
        String[] target = targetVersion.split("\\.");

        for (int i = 0; i < target.length; i++) {
            int currentNum = (i < current.length) ? Integer.parseInt(current[i]) : 0;
            int targetNum = Integer.parseInt(target[i]);

            if (currentNum < targetNum) {
                return false;
            } else if (currentNum > targetNum) {
                return true;
            }
        }
        return true;
    }

}
