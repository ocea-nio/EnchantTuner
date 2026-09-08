package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class TuningKeys {
    public static NamespacedKey DELICATE;
    public static NamespacedKey APPLIED_ENCHANTS;

    public static void init(JavaPlugin plugin) {
        APPLIED_ENCHANTS =
                new NamespacedKey(plugin, "applied_enchants");
        DELICATE =
                new NamespacedKey(plugin, "delicate");
    }

    private TuningKeys() {
    }
}
