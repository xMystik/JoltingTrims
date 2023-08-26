package me.skript.joltingtrims;

import me.skript.joltingtrims.Commands.TrimCommand;
import me.skript.joltingtrims.Listeners.GeneralMenuListener;
import me.skript.joltingtrims.Listeners.MaterialMenuListener;
import me.skript.joltingtrims.Listeners.PatternMenuListener;
import me.skript.joltingtrims.Listeners.SmithingTableListener;
import me.skript.joltingtrims.Data.FileData.ConfigFile;
import me.skript.joltingtrims.Data.FileData.FilesManager;
import me.skript.joltingtrims.Data.CacheData.DataManager;
import me.skript.joltingtrims.Utilities.JLib;
import me.skript.joltingtrims.Utilities.TrimBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoltingTrims extends JavaPlugin {

    private FilesManager filesManager = new FilesManager(this);
    private ConfigFile configuration;
    private ConfigFile messages;
    private ConfigFile generalMenu;
    private ConfigFile materialMenu;
    private ConfigFile patternMenu;

    @Override
    public void onEnable() {

        if(!Bukkit.getServer().getVersion().contains("1.20")) {
            Bukkit.getPluginManager().disablePlugin(this);
            Bukkit.getConsoleSender().sendMessage(JLib.format("&3&l[JoltingTrims] &7Plugin was disabled cause of server version."));
        }
        else {

            configuration = filesManager.fileSetup("Configuration");
            messages = filesManager.fileSetup("Messages");

            filesManager.folderSetup("GUI");

            generalMenu = filesManager.fileSetup("GeneralMenu", "GUI");
            materialMenu = filesManager.fileSetup("MaterialMenu", "GUI");
            patternMenu = filesManager.fileSetup("PatternMenu", "GUI");

            new DataManager(this);

            new TrimCommand(this);

            new GeneralMenuListener(this);
            new MaterialMenuListener(this);
            new PatternMenuListener(this);
            new SmithingTableListener(this);
        }
    }

    @Override
    public void onDisable() {
        DataManager.closeAllMenus();
    }

    public static JoltingTrims getInstance() {
        return getPlugin(JoltingTrims.class);
    }

    public FileConfiguration getMessagesFile() {
        return messages.getConfig();
    }

    public void reloadMessagesFile() {
        messages.reloadConfig();
    }

    public FileConfiguration getConfigurationFile() {
        return configuration.getConfig();
    }

    public void reloadConfigurationFile() {
        configuration.reloadConfig();
    }

    public FileConfiguration getGeneralMenuFile() {
        return generalMenu.getConfig();
    }

    public void reloadGeneralMenuFile() {
        generalMenu.reloadConfig();
    }

    public FileConfiguration getMaterialMenuFile() {
        return materialMenu.getConfig();
    }

    public void reloadMaterialMenuFile() {
        materialMenu.reloadConfig();
    }

    public FileConfiguration getPatternMenuFile() {
        return patternMenu.getConfig();
    }

    public void reloadPatternMenuFile() {
        patternMenu.reloadConfig();
    }

}
