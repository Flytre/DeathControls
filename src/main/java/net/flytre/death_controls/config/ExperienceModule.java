package net.flytre.death_controls.config;

import com.google.gson.annotations.SerializedName;

public class ExperienceModule {

    @SuppressWarnings("unused")
    private final String _comment = "Recoverable xp percent is the percentage of dropped xp the orbs contain. I.e. for 0.25, the orbs would contain 25 xp for every 100 xp dropped.";

    @SerializedName("dropped_xp_percent")
    private final double droppedXpPercent;

    @SerializedName("recoverable_xp_percent")
    private final double recoverableXpPercent;

    @SerializedName("maximum_recoverable_xp")
    private final double maxRecoverableXp;

    @SerializedName("use_vanilla_formula")
    private final boolean useVanillaFormula;

    public ExperienceModule() {
        droppedXpPercent = 0.25;
        recoverableXpPercent = 0.25;
        maxRecoverableXp = 100;
        useVanillaFormula = true;
    }

    public double getDroppedXpPercent() {
        return droppedXpPercent;
    }

    public double getRecoverableXpPercent() {
        return recoverableXpPercent;
    }

    public double getMaxRecoverableXp() {
        return maxRecoverableXp;
    }

    public boolean shouldUseVanillaFormula() {
        return useVanillaFormula;
    }
}
