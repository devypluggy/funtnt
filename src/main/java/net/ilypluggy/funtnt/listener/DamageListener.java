package net.ilypluggy.funtnt.listener;

import net.ilypluggy.funtnt.config.ConfigManager;
import net.ilypluggy.funtnt.model.CustomTntType;
import net.ilypluggy.funtnt.util.Keys;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class DamageListener implements Listener {

    private final ConfigManager config;

    public DamageListener(ConfigManager config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            return;
        }

        Entity damager = event.getDamager();
        if (!(damager instanceof TNTPrimed)) return;

        PersistentDataContainer pdc = damager.getPersistentDataContainer();
        String id = pdc.get(Keys.TNT_ID, PersistentDataType.STRING);
        if (id == null) return;

        CustomTntType type = config.get(id);
        if (type == null) return;

        double mul = type.getDamageMultiplier();
        if (Math.abs(mul - 1.0) < 0.001) return;

        double base = event.getDamage();
        event.setDamage(base * mul);
    }
}
