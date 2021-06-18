package net.flytre.death_controls.compat;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Compat {


    public static final List<Consumer<ServerPlayerEntity>> PRE_PROCESS = new ArrayList<>();
    public static final List<Consumer<ServerPlayerEntity>> POST_PROCESS = new ArrayList<>();
}
