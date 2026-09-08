package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;


public class TuningListener implements Listener {

    private final JavaPlugin plugin;
    private final TuningService service;
    private static final Set<Integer> CLICKABLE_SLOT = Set.of(20,23,25);
    private static final int TARGET_SLOT = 20;
    private static final int COST_SLOT = 23;
    private static final int CONFIRM_SLOT = 25;


    public TuningListener(JavaPlugin plugin, TuningService service) {
        this.plugin = plugin;
        this.service = service;
    }

    @EventHandler
    public  void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!player.isSneaking()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.ANVIL) return;
        event.setCancelled(true); //デフォルトのイベントをキャンセル
        //GUI
        Inventory gui = new TuningHolder().getInventory();
        player.openInventory(gui);
    }



    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if (!(inv.getHolder() instanceof TuningHolder )) return;
        if (event.getRawSlot() >= inv.getSize()) {
            return;
        }
        // ドラッグや範囲クリックなど、想定外の取り方は無視する
        if (event.getClick() == ClickType.WINDOW_BORDER_LEFT
                || event.getClick() == ClickType.WINDOW_BORDER_RIGHT) {
            return;
        }
        switch (event.getRawSlot()) {
            case CONFIRM_SLOT -> {
                event.setCancelled(true);
                // 腐肉・対象装備が絡まない場合には一切干渉しない
                if (!TuningInput.containsRelevantItems(inv,TARGET_SLOT,COST_SLOT)) {
                    return;
                }
                TuningInput input = TuningInput.parse(inv,TARGET_SLOT,COST_SLOT);

                if (input == null) {
                    // 素材が既に無い・条件を満たさない
                    // → 何もせず
                    return;
                }
                int required = service.requiredFleshCount(input.targetItem());

                if (required < 0) {
                    player.sendMessage(ChatColor.RED + "このアイテムには既に5つのエンチャントが付いています！");
                    return;
                }

                if (input.rottenFleshCount() < required) {
                    player.sendMessage(ChatColor.RED + "腐肉が" + required + "個必要です！(現在"
                            + input.rottenFleshCount() + "個)");
                    return;
                }

                ItemStack result = input.targetItem();
                Map<EnchantDefinition,Integer> enchants = service.rollEnchantment(result);

                if (enchants.isEmpty()) {
                    // これ以上付与できる候補が無い(プール枯渇 or 全て付与済み)
                    player.sendMessage(ChatColor.RED + "これ以上付与できるエンチャントがありません！");
                    return; // 素材は消費しない
                }


                enchants.forEach((definition, level) -> {
                    if (definition.getType() == EnchantDefinition.EnchantType.VANILLA) {

                        Enchantment enchant =
                                Registry.ENCHANTMENT.get(definition.getKey());

                        if (enchant != null) {
                            //これが付与
                            result.addUnsafeEnchantment(enchant, level);
                        }

                    } else if (definition.getType() == EnchantDefinition.EnchantType.CUSTOM) {
                        // Custom enchantmentの付与処理

                    }

                    service.markAsApplied(result, definition);
                });
                service.consumeIngredients(inv, required,COST_SLOT);

                //音を鳴らす
                player.playSound(player.getLocation(),
                        Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
            }
            default -> {
                if (!CLICKABLE_SLOT.contains(event.getRawSlot())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (!(event.getPlayer() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if(!(inv.getHolder() instanceof TuningHolder)) return;
        ItemStack[] stack = {inv.getItem(TARGET_SLOT),inv.getItem(COST_SLOT)};
        for (ItemStack item : stack) {

            if (item == null || item.getType().isAir()) {
                continue;
            }
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item);

            // インベントリに入りきらなかった分
            for (ItemStack remaining : leftover.values()) {
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        remaining
                );
            }
        }
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

    /**
     * 砥石を使うと、呪いも含めて全てのエンチャントと
     * このシステムの付与済み記録をリセットする。
     */
    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {

        ItemStack result = event.getResult();

        if (result == null || result.getType().isAir()) {
            return;
        }

        ItemStack cleared = result.clone();
        service.resetAllEnchants(cleared);

        event.setResult(cleared);
    }
}