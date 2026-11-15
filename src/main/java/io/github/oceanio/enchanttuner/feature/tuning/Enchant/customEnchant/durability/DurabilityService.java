package io.github.oceanio.enchanttuner.feature.tuning.customEnchant.durability;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.Map;


public class DurabilityService implements CustomEnchant {
    @Override
    public void apply(ItemStack item, int level) {
        apply(item, level, Map.of());
    }

    @Override
    public void apply(ItemStack item, int level, Map<String, Object> args) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(
                TuningKeys.DURABILITY,
                PersistentDataType.INTEGER,
                level
        );
        Bukkit.getLogger().info("fired1");
        OverrideDurability(item,meta, level, pdc, args);
        Bukkit.getLogger().info("fired3");
        item.setItemMeta(meta);
    }

    @Override
    public boolean hasTuning(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(
                TuningKeys.DURABILITY,
                PersistentDataType.INTEGER
        );
    }

    private void OverrideDurability(ItemStack item,ItemMeta meta, int level,PersistentDataContainer pdc, Map<String, Object> args) {
        Bukkit.getLogger().info("fired2");
        if (!(meta instanceof Damageable damageable)) return;
        int currentMax = damageable.hasMaxDamage()
                ? damageable.getMaxDamage()
                : item.getType().getMaxDurability();

        if (currentMax <= 0) return;

        int defaultDura = pdc.getOrDefault(
                TuningKeys.DEFAULT_DURA,
                PersistentDataType.INTEGER,
                currentMax
        );

        pdc.set(
                TuningKeys.DEFAULT_DURA,
                PersistentDataType.INTEGER,
                defaultDura
        );

        int bonus = integerArg(args, "bonus-per-level", meta.hasEquippable() ? 50 : 75);
        int prepare = defaultDura + bonus * level;

        damageable.setMaxDamage(prepare);
            Bukkit.getLogger().info("default:"+defaultDura+",prepare:"+prepare+"result:"+damageable.getMaxDamage());

    }

    private int integerArg(Map<String, Object> args, String key, int fallback) {
        Object value = args.get(key);
        if (value instanceof Number number) return number.intValue();
        if (value instanceof String text) {
            try { return Integer.parseInt(text); } catch (NumberFormatException ignored) { }
        }
        return fallback;
    }
}
