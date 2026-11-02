package io.github.oceanio.enchanttuner.feature.tuning.customenchant.fluidHeal;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FluidHealListener implements Listener, CustomEnchant {
    private final JavaPlugin plugin;

    public FluidHealListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void apply(ItemStack item, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(
                TuningKeys.FLUIDHEAL,
                PersistentDataType.INTEGER,
                level
        );
        item.setItemMeta(meta);
    }

    @Override
    public boolean hasTuning(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        return meta.getPersistentDataContainer().has(
                TuningKeys.FLUIDHEAL,
                PersistentDataType.INTEGER
        );
    }

    @EventHandler
    public void onKilled(EntityDeathEvent event){
        LivingEntity victim = event.getEntity();

        Player player = victim.getKiller();
        if (player == null) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (!hasTuning(weapon)) return;

        ItemMeta meta = weapon.getItemMeta();
        if (meta == null) return;

        int enchantLevel = meta.getPersistentDataContainer().getOrDefault(
                TuningKeys.FLUIDHEAL,
                PersistentDataType.INTEGER,
                0
        );

        if (enchantLevel < 1) return;

        healParticle(victim,player);

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                20 * 5,
                enchantLevel - 1
        ));
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        if (!hasTuning(weapon))return;
        Entity victim = event.getEntity();

        World world = victim.getWorld();
        Location location = victim.getLocation().add(0,0.15,0);
        world.playSound(
                location,
                Sound.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE,
                1.0f, // 音量
                1.5f  // ピッチ
        );

        Particle.DustOptions aqua = new Particle.DustOptions(
                Color.fromRGB(100, 220, 255),
                1.2f
        );

        Particle.DustOptions white = new Particle.DustOptions(
                Color.fromRGB(255, 255, 255),
                1.0f
        );

        world.spawnParticle(Particle.DUST, location, 12, 0.35, 0.1, 0.35, 0, aqua);
        world.spawnParticle(Particle.DUST, location, 8, 0.35, 0.1, 0.35, 0, white);
    }

    private void healParticle(LivingEntity vic,Player player){
        World world = player.getWorld();
        if (!(world == vic.getWorld()))return;
        Location start = vic.getLocation().add(0, 0.5, 0);
        Location end = player.getLocation().add(0, 1, 0);

        Vector direction = end.toVector().subtract(start.toVector());

        int duration = 15;

        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (tick >= duration || !player.isOnline()) {
                    cancel();
                    return;
                }

                double progress = (double) tick / duration;

                Location current = start.clone().add(
                        direction.clone().multiply(progress)
                );

                Particle.DustOptions aqua = new Particle.DustOptions(
                        Color.fromRGB(100, 220, 255),
                        1.2f
                );

                world.playSound(
                        start,
                        Sound.BLOCK_CONDUIT_ACTIVATE,
                        1.0f, // 音量
                        1.5f  // ピッチ
                );
                world.spawnParticle(
                        Particle.DUST,
                        current,
                        5,
                        0.08, 0.08, 0.08,
                        0,
                        aqua
                );

                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
