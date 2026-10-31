package io.github.oceanio.enchanttuner.feature.tuning.customenchant.durability;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;



public class DurabilityListener implements Listener, CustomEnchant {
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
        OverrideDurability(meta,level);
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

    private void OverrideDurability(ItemMeta meta,int level){
        if(meta instanceof Damageable damageable){
            int defaultDura = damageable.getMaxDamage();
            int prepare = 0;
            if (meta.hasEquippable()) {
                prepare = defaultDura + (50 * level);
            }else prepare = defaultDura + (75 * level);
            if(prepare > 0){
                damageable.setMaxDamage(prepare);
            }
        }
    }


}
