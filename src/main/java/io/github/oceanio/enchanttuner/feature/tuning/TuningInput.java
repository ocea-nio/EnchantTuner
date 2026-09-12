package io.github.oceanio.enchanttuner.feature.tuning;


import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public record TuningInput(ItemStack targetItem, int rottenFleshCount) {

    public static TuningInput parse(Inventory inv,int target_slot,int cost_slot) {
        ItemStack target = inv.getItem(target_slot);
        ItemStack cost = inv.getItem(cost_slot);
        ItemStack[] slot = {target,cost};

        for (ItemStack item : slot){
            if (item == null || item.getType() == Material.AIR) return null;
        }

        // 装備1つ、腐肉1つ以上が必要
        if(!(isEquipment(target)))return null;
        if (!(cost.getType() == Material.ROTTEN_FLESH)) return null;

        int flesh = cost.getAmount();


        return new TuningInput(target, flesh);
    }

    /**
     *GUI追加後の修正候補
     */
    public static boolean containsRelevantItems(Inventory inv,int target,int cost) {
        ItemStack[] slot = {inv.getItem(target), inv.getItem(cost)};
        for (ItemStack item : slot){
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            if (isEquipment(item) || item.getType() == Material.ROTTEN_FLESH) {
                return true;
            }
        }
            return false;
    }




    private static boolean isEquipment(ItemStack item) {
        String name = item.getType().name();
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") ||
                name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS") ||
                name.endsWith("_SWORD") || name.endsWith("_AXE") ||
                name.endsWith("_PICKAXE") || name.endsWith("_SHOVEL") ||
                name.endsWith("_HOE") || name.endsWith("_SPEAR") ||
                item.getType() == Material.TRIDENT ||
                item.getType() == Material.BOW || item.getType() == Material.CROSSBOW ||
                item.getType() == Material.MACE || item.getType() == Material.SHEARS ||
                item.getType() == Material.FISHING_ROD;
    }
}
