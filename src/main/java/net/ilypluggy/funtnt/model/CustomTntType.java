package net.ilypluggy.funtnt.model;

import java.util.Collections;
import java.util.List;

public final class CustomTntType {

    private final String id;
    private final String displayName;
    private final List<String> lore;
    private final double powerMultiplier; // default 4.0
    private final double damageMultiplier;
    private final boolean breakObsidian;
    private final int fuseTicks;

    public CustomTntType(String id,
                         String displayName,
                         List<String> lore,
                         double powerMultiplier,
                         double damageMultiplier,
                         boolean breakObsidian,
                         int fuseTicks) {
        this.id = id;
        this.displayName = displayName;
        this.lore = lore == null ? Collections.emptyList() : Collections.unmodifiableList(lore);
        this.powerMultiplier = powerMultiplier;
        this.damageMultiplier = damageMultiplier;
        this.breakObsidian = breakObsidian;
        this.fuseTicks = fuseTicks;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public List<String> getLore() { return lore; }
    public double getPowerMultiplier() { return powerMultiplier; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public boolean canBreakObsidian() { return breakObsidian; }
    public int getFuseTicks() { return fuseTicks; }
}
