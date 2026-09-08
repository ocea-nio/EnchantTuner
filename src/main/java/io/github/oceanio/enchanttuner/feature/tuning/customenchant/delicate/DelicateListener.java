package io.github.oceanio.enchanttuner.feature.tuning.customenchant.delicate;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DelicateListener implements Listener, CustomEnchant {
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

        updateLore(item);
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();

        // Delicateが付いているか確認
        if (!hasDelicate(item)) {
            return;
        }
        Block block = event.getBlock();
        BlockData data = block.getBlockData();
        if (data instanceof Ageable ageable){
            int age = ageable.getAge();
            int maxAge = ageable.getMaximumAge();
            if (age < maxAge) {
                event.setCancelled(true);
            }
        }
    }

    private void updateLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        Integer level = pdc.get(
                TuningKeys.DELICATE,
                PersistentDataType.INTEGER
        );

        if (level == null) {
            return;
        }

        List<Component> lore = meta.lore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(Component.text("繊細 " + level).color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));

        meta.lore(lore);
        item.setItemMeta(meta);
    }

    private boolean hasDelicate(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(
                TuningKeys.DELICATE,
                PersistentDataType.INTEGER
        );
    }

}
