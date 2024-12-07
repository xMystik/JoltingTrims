package me.skript.joltingtrims.utilities;

import me.skript.joltingtrims.JoltingTrims;
import me.skript.joltingtrims.utilities.enums.ToastType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JToast {

    private final NamespacedKey key;
    private final String icon;
    private final String message;
    private final ToastType type;
    private final JoltingTrims plugin = JoltingTrims.getInstance();

    public static void showTo(Player player, ToastType type, String icon, String message) {
        new JToast(type, icon, message).setup(player);
    }

    private JToast(ToastType type, String icon, String message) {
        this.key = new NamespacedKey(plugin, UUID.randomUUID().toString());
        this.type = type;
        this.icon = icon;
        this.message = message;
    }

    private void setup(Player player) {
        createAdvancement();
        grantAdvancement(player);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            revokeAdvancement(player);
        }, 5 );
    }

    private void createAdvancement() {
        if(plugin.getServer().getVersion().contains("1.21")) {

            String advancementJson = "{\n" +
                    "    \"display\": {\n" +
                    "        \"icon\": {\n" +
                    "            \"id\": \"minecraft:" + icon.toLowerCase() + "\",\n" +
                    "            \"count\": 1\n" +
                    "        },\n" +
                    "        \"title\": {\n" +
                    "            \"text\": \"" + message.replace("|", "\\n") + "\"\n" +
                    "        },\n" +
                    "        \"description\": {\n" +
                    "            \"text\": \"\"\n" +
                    "        },\n" +
                    "        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
                    "        \"frame\": \"" + type.toString().toLowerCase() + "\",\n" +
                    "        \"show_toast\": true,\n" +
                    "        \"announce_to_chat\": false,\n" +
                    "        \"hidden\": true\n" +
                    "    },\n" +
                    "    \"criteria\": {\n" +
                    "        \"trigger\": {\n" +
                    "            \"trigger\": \"minecraft:impossible\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            Bukkit.getUnsafe().loadAdvancement(key, advancementJson);

        } else {
            Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
                    "    \"criteria\": {\n" +
                    "        \"trigger\": {\n" +
                    "            \"trigger\": \"minecraft:impossible\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"display\": {\n" +
                    "        \"icon\": {\n" +
                    "            \"item\": \"minecraft:" + icon.toLowerCase() + "\"\n" +
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
    }

    private void grantAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
    }

    private void revokeAdvancement(Player player) {
        player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
    }

}
