package io.github.oceanio.enchanttuner.feature.tuning;

import io.github.oceanio.enchanttuner.core.Feature;
import io.github.oceanio.enchanttuner.core.YamlManager;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.delicate.DelicateListener;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.durability.DurabilityService;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.fluidHeal.FluidHealListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class TuningFeature implements Feature {
    private  TuningService service;
    private TuningListener listener;

    //Enchant
    private DelicateListener delicate;
    private DurabilityService durability;
    private FluidHealListener fluidHeal;

    //Yaml
    private YamlManager enchantPool;
    private YamlManager enchants;
    private YamlManager banEnchant;



    @Override
    public String getName() {
        return "Tuning";
    }

    @Override
    public void enable(JavaPlugin plugin) {
        //pdc
        TuningKeys.init(plugin);
        //yaml
        this.enchantPool = new YamlManager(plugin, "EnchantPool");
        this.enchants = new YamlManager(plugin, "enchants");
        //enchantPool
        EnchantPool pool = new EnchantPool(this.enchants,this.enchantPool);

        //==+==Enchant Register==+==
        this.delicate = new DelicateListener();
        this.durability = new DurabilityService();
        this.fluidHeal = new FluidHealListener(plugin);
        Map<NamespacedKey, CustomEnchant> customEnchants = Map.ofEntries(
                Map.entry(new NamespacedKey(plugin, "delicate"), delicate),
                Map.entry(new NamespacedKey(plugin, "durability"), durability),
                Map.entry(new NamespacedKey(plugin, "fluid_heal"), fluidHeal)
        );

        //service
        this.service = new TuningService(pool,plugin, customEnchants);
        this.listener = new TuningListener(plugin, service, customEnchants);

        //register
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getServer().getPluginManager().registerEvents(delicate, plugin);
        plugin.getServer().getPluginManager().registerEvents(fluidHeal, plugin);
        Bukkit.getLogger().info(
                "FluidHeal instance: " + fluidHeal
        );
    }

    @Override
    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
        if (delicate != null) {
            HandlerList.unregisterAll(delicate);
        }
        if (fluidHeal != null) {
            HandlerList.unregisterAll(fluidHeal);
        }
    }
}
