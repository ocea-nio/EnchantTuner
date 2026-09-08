package io.github.oceanio.enchanttuner.feature.tuning.customenchant.delicate;

import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;

public class DelicateListener implements Listener, CustomEnchant {
    @Override
    public void apply(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        List<Component> lore = meta.lore();

        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Component.text("Delicate " + level));
        meta.lore(lore);
        item.setItemMeta(meta);
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){

    }
}
