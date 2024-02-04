package me.skript.joltingtrims.data.tempdata;

import me.skript.joltingtrims.utilities.JUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.UUID;

public class PlayerData {

    private final UUID playerUUID;
    private TrimMaterial material;
    private TrimPattern pattern;
    private TrimMaterial previousMaterial;
    private TrimPattern previousPattern;
    private ItemStack editingItem;

    public PlayerData(UUID playerUUID, ItemStack editingItem, TrimMaterial material, TrimPattern pattern) {
        this.playerUUID = playerUUID;
        this.editingItem = editingItem;
        this.material = material;
        this.pattern = pattern;
    }

    public PlayerData(UUID playerUUID, ItemStack editingItem) {
        this.playerUUID = playerUUID;
        this.editingItem = editingItem;
    }

    public PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }


    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public TrimMaterial getTrimMaterial() {
        return material;
    }

    public TrimPattern getTrimPattern() {
        return pattern;
    }

    public TrimMaterial getPreviousTrimMaterial() {
        return previousMaterial;
    }

    public TrimPattern getPreviousTrimPattern() {
        return previousPattern;
    }

    public ItemStack getEditingItem() {
        return editingItem;
    }

    public void setTrimMaterial(Material material) {
        this.previousMaterial = this.material;
        this.material = JUtil.convertToTrimMaterial(material);
    }

    public void setTrimMaterial(TrimMaterial material) {
        this.material = material;
    }

    public void setTrimPattern(TrimPattern pattern) {
        this.pattern = pattern;
    }

    public void setTrimPattern(Material material) {
        this.pattern = JUtil.convertToTrimPattern(material);
    }

    public void setPreviousTrimMaterial(TrimMaterial trimMaterial) {
        this.previousMaterial = trimMaterial;
    }

    public void setPreviousTrimPattern(TrimPattern trimPattern) {
        this.previousPattern = trimPattern;
    }

    public void setEditingItem(ItemStack editingItem) {
        this.editingItem = editingItem;
    }
}
