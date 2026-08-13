package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.inventory.ItemStack;

public class TargetMatcher {
    public static boolean canEnchant(String target, ItemStack item) {
        String  material = item.getType().name();

        switch (target) {
            case "sword" -> {
                if (material.endsWith("_SWORD")) return true;
            }
            case "pickaxe" -> {
                if (material.endsWith("_PICKAXE")) return true;
            }
            case "shovel" -> {
                if (material.endsWith("_SHOVEL")) return true;
            }
            case "axe" -> {
                if (material.endsWith("_AXE") && !material.endsWith("_PICKAXE")) return true;
            }
            case "hoe" -> {
                if (material.endsWith("_HOE")) return true;
            }
            case "trident" -> {
                if (material.endsWith("TRIDENT")) return true;
            }
            case "fishing_rod" -> {
                if (material.endsWith("FISHING_ROD")) return true;
            }
            case "shears" -> {
                if (material.endsWith("SHEARS")) return true;
            }
            case "spear" -> {
                if (material.endsWith("_SPEAR")) return true;
            }
            case "bow" -> {
                if (material.endsWith("BOW") && !material.endsWith("CROSSBOW")) return true;
            }
            case "crossbow" -> {
                if (material.endsWith("CROSSBOW")) return true;
            }
            case "mace" -> {
                if (material.endsWith("MACE")) return true;
            }
            case "helmet" -> {
                if (material.endsWith("_HELMET")) return true;
            }
            case "chestplate" -> {
                if (material.endsWith("_CHESTPLATE")) return true;
            }
            case "leggings" -> {
                if (material.endsWith("_LEGGINGS")) return true;
            }
            case "boots" -> {
                if (material.endsWith("_BOOTS")) return true;
            }
            case "shield" -> {
                if (material.endsWith("SHIELD")) return true;
            }
        }
        return false;
    }
}
