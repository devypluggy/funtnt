package net.ilypluggy.funtnt.listener;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import net.ilypluggy.funtnt.config.ConfigManager;
import net.ilypluggy.funtnt.main;
import net.ilypluggy.funtnt.model.CustomTntType;
import net.ilypluggy.funtnt.util.Keys;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class TntActivationListener implements Listener {

    private final main plugin;
    private final ConfigManager config;

    public TntActivationListener(main plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrime(TNTPrimeEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.TNT) return;

        String id = readAndClearBlockTag(block);
        if (id == null) return;

        CustomTntType type = config.get(id);
        if (type == null) {
            return;
        }
        event.setCancelled(true);
        block.setType(Material.AIR, false);

        Location spawnLoc = block.getLocation().add(0.5, 0.0, 0.5);
        TNTPrimed primed = block.getWorld().spawn(spawnLoc, TNTPrimed.class, tnt -> {
            tnt.setFuseTicks(type.getFuseTicks());
            PersistentDataContainer p = tnt.getPersistentDataContainer();
            p.set(Keys.TNT_ID, PersistentDataType.STRING, type.getId());
        });
        if (primed == null) {
            plugin.getLogger().warning("Не удалось заспавнить TNTPrimed для id=" + id);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.TNT) return;
        readAndClearBlockTag(block);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        cleanExplodedBlocks(event.blockList());
    }

    void cleanExplodedBlocks(java.util.List<Block> blocks) {
        if (blocks == null || blocks.isEmpty()) return;
        for (Block b : blocks) {
            if (b.getType() == Material.TNT) {
                readAndClearBlockTag(b);
            }
        }
    }

    private String readAndClearBlockTag(Block block) {
        Chunk chunk = block.getChunk();
        int localX = block.getX() & 15;
        int y      = block.getY();
        int localZ = block.getZ() & 15;

        NamespacedKey key = Keys.blockKey(plugin, localX, y, localZ);
        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        String id = pdc.get(key, PersistentDataType.STRING);
        if (id != null) {
            pdc.remove(key);
        }
        return id;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.TNT) return;
    }
}
