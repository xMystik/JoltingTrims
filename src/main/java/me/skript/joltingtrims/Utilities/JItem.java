package me.skript.joltingtrims.Utilities;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 * Author: Skript
 * Last Update: 05/01/2023
 *
 */

public class JItem {

    private ItemStack item;
    //REQUIRED PARAMETERS
    private Material material;

    //OPTIONAL PARAMETERS
    private Integer amount = 1;
    private short durability;
    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private Integer customModelData;
    private ItemFlag[] itemFlags = {ItemFlag.HIDE_UNBREAKABLE};
    private String displayName;
    private List<String> lore;
    private Boolean unbreakable = false;

    public JItem(ItemBuilder builder) {
        this.material = builder.material;
        this.amount = builder.amount;
        this.durability = builder.durability;
        this.enchants = builder.enchants;
        this.customModelData = builder.customModelData;
        this.itemFlags = builder.itemFlags;
        this.displayName = builder.displayName;
        this.lore = builder.lore;
        this.unbreakable = builder.unbreakable;
        this.item = builder.item;
    }

    public Material getMaterial() {
        return material;
    }

    public Integer getAmount() {
        return amount;
    }

    public short getDurability() {
        return durability;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return enchants;
    }

    public Integer getCustomModelData() {
        return customModelData;
    }

    public ItemFlag[] getItemFlags() {
        return itemFlags;
    }

    public String getDisplayName() {
        return JLib.format(displayName);
    }

    public List<String> getLore() {
        return lore;
    }

    public Boolean getUnbreakable() {
        return unbreakable;
    }

    public static class ItemBuilder {

        private ItemStack item;
        //----------------------------[REQUIRED PARAMETERS]----------------------------//
        private Material material;

        //----------------------------[OPTIONAL PARAMETERS]----------------------------//
        private Integer amount = 1;
        //private Byte data = 0;
        private short durability;
        private Map<Enchantment, Integer> enchants = new HashMap<>();
        private Integer customModelData;
        private ItemFlag[] itemFlags = {ItemFlag.HIDE_UNBREAKABLE};;
        private String displayName;
        private List<String> lore;
        private Boolean unbreakable = false;
        private ItemMeta meta;

        //----------------------------[MANDATORY CONSTRUCTOR]----------------------------//
        public ItemBuilder(Material material) {
            this.material = material;
        }

        //----------------------------[OPTIONAL SETTERS]----------------------------//
        public ItemBuilder setAmount(Integer amount) {
            if(amount <= 0 || amount > 64) {
                this.amount = 1;
            }
            else {
                this.amount = amount;
            }
            return this;
        }

        public ItemBuilder setDurability(short durability) {
            if(durability < 0) {
                this.durability = 1;
            }
            else {
                this.durability = durability;
            }
            return this;
        }

        public ItemBuilder setEnchants(Map<Enchantment, Integer> enchants) {
            this.enchants = enchants;
            return this;
        }

        public ItemBuilder setEnchants(Enchantment enchant, Integer level) {
            Map<Enchantment, Integer> tempEnchantsList = new HashMap<>();
            tempEnchantsList.put(enchant, level);
            this.enchants = tempEnchantsList;
            return this;
        }

        public ItemBuilder setCustomModelData(Integer customModelData) {
            if(customModelData < 0) {
                this.customModelData = 0;
            }
            else{
                this.customModelData = customModelData;
            }
            return this;
        }

        public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
            ItemFlag[] tempFlags = itemFlags;
            this.itemFlags = tempFlags;
            return this;
        }

        public ItemBuilder setItemFlags(ItemFlag itemFlag) {
            ItemFlag[] tempList = new ItemFlag[1];
            tempList[0] = itemFlag;
            this.itemFlags = tempList;
            return this;
        }

        public ItemBuilder setDisplayName(String displayName) {
            this.displayName = JLib.format(displayName);
            return this;
        }

        public ItemBuilder setLore(List<String> lore) {
            List<String> newLore = new ArrayList<String>();
            for (String nLore: lore) {
                newLore.add(JLib.format(nLore));
            }
            this.lore = newLore;
            return this;
        }

        public ItemBuilder setLore(String lore) {
            List<String> newLore = new ArrayList<String>();
            newLore.add(JLib.format(lore));
            this.lore = newLore;
            return this;
        }

        public ItemBuilder setUnbreakable(Boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        //----------------------------[BUILDER CONSTRUCTOR]----------------------------//
        public ItemStack build() {
            this.item = new ItemStack(this.material, this.amount, this.durability);
            this.item.addEnchantments(this.enchants);
            this.meta = item.getItemMeta();
            this.meta.setCustomModelData(this.customModelData);
            this.meta.addItemFlags(this.itemFlags);
            this.meta.setDisplayName(this.displayName);
            this.meta.setLore(this.lore);
            this.meta.setUnbreakable(this.unbreakable);

            this.item.setItemMeta(this.meta);

            return item;
        }

    }

}
