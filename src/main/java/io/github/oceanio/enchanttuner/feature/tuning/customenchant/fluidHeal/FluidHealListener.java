package io.github.oceanio.enchanttuner.feature.tuning.customenchant.fluidHeal;

import io.github.oceanio.enchanttuner.feature.tuning.TuningKeys;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

public class FluidHealListener implements CustomEnchant {
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

    @Override
    public void onTrigger(ItemStack configuredItem, int level, Event triggerEvent, java.util.Map<String, Object> args) {
        if (triggerEvent instanceof EntityDamageByEntityEvent attack) {
            playAttackEffect(attack);
        } else if (triggerEvent instanceof EntityDeathEvent death) {
            onKilled(death, level, args);
        }
    }

    private void onKilled(EntityDeathEvent event, int enchantLevel, java.util.Map<String, Object> args){
        LivingEntity victim = event.getEntity();

        Player player = victim.getKiller();
        if (player == null) return;

        ItemStack weapon = player.getInventory().getItemInMainHand();

        if (!hasTuning(weapon)) return;

        if (enchantLevel < 1) return;

        healParticle(victim,player);

        int durationSeconds = integerArg(args, "duration-seconds", 5);
        int amplifier = integerArg(args, "amplifier", enchantLevel - 1, enchantLevel);
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.ABSORPTION,
                20 * Math.max(1, durationSeconds),
                Math.max(0, amplifier)
        ));
    }

    private void playAttackEffect(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player player)) return;
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

    private int integerArg(java.util.Map<String, Object> args, String key, int fallback) {
        return integerArg(args, key, fallback, fallback + 1);
    }

    private int integerArg(java.util.Map<String, Object> args, String key, int fallback, int level) {
        Object value = args.get(key);
        if (value instanceof Number number) return number.intValue();
        if (!(value instanceof String expression)) return fallback;
        String normalized = expression.replace("%level%", Integer.toString(level)).replace(" ", "");
        try {
            int minus = normalized.indexOf('-', 1);
            if (minus > 0) return Integer.parseInt(normalized.substring(0, minus)) - Integer.parseInt(normalized.substring(minus + 1));
            int plus = normalized.indexOf('+', 1);
            if (plus > 0) return Integer.parseInt(normalized.substring(0, plus)) + Integer.parseInt(normalized.substring(plus + 1));
            return Integer.parseInt(normalized);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
