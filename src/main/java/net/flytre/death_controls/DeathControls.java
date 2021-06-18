package net.flytre.death_controls;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.flytre.death_controls.compat.TrinketsCompat;
import net.flytre.death_controls.config.Config;
import net.flytre.flytre_lib.config.ConfigHandler;
import net.flytre.flytre_lib.config.ConfigRegistry;

public class DeathControls implements ModInitializer {

    public static final ConfigHandler<Config> CONFIG = new ConfigHandler<>(new Config(), "death_controls");


    @Override
    public void onInitialize() {

        if (FabricLoader.getInstance().isModLoaded("trinkets"))
            TrinketsCompat.register();

        ConfigRegistry.registerServerConfig(CONFIG);
        CONFIG.handle();
    }
}
