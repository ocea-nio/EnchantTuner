package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuningService {

    private final EnchantPool pool = new EnchantPool();

    public Map<Enchantment, Integer> rollEnchantment(ItemStack item) {

        Map<Enchantment, Integer> result = new HashMap<>();

        List<EnchantPool.EnchantEntry> candidates = pool.getAllEntries().stream()
                .filter(e -> e.getEnchantment().canEnchantItem(item))
                .toList();

        if (candidates.isEmpty()) return result;

        EnchantPool.EnchantEntry entry = candidates.get(
                (int)(Math.random() * candidates.size())
        );

        int level = pool.rollLevel(entry.getMaxLevel());
        result.put(entry.getEnchantment(), level);

        return result;
    }

    public void consumeIngredients(CraftingInventory inv) {

        for (int i = 0; i < inv.getSize(); i++) {

            ItemStack item = inv.getItem(i);

            if (item == null || item.getType() == Material.AIR) continue;

            int newAmount = item.getAmount() - 1;

            if (newAmount <= 0) {
                inv.setItem(i, null);
            } else {
                item.setAmount(newAmount);
            }
        }
    }
}