package io.github.oceanio.enchanttuner.feature.banenchant;

import io.github.oceanio.enchanttuner.core.Feature;
import io.github.oceanio.enchanttuner.core.YamlManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class BanEnchantFeature implements Feature {
    private  BanEnchantService service;
    private BanEnchantListener listener;
    private BanEnchantCommand command;
    private YamlManager config;

    @Override
    public String getName(){
        return "BanEnchant";
    }

    @Override
    public void enable(JavaPlugin plugin){
        //config
        this.config = new YamlManager(plugin, "ban-enchant");

        //service
        this.service = new BanEnchantService();
        this.service.load(config);

        //listener
        this.listener = new BanEnchantListener(service);
        this.command = new BanEnchantCommand(service);

        //イベント登録
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        // コマンド登録
        PluginCommand pluginCommand = plugin.getCommand("ban_enchant");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
        }

    }

    @Override
    public void disable() {
        org.bukkit.event.HandlerList.unregisterAll(listener);
    }

}
