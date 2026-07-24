package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Material;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public record TuningInput(ItemStack targetItem, int rottenFleshCount) {

    public static TuningInput parse(JavaPlugin plugin, CraftingInventory inv) {
        ItemStack target = null;
        int flesh = 0;

        for (ItemStack item : inv.getMatrix()) {
            if (item == null || item.getType() == Material.AIR) continue;

            if (isEquipment(item)) {
                if (target != null) return null; // 装備が2つ以上ある場合は無効
                target = item;
            } else if (item.getType() == Material.ROTTEN_FLESH) {
                flesh += item.getAmount();
            } else {
                return null; // 関係ないアイテムが混ざっている
            }
        }

        // 装備1つ、腐肉1つ以上が必要
        if (target == null || flesh == 0) return null;

        return new TuningInput(target, flesh);
    }

    private static boolean isEquipment(ItemStack item) {
        String name = item.getType().name();
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") ||
                name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") ||
                name.endsWith("_SWORD") || name.endsWith("_AXE") ||
                name.endsWith("_PICKAXE") || name.endsWith("_SHOVEL") ||
                name.endsWith("_HOE") || item.getType() == Material.TRIDENT ||
                item.getType() == Material.BOW || item.getType() == Material.CROSSBOW;
    }
}