package io.github.oceanio.enchanttuner.feature.tuning;

import io.github.oceanio.enchanttuner.core.Feature;
import io.github.oceanio.enchanttuner.core.YamlManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class TuningFeature implements Feature {
    private  TuningService service;
    private TuningListener listener;
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
        //service
        this.service = new TuningService(pool,plugin);

        this.listener = new TuningListener(plugin, service);

        //register
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}