package me.skript.joltingtrims.menus;

import me.skript.joltingtrims.data.tempdata.DataManager;
import me.skript.joltingtrims.data.tempdata.PlayerData;
import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.utilities.JItemBuilder;
import me.skript.joltingtrims.utilities.JMenu;
import me.skript.joltingtrims.utilities.JUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.List;

public class PatternMenu extends JMenu {

    private final JoltingTrims plugin = JoltingTrims.getInstance();
    private final DataManager dataManager = plugin.getDataManager();

    public PatternMenu(Player owner) {
        super(owner);
    }

    @Override
    public int getSize() {
        if(plugin.getPatternMenuFile().getInt("menu-size") > 54 || plugin.getPatternMenuFile().getInt("menu-size") < 9) {
            return 54;
        }
        else {
            return plugin.getPatternMenuFile().getInt("menu-size");
        }
    }

    @Override
    public String getTitle() {
        return JUtil.format(plugin.getPatternMenuFile().getString("menu-title"));
    }

    @Override
    public void setupContents() {
        ConfigurationSection layoutSection = plugin.getPatternMenuFile().getConfigurationSection("Layout");

        JUtil.setupInventoryLayout(layoutSection, getInventory(), getOwner());

        ConfigurationSection patternsSection = plugin.getConfigurationFile().getConfigurationSection("Patterns");

        if (patternsSection != null) {

            PlayerData playerData = dataManager.getOrCreatePlayerData(getOwner());
            TrimPattern selectedTrimPattern = playerData.getTrimPattern();

            for (String patName : patternsSection.getKeys(false)) {
                ConfigurationSection patSection = patternsSection.getConfigurationSection(patName);
                if (patSection != null) {
                    boolean isEnabled = patSection.getBoolean("enabled", false);

                    if (isEnabled) {

                        List<String> itemLore = plugin.getPatternMenuFile().getStringList("patterns-lore");

                        for(int i = 0; i < itemLore.size(); i++) {
                            String loreLine = itemLore.get(i);

                            if(loreLine.contains("%PERMISSION%")) {
                                boolean hasPerm = getOwner().hasPermission(patSection.getString("permission"));
                                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getPatternMenuFile().getString("pattern-unlocked") : plugin.getPatternMenuFile().getString("pattern-locked"));
                                itemLore.set(i, loreLine);
                            }
                        }

                        ItemStack patItem = new JItemBuilder(Material.getMaterial(patName + "_ARMOR_TRIM_SMITHING_TEMPLATE"))
                                .setAmount(1)
                                .setDisplayName(plugin.getPatternMenuFile().getString("patterns-name").replace("%PATTERN%", JUtil.capitalizeWords(patName)))
                                .setLore(itemLore)
                                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                                .build();

                        // Check if this pattern matches the selected TrimPattern
                        if (selectedTrimPattern != null && JUtil.convertToTrimPattern(Material.getMaterial(patName + "_ARMOR_TRIM_SMITHING_TEMPLATE")) == selectedTrimPattern) {
                            // Add the glowing enchantment
                            patItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        }

                        // Add the trim item to the GUI
                        getInventory().addItem(patItem);
                    }
                }
            }
        }
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {

    }
}
