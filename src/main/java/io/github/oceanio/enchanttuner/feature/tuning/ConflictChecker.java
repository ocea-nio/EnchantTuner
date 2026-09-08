package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


import java.util.Collections;
import java.util.List;

public class ConflictChecker {
    public static boolean canEnchant(List<NamespacedKey> npc, PersistentDataContainer pdc, NamespacedKey key) {
        String applied = pdc.get(key, PersistentDataType.STRING);
        if (applied == null){
            return true;
        }
        Bukkit.getLogger().info(applied);
        List<String> conflicts = npc.stream().map(NamespacedKey::toString).toList();
        return  Collections.disjoint(List.of(applied.split(",")), conflicts);

    }
}
