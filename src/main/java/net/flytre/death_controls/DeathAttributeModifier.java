package net.flytre.death_controls;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DeathAttributeModifier extends EntityAttributeModifier {

    private final boolean gradual;
    private final int maxDuration;
    private final double value;
    private int duration;

    public DeathAttributeModifier(String name, double value, Operation operation, int maxDuration, boolean gradual) {
        super(name, value, operation);
        this.maxDuration = this.duration = maxDuration;
        this.gradual = gradual;
        this.value = value;
    }

    public DeathAttributeModifier(UUID uuid, String name, double value, Operation operation, int maxDuration, boolean gradual, int duration) {
        super(uuid, name, value, operation);
        this.maxDuration = maxDuration;
        this.gradual = gradual;
        this.value = value;
        this.duration = duration;
    }

    public boolean isGradual() {
        return gradual;
    }

    @Override
    public double getValue() {
        return gradual ? value * ((double) duration / maxDuration) : value;
    }

    //Return true if should remove
    public @Nullable DeathAttributeModifier tick() {
        duration--;
        return duration > 0 ? new DeathAttributeModifier(getId(), getName(), super.getValue(), getOperation(), maxDuration, gradual, duration) : null;
    }

    @Override
    public NbtCompound toNbt() {
        NbtCompound compound = super.toNbt();
        compound.putBoolean("gradual", gradual);
        compound.putInt("duration", duration);
        compound.putInt("maxDuration", maxDuration);
        return compound;
    }
}
