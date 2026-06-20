package me.skript.joltingtrims.listeners;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.menus.GeneralMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MenuBlockListener implements Listener {

    private final JoltingTrims plugin;

    public MenuBlockListener(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onMenuBlockRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Action action = event.getAction();

        String configuredMaterial = plugin.getConfigurationFile().getString("menu-block-material");
        if(configuredMaterial == null || configuredMaterial.isBlank()) {
            return;
        }

        Material menuBlockMaterial = Material.matchMaterial(configuredMaterial);
        if(menuBlockMaterial == null || !menuBlockMaterial.isBlock()) {
            plugin.getLogger().warning("Invalid menu-block-material in Configuration.yml. Please use a valid block material.");
            return;
        }

        if(clickedBlock != null && clickedBlock.getType() == menuBlockMaterial && action == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            new GeneralMenu(player).openMenu();
        }
    }

}
