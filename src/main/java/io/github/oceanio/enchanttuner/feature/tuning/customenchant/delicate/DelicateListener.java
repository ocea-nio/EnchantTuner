package io.github.oceanio.enchanttuner.feature.tuning.customenchant.delicate;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;



public class DelicateListener implements CustomEnchant {
    @Override
    public void apply(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        pdc.set(
                TuningKeys.DELICATE,
                PersistentDataType.INTEGER,
                level
        );

        item.setItemMeta(meta);
    }
    @Override
    public void onTrigger(ItemStack item, int level, Event triggerEvent, java.util.Map<String, Object> args) {
        if (!(triggerEvent instanceof BlockBreakEvent event)) return;
        Block block = event.getBlock();
        BlockData data = block.getBlockData();
        boolean cancelUnripe = !Boolean.FALSE.equals(args.get("cancel-unripe"));
        if (cancelUnripe && data instanceof Ageable ageable){
            int age = ageable.getAge();
            int maxAge = ageable.getMaximumAge();
            if (age < maxAge) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean hasTuning(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(
                TuningKeys.DELICATE,
                PersistentDataType.INTEGER
        );
    }

}
