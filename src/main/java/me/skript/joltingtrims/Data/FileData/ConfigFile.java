package me.skript.joltingtrims.Data.FileData;

import me.skript.joltingtrims.JoltingTrims;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

/*
 *
 * Author: Skript
 * Last Update: 04/5/2020
 *
 */

public class ConfigFile {

    private JoltingTrims plugin;
    private File file;
    private FileConfiguration config;
    private String fileAsName;
    private String folderPath;

    //----------------------------[ ConfigFiles Constructors Start ]----------------------------//
    //Used to create a config file on the default plugins path.
    public ConfigFile(JoltingTrims pluginName, String fileName) {
        plugin = pluginName;
        file = new File(plugin.getDataFolder(), fileName + ".yml");
        fileAsName = fileName;
        folderPath = "default/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }

    //Used to create a config file on the specified path.
    public ConfigFile(JoltingTrims pluginName, String fileName, String folderPathName) {
        plugin = pluginName;
        file = new File(plugin.getDataFolder() + "/" + folderPathName, fileName + ".yml");
        fileAsName = fileName;
        folderPath = folderPathName + "/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }

    //Used to create a config file on the specified path(s).
    public ConfigFile(JoltingTrims pluginName, String fileName, String... folderPathName) {
        plugin = pluginName;
        String path = "default";
        for(String s : folderPathName) {
            path += "/" + s;
        }
        file = new File(plugin.getDataFolder() + "/" + path, fileName + ".yml");
        fileAsName = fileName;
        folderPath = path + "/" + fileName + ".yml";
        setupConfig();
        config = new YamlConfiguration();
        reloadConfig();
    }
    //-----------------------------[ ConfigFiles Constructors End ]-----------------------------//

    //------------------------------[ ConfigFiles Methods Start ]-------------------------------//
    //Used to return the configuration the file.
    public FileConfiguration getConfig() {
        return config;
    }

    //Used to return the file.
    public File getConfigFile() {
        return file;
    }

    //Used to return the file name.
    public String getFileAsName() {
        return fileAsName;
    }

    //Used to return the file name. (Including the .yml)
    public String getFileName() { return file.getName(); }

    //Used to return the folder path.
    public String getFolderPath() { return folderPath; }

    //Used to save the configuration file.
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage( fileAsName + " file saving has been cancelled. [IOException]");
        }
    }

    //Used to load file contents into the file configuration.
    public void reloadConfig() {
        try {
            config.load(file);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(fileAsName + " file reloading has been cancelled. [Exception]");
        }
    }

    //Used to copy the default config from the jar into the plugin folder.
    public void copyDefaults(InputStream inputFile, File outputFile) {
        try {
            OutputStream output = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int length;
            while((length = inputFile.read(buffer)) > 0){
                output.write(buffer,0, length);
            }
            output.close();
            inputFile.close();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Copy defaults has been cancelled. [Exception]");
        }
    }

    //Used to create the configuration file.
    private void setupConfig() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            copyDefaults(plugin.getResource(file.getName()), file);
        }
    }
    //-------------------------------[ ConfigFiles Methods End ]--------------------------------//

    //---------------------------[ ConfigFiles Extra Methods Start ]----------------------------//
    //----------------------------[ ConfigFiles Extra Methods End ]-----------------------------//
}
