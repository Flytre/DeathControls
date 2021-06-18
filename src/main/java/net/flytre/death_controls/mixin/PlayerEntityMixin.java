package net.flytre.death_controls.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {


    @Redirect(method="dropInventory", at = @At(value = "INVOKE",target = "Lnet/minecraft/world/GameRules;getBoolean(Lnet/minecraft/world/GameRules$Key;)Z"))
    public boolean death_controls$dropInvCanceller(GameRules gameRules, GameRules.Key<GameRules.BooleanRule> rule) {
        return true;
    }
}
