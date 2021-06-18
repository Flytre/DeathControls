package net.flytre.death_controls.config;

import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("inventory")
    private final InventoryModule inventoryModule;

    @SerializedName("effects")
    private final EffectModule effectModule;

    @SerializedName("experience")
    private final ExperienceModule experienceModule;


    public Config() {
        this.inventoryModule = new InventoryModule();
        this.effectModule = new EffectModule();
        this.experienceModule = new ExperienceModule();
    }

    public InventoryModule getInventoryModule() {
        return inventoryModule;
    }

    public EffectModule getEffectModule() {
        return effectModule;
    }

    public ExperienceModule getExperienceModule() {
        return experienceModule;
    }
}
