package net.flytre.death_controls.config;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

public class InventoryModule {
    @SerializedName("partial_keep_inventory")
    private final SelectiveInventory selectiveInventory;

    @SerializedName("loss")
    private final Loss loss;

    @SerializedName("random")
    private final Random random;

    @SerializedName("lists")
    private final Lists lists;

    public InventoryModule() {
        this.selectiveInventory = new SelectiveInventory();
        this.loss = new Loss();
        this.random = new Random();
        this.lists = new Lists();
    }

    public SelectiveInventory getSelectiveInventory() {
        return selectiveInventory;
    }

    public Loss getLoss() {
        return loss;
    }

    public Random getRandom() {
        return random;
    }

    public Lists getLists() {
        return lists;
    }

    public static class SelectiveInventory {
        @SerializedName("trinket_slots_to_keep")
        private final String[] trinketSlots;

        @SerializedName("keep_all_trinkets")
        private final boolean keepAllTrinkets;

        @SerializedName("keep_armor")
        private final boolean keepArmor;
        @SerializedName("keep_hotbar")
        private final boolean keepHotbar;
        @SerializedName("keep_mainhand")
        private final boolean keepMainhand;
        @SerializedName("keep_offhand")
        private final boolean keepOffhand;
        @SerializedName("keep_main_inventory")
        private final boolean keepMainInventory;


        private transient Set<String> cachedTrinketSlots = null;

        public SelectiveInventory() {
            this.keepArmor = true;
            this.keepHotbar = true;
            this.keepMainhand = true;
            this.keepOffhand = true;
            this.keepMainInventory = false;
            this.keepAllTrinkets = false;
            this.trinketSlots = new String[]{"__comment: format example: chest.cape. See https://github.com/emilyalexandra/trinkets/wiki/Default-Slots for available default slots."};
        }

        public boolean shouldKeepArmor() {
            return keepArmor;
        }

        public boolean shouldKeepHotbar() {
            return keepHotbar;
        }

        public boolean shouldKeepMainhand() {
            return keepMainhand;
        }

        public boolean shouldKeepOffhand() {
            return keepOffhand;
        }

        public boolean shouldKeepMainInventory() {
            return keepMainInventory;
        }

        public void cacheTrinketSlots() {
            this.cachedTrinketSlots = new HashSet<>(Arrays.asList(trinketSlots));
        }

        public Set<String> getTrinketSlots() {
            if (cachedTrinketSlots == null)
                cacheTrinketSlots();
            return cachedTrinketSlots;
        }

        public boolean shouldKeepAllTrinkets() {
            return keepAllTrinkets;
        }
    }

    public static class Loss {

        @SerializedName("durability_loss_on_dropped_items")
        private final double durabilityLossOnDroppedItems;

        @SerializedName("durability_loss_on_kept_items")
        private final double durabilityLossOnKeptItems;

        public Loss() {
            this.durabilityLossOnDroppedItems = 0.0d;
            this.durabilityLossOnKeptItems = 0.0d;
        }

        public double droppedDurabilityLoss() {
            return durabilityLossOnDroppedItems;
        }

        public double keptDurabilityLoss() {
            return durabilityLossOnKeptItems;
        }
    }

    public static class Random {

        @SerializedName("chance_of_dropping_each_kept_item")
        private final double randomDropChance;

        @SerializedName("chance_of_destroying_each_dropped_item")
        private final double randomDestroyChance;

        public Random() {
            this.randomDestroyChance = 0.0d;
            this.randomDropChance = 0.0d;
        }

        public double getRandomDropChance() {
            return randomDropChance;
        }

        public double getRandomDestroyChance() {
            return randomDestroyChance;
        }
    }

    public static class Lists {
        @SerializedName("always_keep")
        private final String[] alwaysKeep;

        @SerializedName("always_drop")
        private final String[] alwaysDrop;

        @SerializedName("always_destroy")
        private final String[] alwaysDestroy;

        public Lists() {
            this.alwaysKeep = new String[0];
            this.alwaysDrop = new String[0];
            this.alwaysDestroy = new String[0];
        }

        public String[] getAlwaysKeep() {
            return alwaysKeep;
        }

        public String[] getAlwaysDrop() {
            return alwaysDrop;
        }

        public String[] getAlwaysDestroy() {
            return alwaysDestroy;
        }

        public boolean contains(String[] ls, ItemStack stack) {
            Identifier id = Registry.ITEM.getId(stack.getItem());
            return Arrays.stream(ls).anyMatch(e -> id.toString().equals(e) || id.getNamespace().equals("minecraft") && id.getPath().equals(e));
        }
    }

}
