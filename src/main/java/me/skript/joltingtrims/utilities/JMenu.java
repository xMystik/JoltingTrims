package me.skript.joltingtrims.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class JMenu implements InventoryHolder {

    protected UUID owner;
    protected Inventory inventory;
    protected String title;
    protected int size;

    public JMenu(Player owner) {
        this.owner = owner.getUniqueId();
    }

    public abstract int getSize();
    public abstract String getTitle();
    public abstract void setupContents();
    public abstract void handleMenuClicks(InventoryClickEvent event);

    public void cancelAllClicks(InventoryClickEvent event) {
        if(event.getView().getTopInventory().getHolder() instanceof JMenu && event.getView().getBottomInventory().getHolder() instanceof Player) {
            event.setCancelled(true);
        }
    }

    public void openMenu() {
        inventory = Bukkit.createInventory(this, getSize(), getTitle());

        setupContents();

        Bukkit.getPlayer(owner).openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }
}
