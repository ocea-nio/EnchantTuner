package io.github.oceanio.enchanttuner.feature.banenchant;

import io.github.oceanio.enchanttuner.core.Feature;
import org.bukkit.plugin.java.JavaPlugin;

public class BanEnchantFeature implements Feature {
    private final BanEnchantService service;
    private BanEnchantListener listener;
    private BanEnchantCommand command;

    public BanEnchantFeature(BanEnchantService service ){
        this.service = service;
    }

    @Override
    public String getName(){
        return "BanEnchant";
    }

    @Override
    public void enable(JavaPlugin plugin){
        //DI
        this.listener = new BanEnchantListener(service);
        this.command = new BanEnchantCommand(service);

        //イベント登録
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

        // コマンド登録
        plugin.getCommand("ban_enchant").setExecutor(command);

    }

    @Override
    public void disable() {
        org.bukkit.event.HandlerList.unregisterAll(listener);
    }

}
