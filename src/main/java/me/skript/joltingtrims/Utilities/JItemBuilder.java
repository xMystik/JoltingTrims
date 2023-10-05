package me.skript.joltingtrims.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*
    Author: Skript
    Date: 28/09/2023
 */


public class JItemBuilder {

    private ItemStack item;
    private ItemMeta meta;
    private boolean unbreakable = true;

    public JItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public JItemBuilder setPlayerSkullTexture(String base64Texture) {
        if (meta instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(UUID.randomUUID().toString());
            setSkullTexture(skullMeta, base64Texture);
        }
        return this;
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

    private void setSkullTexture(SkullMeta meta, String texture) {
        try {
            Class<?> profileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Method getProperties = profileClass.getDeclaredMethod("getProperties");

            Object gameProfile = getProperties.invoke(meta);

            List<Object> properties = (List<Object>) getProperties.invoke(gameProfile);
            for (Object property : properties) {
                Method getName = propertyClass.getDeclaredMethod("getName");
                String name = (String) getName.invoke(property);
                if (name.equalsIgnoreCase("textures")) {
                    Method getValue = propertyClass.getDeclaredMethod("getValue");
                    getValue.setAccessible(true);
                    Object value = getValue.invoke(property);

                    Method put = propertyClass.getDeclaredMethod("put", Object.class, Object.class);
                    put.invoke(property, "textures", value);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}