package io.github.oceanio.enchanttuner.feature.tuning;

import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 厳選システムのビジネスロジックを担当するクラス。
 * イベント処理(CraftItemEvent等)は TuningListener 側で行う。
 */
public class TuningService {

    private final EnchantPool pool;
    private final Map<NamespacedKey, CustomEnchant> customEnchants;

    private final NamespacedKey appliedEnchantsKey;

    public static final int MAX_ENCHANTS = 5;

    // 現在のエンチャント数(index) → 次の1個を付与するのに必要な腐肉数
    private static final int[] FLESH_COST = {1, 2, 5, 10, 20};

    public TuningService(EnchantPool pool, JavaPlugin plugin) {
        this(pool, plugin, Map.of());
    }

    public TuningService(EnchantPool pool, JavaPlugin plugin,
                         Map<NamespacedKey, CustomEnchant> customEnchants) {
        this.pool = pool;
        this.customEnchants = Map.copyOf(customEnchants);
        this.appliedEnchantsKey = new NamespacedKey(plugin, "applied_enchants");
    }

    /**
     * 対象アイテムに新しいエンチャントを1つロールする。
     * 既に付与済みの種類は候補から除外される。
     * 5個上限に達している場合は空のMapを返す。
     */
    public Map<EnchantDefinition, Integer> rollEnchantment(ItemStack item) {

        Bukkit.getLogger().info("rollEnchant fire");

        Map<EnchantDefinition, Integer> result = new HashMap<>();

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return result;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        // 5個上限チェック
        if (countAppliedEnchants(pdc) >= MAX_ENCHANTS) {
            return result;
        }
        //lambda
        List<EnchantDefinition> candidates =
                pool.getEntries().stream().map(entry -> {return pool.getDefinition(entry.getEnchantId());})
                        .filter(Objects::nonNull)
                        .filter(EnchantDefinition::isEnabled)
                        .filter(def -> def.getType() != EnchantDefinition.EnchantType.CUSTOM
                                || customEnchants.containsKey(def.getKey()))
                        .filter(def -> def.getTargets().stream().anyMatch(target -> TargetMatcher.canEnchant(target,item)))
                        .filter(def -> ConflictChecker.canEnchant(def.getConflicts(),pdc,appliedEnchantsKey))
                        .filter(def -> !hasBeenApplied(pdc, def.getKey()))
                        .toList();



        if (candidates.isEmpty()) {
            return result;
        }

        EnchantDefinition definition =
                candidates.get(
                        (int) (Math.random() * candidates.size())
                );

        int level =
                rollLevel(definition.getMaxLevel());

        result.put(
                definition,
                level
        );

        return result;
    }

    /**
     * 対象のエンチャントのレベルを決定
     */
    public int rollLevel(int maxLevel) {
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (roll < 50) return 1;
        if (roll < 80) return Math.min(2, maxLevel);
        if (roll < 95) return Math.min(3, maxLevel);
        if (roll < 99) return Math.min(4, maxLevel);
        return maxLevel;
    }



    /**
     * 対象アイテムが既に上限までエンチャントされているかどうかを判定する。
     */
    public boolean isFull(ItemStack item) {

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return false;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        return countAppliedEnchants(pdc) >= MAX_ENCHANTS;
    }

    /**
     * 対象アイテムに次の1個を付与するために必要な腐肉の数を返す。
     * 既に上限(5個)に達している場合は -1 を返す。
     */
    public int requiredFleshCount(ItemStack item) {

        if (isFull(item)) {
            return -1;
        }

        ItemMeta meta = item.getItemMeta();
        int current = 0;

        if (meta != null) {
            current = countAppliedEnchants(meta.getPersistentDataContainer());
        }

        return FLESH_COST[current];
    }

    /**
     * アイテムのエンチャントを(呪いも含めて)すべて削除し、
     * このシステムで管理している「付与済み記録」もクリアする。
     * 砥石使用時のリセット処理などで使う。
     */
    public void resetAllEnchants(ItemStack item) {

        for (Enchantment ench : new HashSet<>(item.getEnchantments().keySet())) {
            item.removeEnchantment(ench);
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(appliedEnchantsKey);

        item.setItemMeta(meta);
    }

    /**
     * itemの持っているエンチャントをsplitで
     * 配列に変えてlengthで数えてるためreturn int
     */
    private int countAppliedEnchants(PersistentDataContainer pdc) {

        String applied =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        if (applied == null || applied.isEmpty()) {
            return 0;
        }
        Bukkit.getLogger().info("PDC with Applied: [" + applied + "]");
        return applied.split(",").length;
    }

    // return boolean
    private boolean hasBeenApplied(PersistentDataContainer pdc, NamespacedKey key) {
        String applied =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        if (applied == null) {
            return false;
        }

        return List.of(applied.split(","))
                .contains(key.toString());
    }

    /**
     * 付与済みエンチャントとしてPDCに記録する。
     * item自体のItemMetaを更新して保存する。
     */
    public void markAsApplied(
            ItemStack item,
            EnchantDefinition enchantment
    ) {

        ItemMeta meta =
                item.getItemMeta();

        if (meta == null) {
            return;
        }

        PersistentDataContainer pdc =
                meta.getPersistentDataContainer();

        String current =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        String key =
                enchantment.getKey().toString();

        if (current == null || current.isEmpty()) {

            current = key;

        } else if (!List.of(
                current.split(",")
        ).contains(key)) {

            current += "," + key;
        }

        pdc.set(
                appliedEnchantsKey,
                PersistentDataType.STRING,
                current
        );

        item.setItemMeta(meta);
    }

    /**
     * クラフトマトリクス上の素材を消費する。
     * - 対象装備(ツール等)は1個消費する(=無くなる。新しいアイテムとして返すため)
     * - 腐肉は requiredFlesh 個ぶんだけ、複数スタックにまたがっていても消費する
     * 結果スロット(index 0)には触れない。
     */
    public void consumeIngredients(Inventory inv, int requiredFlesh,int cost_slot) {
        int remaining = requiredFlesh;
        ItemStack cost = inv.getItem(cost_slot);

        if (cost.getType() == Material.ROTTEN_FLESH) {
            if (remaining <= 0) {
                return;
            }

            int take = Math.min(remaining, cost.getAmount());
            cost.setAmount(cost.getAmount() - take);
        }else return;
    }

    /**
     * クラフトマトリクスの中から「エンチャント対象のツール」を探す。
     * 腐肉・エメラルド以外のアイテムを対象とみなす。
     */
    public ItemStack findTargetTool(CraftingInventory inv) {

        for (ItemStack item : inv.getMatrix()) {

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (item.getType() == Material.ROTTEN_FLESH) {
                continue;
            }

            return item;
        }

        return null;
    }
}
