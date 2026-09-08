package io.github.oceanio.enchanttuner.feature.tuning;

import io.github.oceanio.enchanttuner.core.Feature;
import io.github.oceanio.enchanttuner.core.YamlManager;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.CustomEnchant;
import io.github.oceanio.enchanttuner.feature.tuning.customenchant.delicate.DelicateListener;
import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class TuningFeature implements Feature {
    private  TuningService service;
    private TuningListener listener;
    private DelicateListener delicateListener;
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

        this.delicateListener = new DelicateListener();
        Map<NamespacedKey, CustomEnchant> customEnchants = Map.of(
                new NamespacedKey(plugin, "delicate"), delicateListener
        );
        //service
        this.service = new TuningService(pool,plugin, customEnchants);
        this.listener = new TuningListener(plugin, service, customEnchants);

        //register
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getServer().getPluginManager().registerEvents(delicateListener, plugin);
    }

    @Override
    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
        if (delicateListener != null) {
            HandlerList.unregisterAll(delicateListener);
        }
    }
}
