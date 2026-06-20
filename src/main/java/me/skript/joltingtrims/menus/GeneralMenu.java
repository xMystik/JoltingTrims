package me.skript.joltingtrims.menus;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltinglib.inventories.JMenu;
import me.skript.joltingtrims.utilities.JUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GeneralMenu extends JMenu {

    private final JoltingTrims plugin = JoltingTrims.getInstance();

    public GeneralMenu(Player owner) {
        super(owner);
    }

    @Override
    public int getSize() {
        if(plugin.getGeneralMenuFile().getInt("menu-size") > 54 || plugin.getGeneralMenuFile().getInt("menu-size") < 9) {
            return 54;
        }
        else {
            return plugin.getGeneralMenuFile().getInt("menu-size");
        }
    }

    @Override
    public String getTitle() {
        return JUtil.format(plugin.getGeneralMenuFile().getString("menu-title", "Undefined"));
    }

    @Override
    public void setupContents() {
        ConfigurationSection layoutSection = plugin.getGeneralMenuFile().getConfigurationSection("Layout");

        JUtil.setupInventoryLayout(layoutSection, getInventory(), getOwner());
    }

    @Override
    public void handleClicks(InventoryClickEvent event) {}
}
