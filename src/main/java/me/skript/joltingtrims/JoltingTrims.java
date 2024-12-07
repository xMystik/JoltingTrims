package me.skript.joltingtrims;

import me.skript.joltingtrims.commands.TrimCommand;
import me.skript.joltingtrims.listeners.GeneralMenuListener;
import me.skript.joltingtrims.listeners.MaterialMenuListener;
import me.skript.joltingtrims.listeners.PatternMenuListener;
import me.skript.joltingtrims.listeners.SmithingTableListener;
import me.skript.joltingtrims.data.filedata.ConfigFile;
import me.skript.joltingtrims.data.filedata.FilesManager;
import me.skript.joltingtrims.data.tempdata.DataManager;
import me.skript.joltingtrims.utilities.JUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoltingTrims extends JavaPlugin {

    private FilesManager filesManager = new FilesManager(this);
    private DataManager dataManager;
    private ConfigFile configuration;
    private ConfigFile messages;
    private ConfigFile generalMenu;
    private ConfigFile materialMenu;
    private ConfigFile patternMenu;
    private static final String REQUIRED_VERSION = "1.1";

    @Override
    public void onEnable() {

        if(Bukkit.getServer().getVersion().contains(REQUIRED_VERSION)) {
            Bukkit.getConsoleSender().sendMessage(JUtil.format("&4&l[JoltingTrims] &7Server version is not supported."));
            Bukkit.getPluginManager().disablePlugin(this);
        }

        dataManager = new DataManager(this);

        configuration = filesManager.fileSetup("Configuration");
        messages = filesManager.fileSetup("Messages");

        filesManager.folderSetup("GUI");

        generalMenu = filesManager.fileSetup("GeneralMenu", "GUI");
        materialMenu = filesManager.fileSetup("MaterialMenu", "GUI");
        patternMenu = filesManager.fileSetup("PatternMenu", "GUI");


        new TrimCommand(this);

        new GeneralMenuListener(this);
        new MaterialMenuListener(this);
        new PatternMenuListener(this);
        new SmithingTableListener(this);

        Bukkit.getConsoleSender().sendMessage(JUtil.format("&3&l[JoltingTrims] &7The plugin has been enabled!"));
    }

    @Override
    public void onDisable() {
        getDataManager().closeAllMenus();
    }

    public static JoltingTrims getInstance() {
        return getPlugin(JoltingTrims.class);
    }

    public DataManager getDataManager() {
        return dataManager;
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
