package io.github.oceanio.enchanttuner.feature.tuning;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/** Bridges Bukkit events to effects selected by each enchant's YAML triggers. */
public final class EffectDispatcher implements Listener {
    private final TuningService service;

    public EffectDispatcher(TuningService service) {
        this.service = service;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        service.dispatchEffects(event.getPlayer().getInventory().getItemInMainHand(), EffectTrigger.BLOCK_BREAK, event);
    }

    @EventHandler
    public void onMeleeAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            service.dispatchEffects(player.getInventory().getItemInMainHand(), EffectTrigger.MELEE_ATTACK, event);
        }
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            ItemStack item = killer.getInventory().getItemInMainHand();
            service.dispatchEffects(item, EffectTrigger.ENTITY_KILL, event);
        }
    }
}
