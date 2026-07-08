package io.github.oceanio.enchanttuner.feature.tuning;

import io.github.oceanio.enchanttuner.core.Feature;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class TuningFeature implements Feature {
    private final TuningService service;
    private TuningListener listener;

    public TuningFeature(TuningService service) {
        this.service = service;
    }

    @Override
    public String getName() {
        return "Tuning";
    }

    @Override
    public void enable(JavaPlugin plugin) {
        // 引数なしのコンストラクタを呼び出す

        this.listener = new TuningListener(plugin, service);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void disable() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }
}