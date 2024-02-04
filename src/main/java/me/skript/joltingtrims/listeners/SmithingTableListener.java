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

public class SmithingTableListener implements Listener {

    private final JoltingTrims plugin;

    public SmithingTableListener(JoltingTrims plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSmithingRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        Action action = event.getAction();

        if(plugin.getConfigurationFile().getBoolean("replace-smithing-table")) {
            if(clickedBlock != null && clickedBlock.getType() == Material.SMITHING_TABLE && action == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                new GeneralMenu(player).openMenu();
            }
        }
    }

}
