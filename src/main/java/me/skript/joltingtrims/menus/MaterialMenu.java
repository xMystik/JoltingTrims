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
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.List;

public class MaterialMenu extends JMenu {

    private final JoltingTrims plugin = JoltingTrims.getInstance();
    private final DataManager dataManager = plugin.getDataManager();

    public MaterialMenu(Player owner) {
        super(owner);
    }

    @Override
    public int getSize() {
        if(plugin.getMaterialMenuFile().getInt("menu-size") > 54 || plugin.getMaterialMenuFile().getInt("menu-size") < 9) {
            return 54;
        }
        else {
            return plugin.getMaterialMenuFile().getInt("menu-size");
        }
    }

    @Override
    public String getTitle() {
        return JUtil.format(plugin.getMaterialMenuFile().getString("menu-title"));
    }

    @Override
    public void setupContents() {
        ConfigurationSection layoutSection = plugin.getMaterialMenuFile().getConfigurationSection("Layout");

        JUtil.setupInventoryLayout(layoutSection, getInventory());

        ConfigurationSection materialSection = plugin.getConfigurationFile().getConfigurationSection("Materials");

        if (materialSection != null) {

            PlayerData playerData = dataManager.getOrCreatePlayerData(getOwner());
            TrimMaterial selectedTrimMaterial = playerData.getTrimMaterial();

            for (String matName : materialSection.getKeys(false)) {
                ConfigurationSection matSection = materialSection.getConfigurationSection(matName);
                if (matSection != null) {
                    boolean isEnabled = matSection.getBoolean("enabled", false);
                    if (isEnabled) {

                        List<String> itemLore = plugin.getMaterialMenuFile().getStringList("materials-lore");

                        for(int i = 0; i < itemLore.size(); i++) {
                            String loreLine = itemLore.get(i);

                            if(loreLine.contains("%PERMISSION%")) {
                                boolean hasPerm = getOwner().hasPermission(matSection.getString("permission"));
                                loreLine = loreLine.replace("%PERMISSION%", hasPerm ? plugin.getMaterialMenuFile().getString("material-unlocked") : plugin.getMaterialMenuFile().getString("material-locked"));
                                itemLore.set(i, loreLine);
                            }
                        }

                        ItemStack matItem = new JItemBuilder(Material.getMaterial(matName))
                                .setAmount(1)
                                .setDisplayName(plugin.getMaterialMenuFile().getString("materials-name").replace("%MATERIAL%", JUtil.getDisplayNameOfMaterial(Material.getMaterial(matName))))
                                .setLore(itemLore)
                                .setItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS)
                                .build();

                        // Check if this item's material matches the selected TrimMaterial
                        if (selectedTrimMaterial != null && JUtil.convertToTrimMaterial(Material.getMaterial(matName)) == selectedTrimMaterial) {
                            // Add the glowing enchantment
                            matItem.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                        }

                        // Adds the material item to the GUI
                        getInventory().addItem(matItem);
                    }
                }
            }
        }
    }

    @Override
    public void handleMenuClicks(InventoryClickEvent event) {
    }
}
