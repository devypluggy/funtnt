package net.ilypluggy.funtnt.util;

import net.ilypluggy.funtnt.model.CustomTntType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public final class ItemFactory {

    private ItemFactory() {}

    public static ItemStack create(CustomTntType type, int amount) {
        ItemStack item = new ItemStack(Material.TNT, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(type.getDisplayName());
        if (!type.getLore().isEmpty()) {
            meta.setLore(new ArrayList<>(type.getLore()));
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(Keys.TNT_ID, PersistentDataType.STRING, type.getId());

        item.setItemMeta(meta);
        return item;
    }

    public static String extractId(ItemStack item) {
        if (item == null || item.getType() != Material.TNT) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NamespacedKey key = Keys.TNT_ID;
        if (key == null) return null;
        return pdc.get(key, PersistentDataType.STRING);
    }
}
