package me.skript.joltingtrims.utilities;

import me.skript.joltingtrims.JoltingTrims;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/*
    Author: Skript
    Date: 31/01/2024
 */


public class JItemBuilder {

    private final JoltingTrims plugin = JoltingTrims.getInstance();
    private final ItemStack item;
    private final ItemMeta meta;
    private final boolean unbreakable = true;

    public JItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public JItemBuilder setPlayerSkull(String playerName) {
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;

            // Set the owning player of the skull
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

            item.setItemMeta(skullMeta);
        }
        return this;
    }

    public JItemBuilder setDisplayName(String displayName) {
        meta.setDisplayName(JUtil.format(displayName));
        return this;
    }

    public JItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public JItemBuilder setAmount(int amount) {
        if(amount <= 0 || amount > 64) {
            item.setAmount(1);
        }
        else {
            item.setAmount(amount);
        }
        return this;
    }

    public JItemBuilder setDurability(short durability) {
        if(durability < 0) {
            item.setDurability((short) 1);
        }
        else {
            item.setDurability(durability);
        }
        return this;
    }

    public JItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public JItemBuilder addItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public JItemBuilder setItemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    public JItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public JItemBuilder setLore(List<String> lore) {
        List<String> newLore = new ArrayList<String>();
        for (String nLore: lore) {
            newLore.add(JUtil.format(nLore));
        }
        meta.setLore(newLore);
        return this;
    }

    public JItemBuilder setLore(String lore) {
        List<String> newLore = new ArrayList<String>();
        newLore.add(JUtil.format(lore));
        meta.setLore(newLore);
        return this;
    }

    public JItemBuilder addLore(String loreLine) {
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(JUtil.format(loreLine));
        meta.setLore(lore);
        return this;
    }

    public JItemBuilder addIntTag(NamespacedKey key, int value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        return this;
    }

    public JItemBuilder addStringTag(NamespacedKey key, String value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        return this;
    }

    public JItemBuilder addIntTag(String key, int value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return addIntTag(namespacedKey, value);
    }

    public JItemBuilder addStringTag(String key, String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return addStringTag(namespacedKey, value);
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}