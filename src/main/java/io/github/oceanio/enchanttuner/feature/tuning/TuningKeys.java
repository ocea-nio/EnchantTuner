package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import javax.naming.Name;

public final class TuningKeys {
    public static NamespacedKey DELICATE;
    public static NamespacedKey APPLIED_ENCHANTS;
    public static NamespacedKey DURABILITY;
    public static NamespacedKey DEFAULT_DURA;
    public static NamespacedKey FLUIDHEAL;

    public static void init(JavaPlugin plugin) {
        //example
        APPLIED_ENCHANTS =
                new NamespacedKey(plugin, "applied_enchants");

        //addition
        DELICATE = new NamespacedKey(plugin, "delicate");
        DURABILITY = new NamespacedKey(plugin, "durability");
        FLUIDHEAL = new NamespacedKey(plugin, "fluid_heal");


        //Parameter
        DEFAULT_DURA = new NamespacedKey(plugin, "default_dura");


    }

    private TuningKeys() {
    }
}
