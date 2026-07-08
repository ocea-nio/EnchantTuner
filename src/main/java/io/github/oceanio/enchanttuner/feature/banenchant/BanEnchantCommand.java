package io.github.oceanio.enchanttuner.feature.banenchant;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BanEnchantCommand implements  CommandExecutor {
    private final BanEnchantService service;

    public BanEnchantCommand(BanEnchantService service) {
        this.service = service;
    }

    @Override
    public  boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!sender.hasPermission("banenchant_use")) {
            sender.sendMessage("権限がありません");
            return true;
        }
        if (args.length < 1){
            sender.sendMessage(
                    "/ban_enchant inventory"
            );
            return true;
        }

        if (args[0].equalsIgnoreCase("inventory")){
            service.inventoryRemove();
            sender.sendMessage("removed");
            return true;
        } else {
            sender.sendMessage(
                    "/ban_enchant inventory"
            );
            return true;
        }
    }
}
