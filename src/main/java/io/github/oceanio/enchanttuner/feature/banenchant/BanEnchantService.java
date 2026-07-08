package io.github.oceanio.enchanttuner.feature.banenchant;

import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class BanEnchantService {
    private final Set<Enchantment> ban_enchant = Set.of(
            Enchantment.MENDING,
            Enchantment.BINDING_CURSE
    );

    public boolean isBanned(Enchantment enchantment) {
        return ban_enchant.contains(enchantment);
    }

    public void removeBanEnchants(ItemStack item){
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof ShulkerBox shulker) {
                for (ItemStack inside : shulker.getInventory().getContents()) {
                    removeBanEnchants(inside);
                }
                bsm.setBlockState(shulker);
                meta = bsm;
        }
        for (Enchantment enchantment : new HashSet<>(meta.getEnchants().keySet())) {
            if (isBanned(enchantment)) {
                meta.removeEnchant(enchantment);
            }
        }
        item.setItemMeta(meta);
    }

    public void inventoryRemove(){
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (ItemStack item : player.getInventory().getContents()) {
                removeBanEnchants(item);
            }
            for (ItemStack item : player.getEnderChest().getContents()){
                removeBanEnchants(item);
            }
            for (ItemStack item : player.getInventory().getArmorContents()){
                removeBanEnchants(item);
            }
            removeBanEnchants(player.getInventory().getItemInOffHand());
        }
    }
}
