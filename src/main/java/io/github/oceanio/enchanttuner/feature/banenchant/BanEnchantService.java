package io.github.oceanio.enchanttuner.feature.banenchant;

import io.github.oceanio.enchanttuner.core.YamlManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class BanEnchantService {
    private  Set<Enchantment> banEnchantments;

    public void load(YamlManager config) {
        this.banEnchantments = config.getConfig()
                .getStringList("ban-enchant")
                .stream()
                .map(NamespacedKey::fromString)
                .filter(Objects::nonNull)
                .map(Registry.ENCHANTMENT::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public boolean isBanned(Enchantment enchantment) {
        return banEnchantments.contains(enchantment);
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
