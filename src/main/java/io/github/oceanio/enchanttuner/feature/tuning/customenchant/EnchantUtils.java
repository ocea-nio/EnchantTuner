package io.github.oceanio.enchanttuner.feature.tuning.customenchant;


import io.github.oceanio.enchanttuner.feature.tuning.EnchantDefinition;
import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EnchantUtils {

    private EnchantUtils() {
    }

    public static String toRoman(int number) {
        int[] values = {
                1000, 900, 500, 400,
                100, 90, 50, 40,
                10, 9, 5, 4, 1
        };

        String[] symbols = {
                "M", "CM", "D", "CD",
                "C", "XC", "L", "XL",
                "X", "IX", "V", "IV", "I"
        };

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(symbols[i]);
            }
        }

        return result.toString();
    }

    public static void updateLore(
            ItemStack item,
            EnchantDefinition definition,
            int level
    ) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(
                Component.text(
                                definition.getDisplayName()
                                        + " "
                                        + toRoman(level)
                        )
                        .color(NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        );

        meta.lore(lore);
        item.setItemMeta(meta);
    }
    public static void updateLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        List<Component> lore = meta.lore();
        if (lore == null) {
            return;
        }

        // CustomEnchantのLoreを削除
        lore.removeIf(component ->
                component.color() != null
                        && component.color().equals(NamedTextColor.GRAY)
        );

        meta.lore(lore);
        item.setItemMeta(meta);
    }
}

