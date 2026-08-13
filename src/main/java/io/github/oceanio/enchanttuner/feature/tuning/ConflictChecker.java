package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;


import java.util.List;

public class ConflictChecker {
    public static boolean canEnchant(List<NamespacedKey> npc, PersistentDataContainer pdc, NamespacedKey key) {
        return true;
    }
}
