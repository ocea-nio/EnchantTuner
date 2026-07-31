package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.NamespacedKey;

import java.util.List;

public class EnchantDefinition {
    public enum EnchantType {
        VANILLA,
        CUSTOM
    }

    private final String id;
    private final EnchantType type;
    private final NamespacedKey key;
    private final boolean enabled;
    private final int maxLevel;
    private final List<String> targets;
    private final List<NamespacedKey> conflicts;

    public EnchantDefinition(String id, EnchantType type, NamespacedKey key, boolean enabled, int maxLevel, List<String> targets, List<NamespacedKey> conflicts) {
        this.id = id;
        this.type = type;
        this.key = key;
        this.enabled = enabled;
        this.maxLevel = maxLevel;
        this.targets = List.copyOf(targets);
        this.conflicts = List.copyOf(conflicts);
    }

    public String getId() { return id; }
    public EnchantType getType() { return type; }
    public NamespacedKey getKey() { return key; }
    public boolean isEnabled() { return enabled; }
    public int getMaxLevel() { return maxLevel; }
    public List<String> getTargets() { return targets; }
    public List<NamespacedKey> getConflicts() { return conflicts; }
}
