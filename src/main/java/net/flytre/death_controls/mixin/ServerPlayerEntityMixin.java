package net.flytre.death_controls.mixin;


import com.mojang.authlib.GameProfile;
import net.flytre.death_controls.DeathAttributeModifier;
import net.flytre.death_controls.DeathControls;
import net.flytre.death_controls.compat.Compat;
import net.flytre.death_controls.config.EffectModule;
import net.flytre.death_controls.config.ExperienceModule;
import net.flytre.death_controls.config.InventoryModule;
import net.flytre.flytre_lib.common.util.EnchantmentUtils;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.minecraft.entity.attribute.EntityAttributes.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private static final List<EntityAttribute> ATTRIBUTES = List.of(GENERIC_MAX_HEALTH,
            GENERIC_FOLLOW_RANGE,
            GENERIC_KNOCKBACK_RESISTANCE,
            GENERIC_MOVEMENT_SPEED,
            GENERIC_FLYING_SPEED,
            GENERIC_ATTACK_DAMAGE,
            GENERIC_ATTACK_KNOCKBACK,
            GENERIC_ATTACK_SPEED,
            GENERIC_ARMOR,
            GENERIC_ARMOR_TOUGHNESS,
            GENERIC_LUCK);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    private static Set<Integer> keptSlots(PlayerInventory old) {
        Set<Integer> result = new HashSet<>();
        InventoryModule.SelectiveInventory cfg = DeathControls.CONFIG.getConfig().getInventoryModule().getSelectiveInventory();
        if (cfg.shouldKeepHotbar())
            IntStream.range(0, 9).forEach(result::add);
        if (cfg.shouldKeepArmor())
            IntStream.range(36, 40).forEach(result::add);
        if (cfg.shouldKeepOffhand())
            Stream.of(40).forEach(result::add);
        if (cfg.shouldKeepMainInventory())
            IntStream.range(9, 36).forEach(result::add);
        if (cfg.shouldKeepMainhand())
            Stream.of(old.selectedSlot).forEach(result::add);
        return result;
    }

    @Shadow
    public abstract boolean isSpectator();

    @Inject(method = "tick", at = @At("HEAD"))
    public void death_control$tickDeathModifiers(CallbackInfo ci) {

        for (EntityAttribute attribute : ATTRIBUTES) {
            var instance = getAttributes().getCustomInstance(attribute);
            if (instance == null)
                continue;
            for (var nx : instance.getModifiers())
                if (nx instanceof DeathAttributeModifier) {
                    var nx2 = ((DeathAttributeModifier) nx).tick();

                    if (nx2 == null || ((DeathAttributeModifier) nx).isGradual()) {
                        instance.removeModifier(nx);
                        if (nx2 != null)
                            instance.addTemporaryModifier(nx2);
                    }

                }
        }
    }

    @Inject(method = "copyFrom", at = @At(value = "TAIL"))
    public void death_control$inventory_copy(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {

        if (alive || this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
            return;

        InventoryModule.SelectiveInventory selectionModule = DeathControls.CONFIG.getConfig().getInventoryModule().getSelectiveInventory();
        InventoryModule.Loss lossModule = DeathControls.CONFIG.getConfig().getInventoryModule().getLoss();
        EffectModule.CustomEffect customEffect = DeathControls.CONFIG.getConfig().getEffectModule().getCustomEffect();
        EffectModule.RespawnEffect[] respawnEffects = DeathControls.CONFIG.getConfig().getEffectModule().getRespawnEffects();
        ExperienceModule experienceModule = DeathControls.CONFIG.getConfig().getExperienceModule();


        //Inventory
        for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
            ItemStack stack = oldPlayer.getInventory().getStack(i);
            boolean destroy = false;
            if (lossModule.keptDurabilityLoss() > 0 && stack.isDamageable())
                destroy = stack.damage((int) (stack.getMaxDamage() * lossModule.keptDurabilityLoss()), random, null);
            if (!destroy)
                getInventory().setStack(i, stack);
        }

        if (selectionModule.shouldKeepMainhand())
            getInventory().selectedSlot = oldPlayer.getInventory().selectedSlot;


        //Effects
        if (customEffect.getDuration() > 0)
            getAttributes().addTemporaryModifiers(customEffect.mapConfig());

        for (var re : respawnEffects) {
            addStatusEffect(new StatusEffectInstance(Registry.STATUS_EFFECT.get(Identifier.tryParse(re.getId())), re.getDuration(), re.getAmplifier(), false, false, true));
        }

        //Xp
        if (!experienceModule.shouldUseVanillaFormula()) {
            totalExperience = (int) (EnchantmentUtils.calculateTotalXp(oldPlayer) * (1 - experienceModule.getDroppedXpPercent()));
            experienceLevel = EnchantmentUtils.getExperienceLevel(totalExperience);
            experienceProgress = (totalExperience - EnchantmentUtils.getExperienceFromLevel(experienceLevel)) / (float) getNextLevelExperience();
            System.out.println(experienceProgress);
        }
    }


    /**
     * Override for Death Controls Mod
     *
     * @author Flytre
     */
    @Override
    protected void dropInventory() {

        InventoryModule.Random cfg = DeathControls.CONFIG.getConfig().getInventoryModule().getRandom();
        InventoryModule.Lists listCfg = DeathControls.CONFIG.getConfig().getInventoryModule().getLists();

        Compat.PRE_PROCESS.forEach(i -> i.accept((ServerPlayerEntity) (Object) this));

        super.dropInventory();

        Compat.POST_PROCESS.forEach(i -> i.accept((ServerPlayerEntity) (Object) this));

        if (!this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
            this.vanishCursedItems();
            PlayerInventory inv = getInventory();
            Set<Integer> keptSlots = keptSlots(inv);

            IntStream.range(0, inv.size()).filter(i ->
                    listCfg.contains(listCfg.getAlwaysDestroy(), inv.getStack(i)) ||
                            listCfg.contains(listCfg.getAlwaysDrop(), inv.getStack(i))
                            || (
                            !(listCfg.contains(listCfg.getAlwaysKeep(), inv.getStack(i))) &&
                                    (!keptSlots.contains(i)
                                            || Math.random() < cfg.getRandomDropChance()))).forEach(this::handleStackDrop);
        }
    }

    private void handleStackDrop(int i) {
        PlayerInventory inv = getInventory();
        InventoryModule.Loss lossModule = DeathControls.CONFIG.getConfig().getInventoryModule().getLoss();
        InventoryModule.Random randomModule = DeathControls.CONFIG.getConfig().getInventoryModule().getRandom();
        InventoryModule.Lists listModule = DeathControls.CONFIG.getConfig().getInventoryModule().getLists();


        ItemStack stack = inv.getStack(i);
        if (!stack.isEmpty()) {

            boolean drop = true;
            if (stack.isDamageable() && lossModule.droppedDurabilityLoss() > 0) {
                drop = !stack.damage((int) (stack.getMaxDamage() * lossModule.droppedDurabilityLoss()), random, null);
            }

            drop &= randomModule.getRandomDestroyChance() == 0 || Math.random() > randomModule.getRandomDestroyChance();
            drop &= !listModule.contains(listModule.getAlwaysDestroy(), stack);
            if (drop)
                dropItem(stack, true, false);
            inv.setStack(i, ItemStack.EMPTY);
        }
    }


    /**
     * Override for Death Controls Mod
     *
     * @author Flytre
     */
    @Override
    protected int getXpToDrop(PlayerEntity player) {
        ExperienceModule xpModule = DeathControls.CONFIG.getConfig().getExperienceModule();
        if (xpModule.shouldUseVanillaFormula() || world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || isSpectator())
            return super.getXpToDrop(player);
        return (int) Math.min(xpModule.getDroppedXpPercent() * EnchantmentUtils.calculateTotalXp(this) * xpModule.getRecoverableXpPercent(), xpModule.getMaxRecoverableXp());
    }
}
