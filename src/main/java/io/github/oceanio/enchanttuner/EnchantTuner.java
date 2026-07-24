package io.github.oceanio.enchanttuner;

import io.github.oceanio.enchanttuner.core.FeatureManager;
import io.github.oceanio.enchanttuner.core.YamlManager;
import io.github.oceanio.enchanttuner.feature.banenchant.BanEnchantFeature;
import io.github.oceanio.enchanttuner.feature.banenchant.BanEnchantService;
import io.github.oceanio.enchanttuner.feature.tuning.TuningFeature;
import io.github.oceanio.enchanttuner.feature.tuning.TuningService;
import org.bukkit.plugin.java.JavaPlugin;


public final class EnchantTuner extends JavaPlugin {
    private FeatureManager featureManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        featureManager = new FeatureManager(this);

        //ここでfeature登録
        featureManager.register(new TuningFeature(new TuningService()));
        featureManager.register(new BanEnchantFeature(new BanEnchantService(new YamlManager(this,"config"))));

        //要素許可
        featureManager.enableAll();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
