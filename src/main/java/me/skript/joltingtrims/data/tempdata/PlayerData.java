package me.skript.joltingtrims.data.tempdata;

import me.skript.joltingtrims.utilities.JUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class PlayerData {

    private Player player;
    private TrimMaterial material;
    private TrimPattern pattern;
    private TrimMaterial previousMaterial;
    private TrimPattern previousPattern;
    private ItemStack editingItem;


    public PlayerData(Player player, ItemStack editingItem, TrimMaterial material, TrimPattern pattern) {
        this.player = player;
        this.editingItem = editingItem;
        this.material = material;
        this.pattern = pattern;
    }

    public PlayerData(Player player, ItemStack editingItem) {
        this.player = player;
        this.editingItem = editingItem;
    }

    public PlayerData(Player player) {
        this.player = player;
    }


    public Player getPlayer() {
        return player;
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
