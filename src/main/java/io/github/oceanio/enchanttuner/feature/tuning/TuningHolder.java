package io.github.oceanio.enchanttuner.feature.tuning;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;


import java.net.URI;
import java.util.UUID;



public class TuningHolder implements InventoryHolder {
    Inventory inv;
    public  TuningHolder() {
        this.inv = Bukkit.createInventory(this, 45, "エンチャント調整");
        setup();
    }

    private void setup(){
        //FrameGen
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.displayName(Component.text(""));
        border.setItemMeta(borderMeta);

        ItemStack frame = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta frameMeta = frame.getItemMeta();
        frameMeta.displayName(Component.text(""));
        frame.setItemMeta(frameMeta);

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.displayName(Component.text("§l§a決定"));

        PlayerProfile profile = (PlayerProfile) Bukkit.createPlayerProfile(UUID.randomUUID());

        PlayerTextures textures = profile.getTextures();

        try {
            textures.setSkin(
                    URI.create(
                            "https://textures.minecraft.net/texture/6e42cc14b12e1515cde506f7bc35463b72c4f8a939f48ef1eef6e7e75d86fc0b"
                    ).toURL()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        profile.setTextures(textures);
        headMeta.setPlayerProfile(profile);
        head.setItemMeta(headMeta);

        //FrameSetup
        int[] BORDER_SLOTS = {
                0,1,2,3,4,5,6,7,8,9,13,14,15,16,17,18,22,24,26,27,31,32,33,34,35,36,37,38,39,40,41,42,43,44
        };
        int[] FRAME_SLOT = {10,11,12,19,21,28,29,30};


        for (int slot : BORDER_SLOTS) {
            inv.setItem(slot, border);
        }
        for (int slot : FRAME_SLOT) {
            inv.setItem(slot, frame);
        }
        inv.setItem(25,head);

    }


    @Override
    public Inventory getInventory() {
        return inv;
    }
}
