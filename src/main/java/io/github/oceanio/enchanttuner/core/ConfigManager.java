package io.github.oceanio.enchanttuner.core;

import java.util.*;

public class ConfigManager {
    private final YamlManager config;

    public ConfigManager(YamlManager config) {
        this.config = config;
    }

    public List<String> getStringList(String path) {
        if (!config.getConfig().contains(path)) {
            throw new IllegalArgumentException("存在しない設定です: " + path);
        }
        return config.getConfig().getStringList(path);
    }

    public int getInt(String path) {
        if (!config.getConfig().contains(path)) {
            throw new IllegalArgumentException("存在しない設定です: " + path);
        }
        return config.getConfig().getInt(path);
    }
}