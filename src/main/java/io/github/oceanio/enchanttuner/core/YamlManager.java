package io.github.oceanio.enchanttuner.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YamlManager{
    private final JavaPlugin plugin;
    private final String fileName;
    private final File file;
    private FileConfiguration config;

    public YamlManager(JavaPlugin plugin, String fileName){
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.file = new File(plugin.getDataFolder(),this.fileName);
    }

    public void reload(){
        if (!file.exists()){
            file.getParentFile().mkdirs();
            if (plugin.getResource(fileName) != null){
                plugin.saveResource(fileName,false);
            }else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.SEVERE, fileName + " の作成に失敗しました。", e);
                }
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig(){
        if (config == null){
            reload();
        }
        return config;
    }

    public void save() {
        if (config == null || file == null) return;
        try {
            config.save(file);
        }catch (IOException e){
            plugin.getLogger().log(Level.SEVERE,fileName +"の保存に失敗しました",e);
        }
    }
}