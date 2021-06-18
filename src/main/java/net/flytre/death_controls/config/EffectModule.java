package net.flytre.death_controls.config;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.annotations.SerializedName;
import net.flytre.death_controls.DeathAttributeModifier;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;

public class EffectModule {

    @SerializedName("respawn_effects")
    private final RespawnEffect[] respawnEffects;

    @SerializedName("custom_effect")
    private final CustomEffect customEffect;

    public EffectModule() {
        this.respawnEffects = new RespawnEffect[]{new RespawnEffect()};
        this.customEffect = new CustomEffect();
    }

    public RespawnEffect[] getRespawnEffects() {
        return respawnEffects;
    }

    public CustomEffect getCustomEffect() {
        return customEffect;
    }

    public static class RespawnEffect {
        private final String id;
        private final int duration;
        private final int amplifier;

        public RespawnEffect() {
            this.id = "minecraft:regeneration";
            this.duration = 200;
            this.amplifier = 0;
        }

        public String getId() {
            return id;
        }

        public int getDuration() {
            return duration;
        }

        public int getAmplifier() {
            return amplifier;
        }
    }

    public static class CustomEffect {

        @SuppressWarnings("unused")
        private final String _comment = "Modifiers are added, while multipliers are multiplied to the default value. Gradual: true makes the effect intensity scale with duration";

        private final int duration;

        @SerializedName("gradual_recovery")
        private final boolean gradualRecovery;

        @SerializedName("max_health_modifier")
        private final double maxHealthModifier;

        @SerializedName("knockback_resistance_modifier")
        private final double knockbackResistanceModifier;

        @SerializedName("movement_speed_multiplier")
        private final double movementSpeedMultiplier;

        @SerializedName("attack_damage_modifier")
        private final double attackDamageModifier;

        @SerializedName("attack_speed_multiplier")
        private final double attackSpeedMultiplier;

        @SerializedName("armor_modifier")
        private final double armorModifier;

        @SerializedName("armor_toughness_modifier")
        private final double armorToughnessModifier;

        @SerializedName("attack_knockback_modifier")
        private final double attackKnockbackModifier;

        @SerializedName("luck_modifier")
        private final double luckModifier;

        public CustomEffect() {
            duration = 0;
            gradualRecovery = true;
            maxHealthModifier = -10;
            knockbackResistanceModifier = 0;
            movementSpeedMultiplier = 1.0;
            attackDamageModifier = -1;
            attackSpeedMultiplier = 1.0;
            armorModifier = 0;
            armorToughnessModifier = 0;
            attackKnockbackModifier = 0;
            luckModifier = 0;
        }

        public Multimap<EntityAttribute, EntityAttributeModifier> mapConfig() {
            Multimap<EntityAttribute, EntityAttributeModifier> map = HashMultimap.create();
            String name = "death_controls:re" + (gradualRecovery ? "_g" : "");
            map.put(EntityAttributes.GENERIC_MAX_HEALTH, new DeathAttributeModifier(name, maxHealthModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new DeathAttributeModifier(name, knockbackResistanceModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new DeathAttributeModifier(name, movementSpeedMultiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new DeathAttributeModifier(name, attackDamageModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_ATTACK_SPEED, new DeathAttributeModifier(name, attackSpeedMultiplier - 1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_ARMOR, new DeathAttributeModifier(name, armorModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, new DeathAttributeModifier(name, armorToughnessModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, new DeathAttributeModifier(name, attackKnockbackModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            map.put(EntityAttributes.GENERIC_LUCK, new DeathAttributeModifier(name, luckModifier, EntityAttributeModifier.Operation.ADDITION, duration, gradualRecovery));
            return map;
        }

        public int getDuration() {
            return duration;
        }
    }
}
