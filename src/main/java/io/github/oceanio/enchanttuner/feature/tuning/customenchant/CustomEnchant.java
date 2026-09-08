package io.github.oceanio.enchanttuner.feature.tuning.customenchant;

import org.bukkit.inventory.ItemStack;

public interface CustomEnchant {
    void apply(ItemStack item, int level);
}
