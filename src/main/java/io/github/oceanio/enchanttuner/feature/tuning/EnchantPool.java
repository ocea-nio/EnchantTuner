package io.github.oceanio.enchanttuner.feature.tuning;


import io.github.oceanio.enchanttuner.core.YamlManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class EnchantPool {
    private final Map<String, EnchantDefinition> definitions = new HashMap<>();
    private final List<EnchantPoolEntry> entries = new ArrayList<>();

    public EnchantPool(YamlManager enchants, YamlManager enchantPool) {
        this(enchants, enchantPool, null);
    }

    public EnchantPool(YamlManager enchants, YamlManager enchantPool, JavaPlugin plugin) {
        yamlLoadDefinitions(enchants);
        if (plugin != null) yamlLoadDefinitionFiles(plugin);
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

    /** Load optional one-enchant-per-file definitions from plugins/EnchantTuner/enchants/. */
    private void yamlLoadDefinitionFiles(JavaPlugin plugin) {
        Path directory = plugin.getDataFolder().toPath().resolve("enchants");
        try {
            Files.createDirectories(directory);
            try (var files = Files.list(directory)) {
                files.filter(path -> path.getFileName().toString().endsWith(".yml"))
                        .sorted()
                        .forEach(path -> loadDefinitionFile(path));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load enchant definition folder: " + directory, exception);
        }
    }

    private void loadDefinitionFile(Path file) {
        String fileName = file.getFileName().toString();
        String id = fileName.substring(0, fileName.length() - ".yml".length());
        if (definitions.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate enchant definition id '" + id + "' in " + file);
        }
        ConfigurationSection section = YamlConfiguration.loadConfiguration(file.toFile());
        definitions.put(id, createDefinition(id, section));
        ConfigurationSection pool = section.getConfigurationSection("pool");
        if (pool != null && pool.getBoolean("enabled", true)) {
            int weight = pool.getInt("weight", 1);
            if (weight > 0) entries.add(new EnchantPoolEntry(id, weight));
        }
    }


    /**
     *entriesListに項目をforで追加。扱う場合は(id,weight)
     */
    private void yamlLoadEntries(YamlManager config) {
        ConfigurationSection pool = config.getConfig().getConfigurationSection("enchant-pool"); //第一層
        ConfigurationSection section = pool == null ? null : pool.getConfigurationSection("entries"); //第二層
        if (section == null){
            return;
        }

        for (String id : section.getKeys(false)){
            if (entries.stream().anyMatch(entry -> entry.getEnchantId().equals(id))) continue;
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
        if (key == null) {
            throw new IllegalArgumentException("Enchant '" + id + "' has an invalid or missing key");
        }

        boolean enabled = section.getBoolean("enabled", section.getBoolean("enable", true));

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
        List<EffectDefinition> effects = new ArrayList<>();
        for (Map<?, ?> effect : section.getMapList("effects")) {
            Object rawId = effect.get("id");
            if (!(rawId instanceof String effectId) || effectId.isBlank()) {
                throw new IllegalArgumentException("Enchant '" + id + "' has an effect without an id");
            }
            Map<String, Object> args = new HashMap<>();
            Object rawArgs = effect.get("args");
            if (rawArgs instanceof Map<?, ?> values) {
                values.forEach((arg, value) -> args.put(String.valueOf(arg), value));
            }
            List<EffectTrigger> triggers = new ArrayList<>();
            Object rawTriggers = effect.get("triggers");
            if (rawTriggers instanceof List<?> values) {
                for (Object value : values) {
                    if (value instanceof String trigger) {
                        triggers.add(EffectTrigger.fromConfig(trigger));
                    }
                }
            }
            effects.add(new EffectDefinition(effectId, args, triggers));
        }
        // Existing server-side enchants.yml files predate configurable effects.
        // Preserve the built-in custom enchant behavior when that field is absent.
        if (!section.contains("effects") && type == EnchantDefinition.EnchantType.CUSTOM) {
            effects.addAll(legacyEffects(id));
        }
        return new EnchantDefinition(id, type, key, enabled, maxLevel, targets, conflicts, displayName, effects);
    }

    private List<EffectDefinition> legacyEffects(String id) {
        return switch (id) {
            case "delicate" -> List.of(new EffectDefinition("delicate_unripe_block", Map.of("cancel-unripe", true), List.of(EffectTrigger.BLOCK_BREAK)));
            case "fluid_heal" -> List.of(
                    new EffectDefinition("fluid_heal_attack", Map.of(), List.of(EffectTrigger.MELEE_ATTACK)),
                    new EffectDefinition("fluid_heal_kill", Map.of("duration-seconds", 5, "amplifier", "%level% - 1"), List.of(EffectTrigger.ENTITY_KILL))
            );
            case "durability" -> List.of(new EffectDefinition("durability_bonus", Map.of("bonus-per-level", 50), List.of()));
            default -> List.of();
        };
    }

    public EnchantDefinition getDefinition(String id) {
        return definitions.get(id);
    }

    public Collection<EnchantDefinition> getDefinitions() {
        return List.copyOf(definitions.values());
    }

    public List<EnchantPoolEntry> getEntries() {
        return entries;
    }
}
