package io.github.oceanio.enchanttuner.core;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConfigManager {
    private final JavaPlugin plugin;

    private final Set<Enchantment> banned = new HashSet<>();
    private final Map<String, List<Enchantment>> tuningpools = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        banned.clear();
        tuningpools.clear();

        loadBans();
        loadPools();
    }

    private void loadBans() {
        List<String> list = plugin.getConfig().getStringList("ban-enchant");

        for (String name : list) {
            Enchantment e = Enchantment.getByName(name);
            if (e != null) banned.add(e);
        }
    }

    private void loadPools() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("pools");
        if (sec == null) return;

        for (String key : sec.getKeys(false)) {
            List<String> list = sec.getStringList(key);

            List<Enchantment> enchants = new ArrayList<>();
            for (String name : list) {
                Enchantment e = Enchantment.getByName(name);
                if (e != null) enchants.add(e);
            }

            tuningpools.put(key, enchants);
        }
    }
}
