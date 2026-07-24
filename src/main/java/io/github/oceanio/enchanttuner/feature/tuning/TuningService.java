package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuningService {

    private final EnchantPool pool = new EnchantPool();

    private final NamespacedKey appliedEnchantsKey;

    public TuningService(JavaPlugin plugin) {
        this.appliedEnchantsKey =
                new NamespacedKey(plugin, "applied_enchants");
    }

    public Map<Enchantment, Integer> rollEnchantment(ItemStack item) {

        Map<Enchantment, Integer> result = new HashMap<>();

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return result;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        List<EnchantPool.EnchantEntry> candidates =
                pool.getAllEntries().stream()
                        .filter(e -> e.getEnchantment().canEnchantItem(item))
                        .filter(e -> !hasBeenApplied(
                                pdc,
                                e.getEnchantment()
                        ))
                        .toList();

        if (candidates.isEmpty()) {
            return result;
        }

        EnchantPool.EnchantEntry entry =
                candidates.get(
                        (int) (Math.random() * candidates.size())
                );

        int level =
                pool.rollLevel(entry.getMaxLevel());

        result.put(
                entry.getEnchantment(),
                level
        );

        return result;
    }

    private boolean hasBeenApplied(
            PersistentDataContainer pdc,
            Enchantment enchantment
    ) {

        String key =
                enchantment.getKey().toString();

        String applied =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        if (applied == null) {
            return false;
        }

        return List.of(
                applied.split(",")
        ).contains(key);
    }

    public void markAsApplied(
            ItemStack item,
            Enchantment enchantment
    ) {

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String current =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        String key =
                enchantment.getKey().toString();

        if (current == null || current.isEmpty()) {

            current = key;

        } else if (!List.of(
                current.split(",")
        ).contains(key)) {

            current += "," + key;
        }

        pdc.set(
                appliedEnchantsKey,
                PersistentDataType.STRING,
                current
        );}}