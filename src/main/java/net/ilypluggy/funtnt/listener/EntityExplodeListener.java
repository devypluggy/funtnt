package net.ilypluggy.funtnt.listener;

import net.ilypluggy.funtnt.config.ConfigManager;
import net.ilypluggy.funtnt.main;
import net.ilypluggy.funtnt.model.CustomTntType;
import net.ilypluggy.funtnt.util.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


public final class EntityExplodeListener implements Listener {

    private static final float VANILLA_TNT_YIELD = 4.0f;
    private static final float MAX_YIELD = 20.0f;
    private static final float MIN_YIELD = 0.1f;

    private final main plugin;
    private final ConfigManager config;

    public EntityExplodeListener(main plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrime(ExplosionPrimeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed)) return;

        String id = entity.getPersistentDataContainer().get(Keys.TNT_ID, PersistentDataType.STRING);
        if (id == null) return;

        CustomTntType type = config.get(id);
        if (type == null) return;

        double mul = type.getPowerMultiplier();
        if (Math.abs(mul - 1.0) < 0.001) return;

        float base = event.getRadius();
        if (base <= 0f) base = VANILLA_TNT_YIELD;

        float newRadius = (float) (base * mul);
        if (newRadius > MAX_YIELD) newRadius = MAX_YIELD;
        if (newRadius < MIN_YIELD) newRadius = MIN_YIELD;

        event.setRadius(newRadius);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed)) return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        String id = pdc.get(Keys.TNT_ID, PersistentDataType.STRING);
        if (id == null) return;

        CustomTntType type = config.get(id);
        if (type == null) return;

        if (!type.canBreakObsidian()) return;

        Location center = entity.getLocation();
        World world = center.getWorld();
        if (world == null) return;

        double radius = VANILLA_TNT_YIELD * type.getPowerMultiplier();
        if (radius > MAX_YIELD) radius = MAX_YIELD;
        double breakRadius = radius * 1.3;
        double breakRadiusSq = breakRadius * breakRadius;

        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        int r = (int) Math.ceil(breakRadius);

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double distSq = dx * dx + dy * dy + dz * dz;
                    if (distSq > breakRadiusSq) continue;

                    Block block = world.getBlockAt(cx + dx, cy + dy, cz + dz);
                    Material m = block.getType();
                    if (!isObsidianLike(m)) continue;
                    if (event.blockList().contains(block)) continue;

                    event.blockList().add(block);
                }
            }
        }
    }

    private static boolean isObsidianLike(Material m) {
        if (m == Material.OBSIDIAN) return true;
        Material crying = Material.getMaterial("CRYING_OBSIDIAN");
        if (crying != null && m == crying) return true;
        Material anchor = Material.getMaterial("RESPAWN_ANCHOR");
        if (anchor != null && m == anchor) return true;
        return false;
    }
}
