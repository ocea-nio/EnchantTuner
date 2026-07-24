package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 厳選システムのビジネスロジックを担当するクラス。
 * イベント処理(CraftItemEvent等)は TuningListener 側で行う。
 */
public class TuningService {

    private final EnchantPool pool = new EnchantPool();

    private final NamespacedKey appliedEnchantsKey;

    public static final int MAX_ENCHANTS = 5;

    // 現在のエンチャント数(index) → 次の1個を付与するのに必要な腐肉数
    private static final int[] FLESH_COST = {1, 2, 5, 10, 20};

    public TuningService(JavaPlugin plugin) {
        this.appliedEnchantsKey =
                new NamespacedKey(plugin, "applied_enchants");
    }

    /**
     * 対象アイテムに新しいエンチャントを1つロールする。
     * 既に付与済みの種類は候補から除外される。
     * 5個上限に達している場合は空のMapを返す。
     */
    public Map<Enchantment, Integer> rollEnchantment(ItemStack item) {

        Map<Enchantment, Integer> result = new HashMap<>();

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

        List<EnchantPool.EnchantEntry> candidates =
                pool.getAllEntries().stream()
                        .filter(e -> e.getEnchantment().canEnchantItem(item))
                        .filter(e -> !hasBeenApplied(
                                pdc,
                                e.getEnchantment()
                        ))
                        .toList();

        if (candidates.isEmpty()) {
            return result;
        }

        EnchantPool.EnchantEntry entry =
                candidates.get(
                        (int) (Math.random() * candidates.size())
                );

        int level =
                pool.rollLevel(entry.getMaxLevel());

        result.put(
                entry.getEnchantment(),
                level
        );

        return result;
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

    private int countAppliedEnchants(PersistentDataContainer pdc) {

        String applied =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        if (applied == null || applied.isEmpty()) {
            return 0;
        }

        return applied.split(",").length;
    }

    private boolean hasBeenApplied(
            PersistentDataContainer pdc,
            Enchantment enchantment
    ) {

        String key =
                enchantment.getKey().toString();

        String applied =
                pdc.get(
                        appliedEnchantsKey,
                        PersistentDataType.STRING
                );

        if (applied == null) {
            return false;
        }

        return List.of(
                applied.split(",")
        ).contains(key);
    }

    /**
     * 付与済みエンチャントとしてPDCに記録する。
     * item自体のItemMetaを更新して保存する。
     */
    public void markAsApplied(
            ItemStack item,
            Enchantment enchantment
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
    public void consumeIngredients(CraftingInventory inv, int requiredFlesh) {

        ItemStack[] matrix = inv.getMatrix();
        int remaining = requiredFlesh;

        for (int i = 0; i < matrix.length; i++) {

            ItemStack item = matrix[i];

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (item.getType() == Material.ROTTEN_FLESH) {

                if (remaining <= 0) {
                    continue;
                }

                int take = Math.min(remaining, item.getAmount());
                item.setAmount(item.getAmount() - take);
                remaining -= take;

            } else {
                // 対象装備は1個消費して無くす
                int newAmount = item.getAmount() - 1;
                item.setAmount(Math.max(newAmount, 0));
            }
        }

        inv.setMatrix(matrix);
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