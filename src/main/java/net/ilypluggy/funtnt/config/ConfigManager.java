package net.ilypluggy.funtnt.config;

import net.ilypluggy.funtnt.model.CustomTntType;
import net.ilypluggy.funtnt.util.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

/**
 * Формат:
 *
 * tnts:
 *   nuke:
 *     name: "&c&lЯдерка"
 *     lore:
 *       - "&7Взрывается сильнее"
 *     power-multiplier: 3.0
 *     damage-multiplier: 2.5
 *     break-obsidian: true
 *     fuse-seconds: 3.5
 */
public final class ConfigManager {

    private final JavaPlugin plugin;
    private final Map<String, CustomTntType> types = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        types.clear();

        ConfigurationSection root = plugin.getConfig().getConfigurationSection("tnts");
        if (root == null) {
            plugin.getLogger().warning("Секция 'tnts:' отсутствует в config.yml — плагин не будет иметь ни одного TNT.");
            return;
        }

        for (String key : root.getKeys(false)) {
            ConfigurationSection sec = root.getConfigurationSection(key);
            if (sec == null) continue;

            String id = key.toLowerCase(Locale.ROOT);

            String name = ColorUtil.colorize(sec.getString("name", "&fCustom TNT"));

            List<String> loreRaw = sec.getStringList("lore");
            List<String> lore = loreRaw.isEmpty() ? Collections.emptyList() : ColorUtil.colorize(loreRaw);

            double power  = sec.getDouble("power-multiplier", 1.0);
            double damage = sec.getDouble("damage-multiplier", 1.0);
            boolean obs   = sec.getBoolean("break-obsidian", false);
            double fuseSec = sec.getDouble("fuse-seconds", 4.0);

            if (power  < 0.1) power  = 0.1;
            if (power  > 20)  power  = 20;
            if (damage < 0)   damage = 0;
            if (damage > 100) damage = 100;
            if (fuseSec < 0.05) fuseSec = 0.05;
            int fuseTicks = (int) Math.round(fuseSec * 20.0);
            if (fuseTicks < 1) fuseTicks = 1;

            CustomTntType type = new CustomTntType(id, name, lore, power, damage, obs, fuseTicks);
            types.put(id, type);
        }

        plugin.getLogger().log(Level.INFO, "Загружено типов кастомного TNT: " + types.size());
    }

    public CustomTntType get(String id) {
        if (id == null) return null;
        return types.get(id.toLowerCase(Locale.ROOT));
    }

    public Map<String, CustomTntType> all() {
        return Collections.unmodifiableMap(types);
    }
}
