package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class TuningListener implements Listener {

    private final JavaPlugin plugin;
    private final TuningService service;

    public TuningListener(JavaPlugin plugin, TuningService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPrepare(PrepareItemCraftEvent event) {

        TuningInput input = TuningInput.parse(plugin, event.getInventory());

        if (input == null) {
            // 条件を満たしていない場合は必ず結果スロットを空にする
            // (これを怠るとプレビューだけが残り、後述の複製バグの温床になる)
            event.getInventory().setResult(null);
            return;
        }

        int required = service.requiredFleshCount(input.targetItem());

        if (required < 0 || input.rottenFleshCount() < required) {
            event.getInventory().setResult(null);
            return;
        }

        ItemStack result = input.targetItem().clone();
        var meta = result.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "??? エンチャント付与 (腐肉" + required + "個)");
            result.setItemMeta(meta);
        }

        event.getInventory().setResult(result);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory() instanceof CraftingInventory inv)) return;
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        // 結果スロットへの操作は常にこちらで制御する。
        // 条件を満たさない・既に処理済みの場合も含め、必ずキャンセルして
        // バニラのデフォルト処理(タダ取り)が走らないようにする。
        event.setCancelled(true);

        // ドラッグや範囲クリックなど、想定外の取り方は無視する
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT
                || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) {
            return;
        }

        TuningInput input = TuningInput.parse(plugin, inv);

        if (input == null) {
            // 素材が既に無い・条件を満たさない
            // → 何もせず、結果スロットの表示も念のため消す
            inv.setResult(null);
            return;
        }

        int required = service.requiredFleshCount(input.targetItem());

        if (required < 0) {
            player.sendMessage(ChatColor.RED + "このアイテムには既に5つのエンチャントが付いています！");
            inv.setResult(null);
            return;
        }

        if (input.rottenFleshCount() < required) {
            player.sendMessage(ChatColor.RED + "腐肉が" + required + "個必要です！(現在"
                    + input.rottenFleshCount() + "個)");
            inv.setResult(null);
            return;
        }

        ItemStack result = input.targetItem().clone();
        var enchants = service.rollEnchantment(result);

        if (enchants.isEmpty()) {
            // これ以上付与できる候補が無い(プール枯渇 or 全て付与済み)
            player.sendMessage(ChatColor.RED + "これ以上付与できるエンチャントがありません！");
            inv.setResult(null);
            return; // 素材は消費しない
        }

        enchants.forEach(result::addUnsafeEnchantment);
        enchants.keySet().forEach(ench -> service.markAsApplied(result, ench));

        service.consumeIngredients(inv, required);

        // 処理完了後、結果スロットの表示を必ずクリアする
        inv.setResult(null);

        player.getInventory().addItem(result).values().forEach(drop ->
                player.getWorld().dropItemNaturally(player.getLocation(), drop));

        player.playSound(player.getLocation(),
                Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);

        // クライアントとの表示ズレを解消するため、次のtickで強制的に同期する
        Bukkit.getScheduler().runTask(plugin, player::updateInventory);
    }

    /**
     * バニラのエンチャントテーブルを使用不可にする。
     * GUIを開こうとした時点でキャンセルする。
     */
    @EventHandler
    public void onOpenEnchantTable(InventoryOpenEvent event) {

        if (event.getInventory().getType() != InventoryType.ENCHANTING) {
            return;
        }

        event.setCancelled(true);

        if (event.getPlayer() instanceof Player player) {
            player.sendMessage(ChatColor.RED + "エンチャントテーブルは使用できません。");
        }
    }
}