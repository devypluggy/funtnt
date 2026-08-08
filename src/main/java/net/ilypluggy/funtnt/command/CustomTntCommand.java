package net.ilypluggy.funtnt.command;

import net.ilypluggy.funtnt.config.ConfigManager;
import net.ilypluggy.funtnt.model.CustomTntType;
import net.ilypluggy.funtnt.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CustomTntCommand implements CommandExecutor, TabCompleter {

    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.RED + "FunTNT" + ChatColor.GRAY + "] " + ChatColor.RESET;
    private static final String PERM = "funtnt.admin";

    private final ConfigManager config;

    public CustomTntCommand(ConfigManager config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERM)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Нет прав.");
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "give":   return handleGive(sender, args);
            case "list":   return handleList(sender);
            case "reload": return handleReload(sender);
            default:
                sendHelp(sender, label);
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        // /customtnt give <player> <id> [count]
        if (args.length < 3) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Использование: /customtnt give <ник> <id> [кол-во]");
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Игрок '" + args[1] + "' не найден или не в сети.");
            return true;
        }
        CustomTntType type = config.get(args[2]);
        if (type == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Тип TNT '" + args[2] + "' не найден. См. /customtnt list");
            return true;
        }
        int count = 1;
        if (args.length >= 4) {
            try {
                count = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(PREFIX + ChatColor.RED + "Кол-во должно быть числом.");
                return true;
            }
            if (count < 1)  count = 1;
            if (count > 64) count = 64;
        }

        ItemStack item = ItemFactory.create(type, count);
        var overflow = target.getInventory().addItem(item);
        if (!overflow.isEmpty()) {
            for (ItemStack drop : overflow.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), drop);
            }
        }

        sender.sendMessage(PREFIX + ChatColor.GREEN + "Выдано " + count + "× " + type.getDisplayName()
                + ChatColor.GREEN + " игроку " + target.getName());
        target.sendMessage(PREFIX + ChatColor.GREEN + "Вы получили " + count + "× " + type.getDisplayName());
        return true;
    }

    private boolean handleList(CommandSender sender) {
        var all = config.all();
        if (all.isEmpty()) {
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "Нет ни одного типа TNT в config.yml");
            return true;
        }
        sender.sendMessage(PREFIX + ChatColor.GOLD + "Загружено типов: " + all.size());
        for (CustomTntType t : all.values()) {
            sender.sendMessage(ChatColor.GRAY + " • " + ChatColor.WHITE + t.getId()
                    + ChatColor.GRAY + " → " + t.getDisplayName()
                    + ChatColor.GRAY + " (power×" + t.getPowerMultiplier()
                    + ", dmg×" + t.getDamageMultiplier()
                    + ", fuse=" + (t.getFuseTicks() / 20.0) + "s"
                    + ", obs=" + t.canBreakObsidian() + ")");
        }
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        try {
            config.reload();
            sender.sendMessage(PREFIX + ChatColor.GREEN + "Конфиг перезагружен. Типов: " + config.all().size());
        } catch (Throwable t) {
            sender.sendMessage(PREFIX + ChatColor.RED + "Ошибка перезагрузки: " + t.getMessage());
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(PREFIX + ChatColor.GOLD + "Команды:");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " give <ник> <id> [кол-во]" + ChatColor.GRAY + " — выдать TNT");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " list" + ChatColor.GRAY + " — список типов");
        sender.sendMessage(ChatColor.YELLOW + "/" + label + " reload" + ChatColor.GRAY + " — перезагрузить конфиг");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission(PERM)) return Collections.emptyList();

        if (args.length == 1) {
            return filter(List.of("give", "list", "reload"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return filter(names, args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return filter(new ArrayList<>(config.all().keySet()), args[2]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            return filter(List.of("1", "8", "16", "32", "64"), args[3]);
        }
        return Collections.emptyList();
    }

    private static List<String> filter(List<String> src, String prefix) {
        if (prefix == null || prefix.isEmpty()) return src;
        String low = prefix.toLowerCase();
        List<String> out = new ArrayList<>();
        for (String s : src) if (s.toLowerCase().startsWith(low)) out.add(s);
        return out;
    }
}
