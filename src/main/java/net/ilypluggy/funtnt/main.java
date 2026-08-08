package net.ilypluggy.funtnt;

import net.ilypluggy.funtnt.command.CustomTntCommand;
import net.ilypluggy.funtnt.config.ConfigManager;
import net.ilypluggy.funtnt.listener.BlockPlaceListener;
import net.ilypluggy.funtnt.listener.DamageListener;
import net.ilypluggy.funtnt.listener.EntityExplodeListener;
import net.ilypluggy.funtnt.listener.TntActivationListener;
import net.ilypluggy.funtnt.util.Keys;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        Keys.init(this);
        configManager = new ConfigManager(this);
        configManager.reload();
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager().registerEvents(new TntActivationListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new EntityExplodeListener(this, configManager), this);
        getServer().getPluginManager().registerEvents(new DamageListener(configManager), this);
        PluginCommand cmd = getCommand("customtnt");
        if (cmd != null) {
            CustomTntCommand handler = new CustomTntCommand(configManager);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        } else {
            getLogger().severe("Команда 'customtnt' не найдена в plugin.yml — что-то сломалось.");
        }

        getLogger().info("FunTNT включён. Загружено типов: " + configManager.all().size());
    }

    @Override
    public void onDisable() {
        getLogger().info("FunTNT выключен.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
