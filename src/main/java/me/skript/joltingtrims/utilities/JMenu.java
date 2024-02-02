package me.skript.joltingtrims.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class JMenu implements InventoryHolder {

    protected Player owner;
    protected Inventory inventory;
    protected String title;
    protected int size;

    public JMenu(Player owner) {
        this.owner = owner;
    }

    public abstract int getSize();
    public abstract String getTitle();
    public abstract void setupLayout();
    public abstract void setupContents();
    public abstract void handleClicks(InventoryClickEvent event);

    public void openMenu() {
        inventory = Bukkit.createInventory(this, getSize(), getTitle());
        setupLayout();
        setupContents();

        owner.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Player getOwner() {
        return owner;
    }
}
