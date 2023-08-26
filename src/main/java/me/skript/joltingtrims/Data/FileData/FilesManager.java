package me.skript.joltingtrims.Data.FileData;

import me.skript.joltingtrims.JoltingTrims;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.*;

/*
*
* Author: Skript
* Last Update: 04/5/2020
*
*/

public class FilesManager {

    private JoltingTrims plugin;
    private HashMap<String, ConfigFile> filesMap = new HashMap<String, ConfigFile>();

    //------------------------------[ Manager Constructors Start ]-----------------------------//
    //We're making the constructors that initializes the file manager.
    //We're initializing the files manager by specifying the plugin.
    public FilesManager(JoltingTrims plugin) {
        this.plugin = plugin;
    }
    //-------------------------------[ Manager Constructors End ]------------------------------//

    //--------------------------------[ Folder Creation Start ]--------------------------------//
    //We're creating a folder with the specified path(s).
    public void folderSetup(String folderName, String... folderPathName) {
        String path = "";
        for(String s : folderPathName) {
            path += "/" + s;
        }
        File folder = new File(plugin.getDataFolder() + "/" + path, folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    //We're creating a folder on the specified path.
    public void folderSetup(String folderName, String folderPathName) {
        File folder = new File(plugin.getDataFolder() + "/" + folderPathName, folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }

    //We're creating a folder on the default path.
    public void folderSetup(String folderName) {
        File folder = new File(plugin.getDataFolder(), folderName);
        if(!folder.exists()) {
            folder.mkdirs();
        }
    }
    //---------------------------------[ Folder Creation End ]---------------------------------//

    //---------------------------------[ File Creation Start ]---------------------------------//
    //Using this method we create a file, specifying the path of the folder too.
    public ConfigFile fileSetup(String fileName, String folderName) {
        ConfigFile file = new ConfigFile(plugin, fileName, folderName);
        filesMap.put("default/" + folderName + "/" + fileName + ".yml", file);
        return file;
    }

    //Using this method we create a new file on the default folder.
    public ConfigFile fileSetup(String fileName) {
        ConfigFile file = new ConfigFile(plugin, fileName);
        filesMap.put("default/" + fileName + ".yml", file);
        return file;
    }
    //----------------------------------[ File Creation End ]----------------------------------//

    //------------------------------------[ Methods Start ]------------------------------------//
    //Used to delete a configuration file.
    public void deleteFile(ConfigFile fileName) {
        if(fileName.getConfigFile().exists()) {
            fileName.getConfigFile().delete();
            filesMap.remove(fileName.getFolderPath());
            Bukkit.getConsoleSender().sendMessage("Successfully deleted file. [" + fileName.getFileName() + "]");
        }
        else {
            Bukkit.getConsoleSender().sendMessage("Unable to delete file. [" + fileName.getFileName() + "]");
        }
    }

    //Used to return a specific configuration file.
    public ConfigFile getFile(String fileName) {
        return filesMap.get(fileName);
    }

    //Used to return all configuration files as a HashMap.
    public HashMap<String, ConfigFile> getAllRegisteredConfigs() {
        return filesMap;
    }

    //Used to return a HashMap with all the paths and configs on the specified folder.
    public HashMap<String, ConfigFile> getAllConfigs(String filesPath) {
        HashMap<String, ConfigFile> map = new HashMap<>();
        for(Map.Entry file : filesMap.entrySet()) {
            String fileName = filesMap.get(file.getKey()).getConfigFile().getName();
            String path = ((String)file.getKey()).replace("/" + fileName, "");
            if(path.equals(filesPath)) {
                map.put(file.getKey() + "", (ConfigFile)file.getValue());
            }
        }
        return map;
    }

    //Used to return a HashMap with all the paths and configs of the specified folder even if they are not registered.
    public HashMap<String, ConfigFile> getAllFiles(String filesPath) {
        HashMap<String, ConfigFile> map = new HashMap<>();
        String folderPath;

        if(filesPath.contains("/")) {
            folderPath = filesPath.replace("default/", "");
        }
        else {
            folderPath = "default";
        }

        File folder;

        if(folderPath.equals("default")) {
            folder = new File(plugin.getDataFolder() + "");
            for(File file : folder.listFiles()) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""));
                    map.put(folderPath + "/" + file.getName(), confFile);
                    filesMap.put(folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        else {
            folder = new File(plugin.getDataFolder() + "/" + folderPath);
            for(File file : folder.listFiles()) {
                if(!file.isDirectory()) {
                    ConfigFile confFile = fileSetup(file.getName().replace(".yml", ""), folderPath);
                    map.put("default/" + folderPath + "/" + file.getName(), confFile);
                    filesMap.put("default/" + folderPath + "/" + file.getName(), confFile);
                }
            }
        }
        return map;
    }
    //-------------------------------------[ Methods End ]-------------------------------------//
}
