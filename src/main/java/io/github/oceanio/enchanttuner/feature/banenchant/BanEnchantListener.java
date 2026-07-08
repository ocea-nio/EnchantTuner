package io.github.oceanio.enchanttuner.feature.banenchant;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class BanEnchantListener implements Listener {
    private final BanEnchantService service;

    public BanEnchantListener(BanEnchantService service) {
        this.service = service;
    }
    @EventHandler
    public void onEnchantTable(EnchantItemEvent event){
        event.getEnchantsToAdd().entrySet().removeIf(e -> service.isBanned(e.getKey()));
    }

    @EventHandler
    public void onLootTable(LootGenerateEvent event){
        for (ItemStack item : event.getLoot()){
            if (item == null || !item.hasItemMeta()) continue;
            service.removeBanEnchants(item);
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event){
        Inventory inventory = event.getInventory();
        for (ItemStack item : inventory.getContents()) {
            service.removeBanEnchants(item);
        }
    }
}
