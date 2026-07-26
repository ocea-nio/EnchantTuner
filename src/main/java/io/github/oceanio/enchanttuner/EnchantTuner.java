package io.github.oceanio.enchanttuner;

import io.github.oceanio.enchanttuner.core.FeatureManager;
import io.github.oceanio.enchanttuner.feature.banenchant.BanEnchantFeature;
import io.github.oceanio.enchanttuner.feature.tuning.TuningFeature;
import org.bukkit.plugin.java.JavaPlugin;


public final class EnchantTuner extends JavaPlugin {
    private FeatureManager featureManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        featureManager = new FeatureManager(this);

        //ここでfeature登録
        featureManager.register(new TuningFeature());
        featureManager.register(new BanEnchantFeature());
        //要素許可
        featureManager.enableAll();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
