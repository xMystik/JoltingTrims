package me.skript.joltingtrims.Data.CacheData;

import me.skript.joltingtrims.Utilities.JLib;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class PlayerData {

    private Player player;
    private TrimMaterial material;
    private TrimPattern pattern;
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

    public void setTrimMaterial(Material material) {
        switch (material) {
            case AMETHYST_SHARD:
                this.material = TrimMaterial.AMETHYST;
                break;
            case COPPER_INGOT:
                this.material = TrimMaterial.COPPER;
                break;
            case DIAMOND:
                this.material = TrimMaterial.DIAMOND;
                break;
            case GOLD_INGOT:
                this.material = TrimMaterial.GOLD;
                break;
            case EMERALD:
                this.material = TrimMaterial.EMERALD;
                break;
            case IRON_INGOT:
                this.material = TrimMaterial.IRON;
                break;
            case LAPIS_LAZULI:
                this.material = TrimMaterial.LAPIS;
                break;
            case NETHERITE_INGOT:
                this.material = TrimMaterial.NETHERITE;
                break;
            case QUARTZ:
                this.material = TrimMaterial.QUARTZ;
                break;
            case REDSTONE:
                this.material = TrimMaterial.REDSTONE;
                break;
        }
    }

    public void setTrimMaterial(TrimMaterial material) {
        this.material = material;
    }

    public TrimPattern getTrimPattern() {
        return pattern;
    }

    public void setTrimPattern(TrimPattern pattern) {
        this.pattern = pattern;
    }

    public void setTrimPattern(Material material) {
        this.pattern = JLib.convertToTrimPattern(material);
    }

    public ItemStack getEditingItem() {
        return editingItem;
    }

    public void setEditingItem(ItemStack editingItem) {
        this.editingItem = editingItem;
    }
}
