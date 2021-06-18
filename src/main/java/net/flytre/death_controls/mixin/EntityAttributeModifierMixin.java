package net.flytre.death_controls.mixin;

import net.flytre.death_controls.DeathAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(EntityAttributeModifier.class)
public class EntityAttributeModifierMixin {


    @Inject(method = "fromNbt", at = @At("HEAD"), cancellable = true)
    private static void death_controls$deathModifierFromNbt(NbtCompound nbt, CallbackInfoReturnable<EntityAttributeModifier> cir) {
        try {
            if (!nbt.contains("gradual"))
                return;
            UUID uuid = nbt.getUuid("UUID");
            EntityAttributeModifier.Operation operation = EntityAttributeModifier.Operation.fromId(nbt.getInt("Operation"));
            cir.setReturnValue(new DeathAttributeModifier(uuid, nbt.getString("Name"), nbt.getDouble("Amount"), operation, nbt.getInt("maxDuration"), nbt.getBoolean("gradual"), nbt.getInt("duration")));
        } catch (Exception ignored) {
        }
    }
}
