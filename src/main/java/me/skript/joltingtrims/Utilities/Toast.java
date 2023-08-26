package me.skript.joltingtrims.Utilities;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.Utilities.Enums.ToastType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Toast {

    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final ToastType type;
    private JoltingTrims plugin = JoltingTrims.getInstance();

    public static void showTo(Player player, String icon, String message, ToastType type) {
        new Toast(icon, message, type).setup(player);
    }

    private Toast(String icon, String message, ToastType type) {
        this.key = new NamespacedKey(plugin, UUID.randomUUID().toString());
        this.icon = icon;
        this.message = message;
        this.type = type;
    }

    private void setup(Player player) {
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            revokeAdvancement(player);
        }, 5 );
    }

    private void createAdvancement() {
        Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
                "    \"criteria\": {\n" +
                "        \"trigger\": {\n" +
                "            \"trigger\": \"minecraft:impossible\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"display\": {\n" +
                "        \"icon\": {\n" +
                "            \"item\": \"minecraft:" + icon + "\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"text\": \"" + message.replace("|", "\n") + "\"\n" +
                "        },\n" +
                "        \"description\": {\n" +
                "            \"text\": \"\"\n" +
                "        },\n" +
                "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                "        \"frame\": \"" + type.toString().toLowerCase() + "\",\n" +
                "        \"announce_to_chat\": false,\n" +
                "        \"show_toast\": true,\n" +
                "        \"hidden\": true\n" +
                "    },\n" +
                "    \"requirements\": [\n" +
                "        [\n" +
                "            \"trigger\"\n" +
                "        ]\n" +
                "    ]\n" +
                "}");
    }

    private void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
    }

}
