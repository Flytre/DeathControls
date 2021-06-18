package net.flytre.death_controls.compat;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.flytre.death_controls.DeathControls;
import net.flytre.death_controls.config.InventoryModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrinketsCompat {

    private static final Map<PlayerEntity, Map<String, Map<String, TrinketInventory>>> SAVED = new HashMap<>();

    public static void register() {
        Compat.PRE_PROCESS.add(TrinketsCompat::preProcess);
        Compat.POST_PROCESS.add(TrinketsCompat::postProcess);
    }

    public static void preProcess(ServerPlayerEntity entity) {
        boolean keepAll = DeathControls.CONFIG.getConfig().getInventoryModule().getSelectiveInventory().shouldKeepAllTrinkets();
        Set<String> slots = DeathControls.CONFIG.getConfig().getInventoryModule().getSelectiveInventory().getTrinketSlots();
        Map<String, Map<String, TrinketInventory>> saved = new HashMap<>();
        InventoryModule.Lists listCfg = DeathControls.CONFIG.getConfig().getInventoryModule().getLists();
        TrinketComponent component = TrinketsApi.getTrinketComponent(entity).orElse(null);

        if (component != null)
            if (keepAll) {
                component.getInventory().forEach((key, map) -> {
                    saved.put(key, new HashMap<>());
                    map.forEach((innerKey, val) -> saved.get(key).put(innerKey, val));
                });
            } else {
                component.getInventory().forEach((key, map) -> {
                    var iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        var entry = iter.next();
                        if (slots.contains(key + "." + entry.getKey()) || listCfg.contains(listCfg.getAlwaysKeep(), entry.getValue().getStack(0))) {
                            saved.putIfAbsent(key, new HashMap<>());
                            saved.get(key).put(entry.getKey(), entry.getValue());
                            iter.remove();
                        }
                    }
                });
            }
        SAVED.put(entity, saved);
    }

    public static void postProcess(ServerPlayerEntity entity) {
        TrinketComponent component = TrinketsApi.getTrinketComponent(entity).orElse(null);
        var saved = SAVED.get(entity);
        if (component != null)
            saved.forEach((key, map) -> map.forEach((innerKey, val) -> {
                component.getInventory().putIfAbsent(key, new HashMap<>());
                component.getInventory().get(key).put(innerKey, val);
            }));
    }
}
