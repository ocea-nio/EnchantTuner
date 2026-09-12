package io.github.oceanio.enchanttuner.feature.tuning;


import io.github.oceanio.enchanttuner.core.YamlManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class EnchantPool {
    private final Map<String, EnchantDefinition> definitions = new HashMap<>();
    private final List<EnchantPoolEntry> entries = new ArrayList<>();

    public EnchantPool(YamlManager enchants, YamlManager enchantPool) {
        yamlLoadDefinitions(enchants);
        yamlLoadEntries(enchantPool);
    }

    /**
     *yamlManagerでenchantsの名前を引数に入れたら
     * enchantsという項目があり、enchantsも中にある
     * 一層目の項目をMapに入れる
     */
    private void yamlLoadDefinitions(YamlManager config) {
        ConfigurationSection section = config.getConfig().getConfigurationSection("enchants"); //enchants.ymlのenchants部分を読み込み
        if (section == null){
            return;
        }

        for (String id : section.getKeys(false)){
            ConfigurationSection enchant = section.getConfigurationSection(id);
            if (enchant == null){
                continue;
            }
            definitions.put(id, createDefinition(id, enchant));
        }
    }


    /**
     *entriesListに項目をforで追加。扱う場合は(id,weight)
     */
    private void yamlLoadEntries(YamlManager config) {
        ConfigurationSection pool = config.getConfig().getConfigurationSection("enchant-pool"); //第一層
        ConfigurationSection section = pool.getConfigurationSection("entries"); //第二層
        if (section == null){
            return;
        }

        for (String id : section.getKeys(false)){
            int weight = section.getInt(id + ".weight");
            entries.add(new EnchantPoolEntry(id, weight));
        }
    }

    private EnchantDefinition createDefinition(String id, ConfigurationSection section){
        EnchantDefinition.EnchantType type =
                EnchantDefinition.EnchantType.valueOf(
                        section.getString("type", "vanilla").toUpperCase()
                );

        NamespacedKey key =
                NamespacedKey.fromString(
                        section.getString("key")
                );

        boolean enabled =
                section.getBoolean("enabled", true);

        int maxLevel =
                section.getInt("max-level", 1);

        List<String> targets =
                section.getStringList("target");

        List<NamespacedKey> conflicts =
                section.getStringList("conflicts")
                        .stream()
                        .map(NamespacedKey::fromString)
                        .filter(Objects::nonNull)
                        .toList();

        String displayName = section.getString("display-name","");
        return new EnchantDefinition(id, type, key, enabled, maxLevel, targets, conflicts, displayName);
    }

    public EnchantDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    public List<EnchantPoolEntry> getEntries() {
        return entries;
    }
}