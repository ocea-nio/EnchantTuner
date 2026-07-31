package io.github.oceanio.enchanttuner.feature.tuning;

public class EnchantPoolEntry {
    private final String enchantId;
    private final int weight;

    public EnchantPoolEntry(String enchantId, int weight) {
        this.enchantId = enchantId;
        this.weight = weight;
    }

    public String getEnchantId() {
        return enchantId;
    }

    public int getWeight() {
        return weight;
    }
}
