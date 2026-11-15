package io.github.oceanio.enchanttuner.feature.tuning.customenchant;

import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Event;

import java.util.Map;

public interface CustomEnchant {
    void apply(ItemStack item, int level);
    default void apply(ItemStack item, int level, Map<String, Object> args) {
        apply(item, level);
    }
    boolean hasTuning(ItemStack item);

    /** Handle an event trigger configured for this effect in enchants.yml. */
    default void onTrigger(ItemStack item, int level, Event event, Map<String, Object> args) {
    }
}
