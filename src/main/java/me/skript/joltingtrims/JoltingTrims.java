package me.skript.joltingtrims;

import me.skript.joltingtrims.commands.TrimCommand;
import me.skript.joltingtrims.listeners.GeneralMenuListener;
import me.skript.joltingtrims.listeners.MaterialMenuListener;
import me.skript.joltingtrims.listeners.PatternMenuListener;
import me.skript.joltingtrims.listeners.SmithingTableListener;
import me.skript.joltingtrims.data.DataManager;
import me.skript.joltingtrims.utilities.JUtil;
import me.skript.joltinglib.configurations.JFilesManager;
import me.skript.joltinglib.configurations.JYML;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoltingTrims extends JavaPlugin {

    private JFilesManager filesManager = new JFilesManager<>(this);
    private DataManager dataManager;
    private JYML configuration;
    private JYML messages;
    private JYML generalMenu;
    private JYML materialMenu;
    private JYML patternMenu;

    @Override
    public void onEnable() {
        if(!JUtil.isVersionAtLeast("1.21.11")) {
            Bukkit.getConsoleSender().sendMessage(JUtil.format("&4&l[JoltingTrims] &7Server version is not supported."));
            Bukkit.getPluginManager().disablePlugin(this);
        }

        dataManager = new DataManager(this);

        configuration = filesManager.createYML("Configuration");
        messages = filesManager.createYML("Messages");

        filesManager.createFolder("GUI");

        generalMenu = filesManager.createYML("GeneralMenu", "GUI");
        materialMenu = filesManager.createYML("MaterialMenu", "GUI");
        patternMenu = filesManager.createYML("PatternMenu", "GUI");


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

        Bukkit.getConsoleSender().sendMessage(JUtil.format("&3&l[JoltingTrims] &7The plugin has been disabled!"));
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
