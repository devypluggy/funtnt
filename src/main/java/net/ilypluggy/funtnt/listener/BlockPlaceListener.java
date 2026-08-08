package net.ilypluggy.funtnt.listener;

import net.ilypluggy.funtnt.main;
import net.ilypluggy.funtnt.util.ItemFactory;
import net.ilypluggy.funtnt.util.Keys;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class BlockPlaceListener implements Listener {

    private final main plugin;

    public BlockPlaceListener(main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack inHand = event.getItemInHand();
        if (inHand == null || inHand.getType() != Material.TNT) return;

        String id = ItemFactory.extractId(inHand);
        if (id == null) return;

        Block block = event.getBlockPlaced();
        Chunk chunk = block.getChunk();

        int localX = block.getX() & 15;
        int y      = block.getY();
        int localZ = block.getZ() & 15;

        PersistentDataContainer pdc = chunk.getPersistentDataContainer();
        pdc.set(Keys.blockKey(plugin, localX, y, localZ), PersistentDataType.STRING, id);
    }
}
