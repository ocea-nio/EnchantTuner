package io.github.oceanio.enchanttuner.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class YamlManager{
    private final JavaPlugin plugin;
    private final Map<String,FileConfiguration> yamlData = new HashMap<>();

    public YamlManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration loadYaml(String yamlName){
        plugin.saveResource(yamlName, false);

        File file = new File(plugin.getDataFolder(), yamlName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        yamlData.put(yamlName, config);
        return config;
    }

    public FileConfiguration getYaml(String yamlName){
        return yamlData.get(yamlName);
    }

    public FileConfiguration reloadYaml(String yamlName) {
        File file = new File(plugin.getDataFolder(), yamlName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()){
            plugin.saveResource(yamlName, false);
        }

        yamlData.put(yamlName, config);
        return config;
    }

    public boolean hasYaml(String yamlName) {
        return yamlData.containsKey(yamlName);
    }

    public void reloadAll() {
        for (String yamlName : new HashSet<>(yamlData.keySet())) {
            reloadYaml(yamlName);
        }
    }
}