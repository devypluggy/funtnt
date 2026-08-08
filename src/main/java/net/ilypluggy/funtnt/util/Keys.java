package net.ilypluggy.funtnt.util;

import net.ilypluggy.funtnt.main;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class Keys {
    public static NamespacedKey TNT_ID;
    public static NamespacedKey CHUNK_BLOCK_PREFIX;

    private Keys() {}

    public static void init(JavaPlugin plugin) {
        TNT_ID = new NamespacedKey(plugin, "custom_tnt_id");
        CHUNK_BLOCK_PREFIX = new NamespacedKey(plugin, "custom_tnt_block");
    }

    public static NamespacedKey blockKey(JavaPlugin plugin, int localX, int y, int localZ) {
        return new NamespacedKey(plugin, "ctnt_" + localX + "_" + y + "_" + localZ);
    }
}
