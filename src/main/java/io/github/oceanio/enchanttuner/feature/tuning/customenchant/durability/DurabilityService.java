package io.github.oceanio.enchanttuner.feature.tuning.customenchant.durability;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public class DurabilityService implements CustomEnchant {
    @Override
    public void apply(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(
                TuningKeys.DURABILITY,
                PersistentDataType.INTEGER,
                level
        );
        Bukkit.getLogger().info("fired1");
        OverrideDurability(item,meta, level, pdc);
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

    private void OverrideDurability(ItemStack item,ItemMeta meta, int level,PersistentDataContainer pdc) {
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

        int bonus = meta.hasEquippable() ? 50 : 75;
        int prepare = defaultDura + bonus * level;

        damageable.setMaxDamage(prepare);
            Bukkit.getLogger().info("default:"+defaultDura+",prepare:"+prepare+"result:"+damageable.getMaxDamage());

    }
}
