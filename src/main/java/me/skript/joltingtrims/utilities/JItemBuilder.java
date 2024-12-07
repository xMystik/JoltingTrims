package me.skript.joltingtrims.utilities;

import me.skript.joltingtrims.JoltingTrims;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/*
    Author: Skript
    Date: 07/12/2024
 */


public class JItemBuilder {

    private final JoltingTrims plugin = JoltingTrims.getInstance();
    private final ItemStack item;
    private final ItemMeta meta;
    private final boolean supportsComponents;

    public JItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
        this.supportsComponents = isComponentSupported();
    }

    private boolean isComponentSupported() {
        try {
            ItemMeta.class.getMethod("displayName", Component.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public JItemBuilder setDisplayName(String displayName) {
        if(supportsComponents) {
            Component displayNameComponent = LegacyComponentSerializer
                    .legacyAmpersand()
                    .deserialize(displayName)
                    .decoration(TextDecoration.ITALIC, false);
            meta.displayName(displayNameComponent);
        } else {
            meta.setDisplayName(JUtil.format(displayName));
        }
        return this;
    }

    public JItemBuilder setLore(List<String> lore) {
        if(supportsComponents) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(LegacyComponentSerializer
                        .legacyAmpersand()
                        .deserialize(line)
                        .decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(loreComponents);
        }
        else {
            List<String> formattedLore = new ArrayList<>();
            for (String nLore: lore) {
                formattedLore.add(JUtil.format(nLore));
            }
            meta.setLore(formattedLore);
        }
        return this;
    }

    public JItemBuilder setLore(String lore) {
        return setLore(List.of(lore));
    }

    public JItemBuilder addLore(String loreLine) {
        if (supportsComponents) {
            List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
            lore.add(LegacyComponentSerializer
                    .legacyAmpersand()
                    .deserialize(loreLine)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
        } else {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
            lore.add(JUtil.format(loreLine));
            meta.setLore(lore);
        }
        return this;
    }

    public JItemBuilder setPlayerSkull(String playerName) {
        if (meta instanceof SkullMeta skullMeta) {

            // Set the owning player of the skull
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

            item.setItemMeta(skullMeta);
        }
        return this;
    }

    public JItemBuilder setCustomModelData(int customModelData) {
        meta.setCustomModelData(customModelData);
        return this;
    }

    public JItemBuilder setAmount(int amount) {
        item.setAmount(Math.max(1, Math.min(amount, 64)));
        return this;
    }

    public JItemBuilder setDurability(int durability) {
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(Math.max(0, durability));
            item.setItemMeta(damageable);
        }
        return this;
    }

    public JItemBuilder addEnchant(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
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

    public JItemBuilder addIntTag(NamespacedKey key, int value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
        return this;
    }

    public JItemBuilder addStringTag(NamespacedKey key, String value) {
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
        return this;
    }

    public JItemBuilder addIntTag(String key, int value) {
        return addIntTag(new NamespacedKey(plugin, key), value);
    }

    public JItemBuilder addStringTag(String key, String value) {
        return addStringTag(new NamespacedKey(plugin, key), value);
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}