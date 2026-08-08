package net.ilypluggy.funtnt.util;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class ColorUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([0-9A-Fa-f]{6})");
    private static final boolean HEX_SUPPORTED;

    static {
        boolean supported;
        try {
            Class<?> bungeeColor = Class.forName("net.md_5.bungee.api.ChatColor");
            bungeeColor.getMethod("of", String.class);
            supported = true;
        } catch (Throwable ignored) {
            supported = false;
        }
        HEX_SUPPORTED = supported;
    }

    private ColorUtil() {}

    public static String colorize(String input) {
        if (input == null || input.isEmpty()) {
            return input == null ? "" : input;
        }

        String result = input;
        if (HEX_SUPPORTED && result.indexOf('&') != -1) {
            Matcher matcher = HEX_PATTERN.matcher(result);
            if (matcher.find()) {
                StringBuffer buffer = new StringBuffer(result.length());
                matcher.reset();
                while (matcher.find()) {
                    String hex = matcher.group(1);
                    String replacement = net.md_5.bungee.api.ChatColor.of("#" + hex).toString();
                    matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
                }
                matcher.appendTail(buffer);
                result = buffer.toString();
            }
        }

        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public static List<String> colorize(List<String> input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> out = new ArrayList<>(input.size());
        for (String line : input) {
            out.add(colorize(line));
        }
        return out;
    }
}
