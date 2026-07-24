package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class EnchantPool {

    public static class EnchantEntry {

        private final Enchantment enchantment;
        private final int maxLevel;
        private final int weight;

        public EnchantEntry(Enchantment enchantment, int maxLevel, int weight) {
            this.enchantment = enchantment;
            this.maxLevel = maxLevel;
            this.weight = weight;
        }

        public Enchantment getEnchantment() { return enchantment; }
        public int getMaxLevel() { return maxLevel; }
        public int getWeight() { return weight; }
    }

    private final List<EnchantEntry> entries = new ArrayList<>();
    private int totalWeight = 0;

    public EnchantPool() {

        //===== 防具系 =====
        addEntry(Enchantment.PROTECTION, 4, 5); // Protection
        addEntry(Enchantment.FIRE_PROTECTION, 4, 5);          // Fire Protection
        addEntry(Enchantment.FEATHER_FALLING, 4, 5);          // Feather Falling
        addEntry(Enchantment.BLAST_PROTECTION, 4, 5);     // Blast Protection
        addEntry(Enchantment.PROJECTILE_PROTECTION, 4, 5);     // Projectile Protection

        // ===== ダメージ系 =====
        addEntry(Enchantment.SHARPNESS, 5, 5);        // Sharpness
        addEntry(Enchantment.SMITE, 5, 5);     // Smite
        addEntry(Enchantment.BANE_OF_ARTHROPODS, 5, 5);  // Bane of Arthropods

        // ===== ツール系 =====
        addEntry(Enchantment.EFFICIENCY, 5, 5);         // Efficiency
        addEntry(Enchantment.SILK_TOUCH, 1, 5);        // Silk Touch
        addEntry(Enchantment.FORTUNE, 3, 5);           // Fortune (修正: 以前はSILK_TOUCHが重複登録されていた)

        // ===== 武器系 =====
        addEntry(Enchantment.KNOCKBACK, 2, 5);
        addEntry(Enchantment.FIRE_ASPECT, 2, 5);
        addEntry(Enchantment.LOOTING, 3, 5);   // Looting

        // ===== 弓 =====
        addEntry(Enchantment.POWER, 5, 5);      // Power
        addEntry(Enchantment.PUNCH, 2, 5);   // Punch
        addEntry(Enchantment.FLAME, 1, 5);        // Flame
        addEntry(Enchantment.INFINITY, 1, 5);    // Infinity

        // ===== 耐久=====
        addEntry(Enchantment.UNBREAKING, 3, 5);        // Unbreaking

        // ===== 釣り =====
        addEntry(Enchantment.LUCK_OF_THE_SEA, 3, 5);              // Luck of the Sea
        addEntry(Enchantment.LURE, 3, 5);              // Lure

        // ===== トライデント系 =====
        addEntry(Enchantment.IMPALING, 5, 5);
        addEntry(Enchantment.LOYALTY, 3, 5);
        addEntry(Enchantment.RIPTIDE, 3, 5);
        addEntry(Enchantment.CHANNELING, 1, 5);

        // ===== クロスボウ =====
        addEntry(Enchantment.QUICK_CHARGE, 3, 5);
        addEntry(Enchantment.PIERCING, 4, 5);
        addEntry(Enchantment.MULTISHOT, 1, 5);

        // ===== 防具特殊 =====
        addEntry(Enchantment.THORNS, 3, 5);
        addEntry(Enchantment.SOUL_SPEED, 3, 5);
    }

    public void addEntry(Enchantment ench, int maxLevel, int weight) {
        entries.add(new EnchantEntry(ench, maxLevel, weight));
        totalWeight += weight;
    }

    public EnchantEntry getRandomEntry() {
        if (entries.isEmpty()) return null;

        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int sum = 0;

        for (EnchantEntry e : entries) {
            sum += e.getWeight();
            if (roll < sum) {
                return e;
            }
        }

        return entries.get(0);
    }

    public int rollLevel(int maxLevel) {
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (roll < 50) return 1;
        if (roll < 80) return Math.min(2, maxLevel);
        if (roll < 95) return Math.min(3, maxLevel);
        if (roll < 99) return Math.min(4, maxLevel);
        return maxLevel;
    }

    public List<EnchantEntry> getAllEntries() {
        return entries;
    }
}