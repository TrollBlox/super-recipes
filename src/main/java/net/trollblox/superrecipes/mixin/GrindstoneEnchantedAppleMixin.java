package net.trollblox.superrecipes.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Holder;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.trollblox.superrecipes.config.SuperConfigs;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneEnchantedAppleMixin extends AbstractContainerMenu {

    protected GrindstoneEnchantedAppleMixin(@Nullable MenuType<?> type, int syncId) {
        super(type, syncId);
    }

    @Final
    @Shadow
    Container repairSlots;

    @Final
    @Shadow
    private Container resultSlots;

    @ModifyArg(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 0
            ),
            index = 0
    )
    private Slot createCustomSlot(Slot slot) {
        return new Slot(repairSlots, 0, 49, 19) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(stack) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
            }
        };
    }

    @ModifyArg(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 1
            ),
            index = 0
    )
    private Slot createSecondCustomSlot(Slot slot) {
        return new Slot(repairSlots, 1, 49, 40) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.isDamageableItem() || EnchantmentHelper.hasAnyEnchantments(stack) || stack.is(Items.ENCHANTED_GOLDEN_APPLE);
            }
        };
    }

    @ModifyArg(
            method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/GrindstoneMenu;addSlot(Lnet/minecraft/world/inventory/Slot;)Lnet/minecraft/world/inventory/Slot;",
                    ordinal = 2
            ),
            index = 0
    )
    private Slot createThirdCustomSlot(Slot slot) {
        return new Slot(resultSlots, 2, 129, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                Level world = player.level();
                BlockPos pos = player.blockPosition();

                if (world instanceof ServerLevel) {
                    ExperienceOrb.award((ServerLevel)world, Vec3.atCenterOf(pos), this.getExperience(world));
                }

                world.levelEvent(LevelEvent.SOUND_GRINDSTONE_USED, pos, 0);

                ItemStack input1 = repairSlots.getItem(0);
                ItemStack input2 = repairSlots.getItem(1);
                if (stack.is(Items.GOLDEN_APPLE)) {
                    input1.setCount(input1.getCount() - (stack.getMaxStackSize() - input2.getCount()));
                    repairSlots.setItem(1, ItemStack.EMPTY);
                } else {
                    repairSlots.setItem(0, ItemStack.EMPTY);
                    repairSlots.setItem(1, ItemStack.EMPTY);
                }
            }

            private int getExperience(Level world) {
                int i = 0;
                i += this.getExperience(repairSlots.getItem(0));
                i += this.getExperience(repairSlots.getItem(1));
                ItemStack input1 = repairSlots.getItem(0);
                ItemStack input2 = repairSlots.getItem(1);
                if (input1.is(Items.ENCHANTED_GOLDEN_APPLE) || input2.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                    int math = Math.min((input1.getCount() + input2.getCount()), input1.getMaxStackSize());
                    math *= SuperConfigs.XP_FROM_ENCHANTED_GOLDEN_APPLE;
                    i += math;
                }
                if (i > 0) {
                    int j = (int)Math.ceil(i / 2.0);
                    return j + world.getRandom().nextInt(j);
                } else {
                    return 0;
                }
            }

            private int getExperience(ItemStack stack) {
                int i = 0;
                ItemEnchantments itemEnchantmentsComponent = EnchantmentHelper.getEnchantmentsForCrafting(stack);

                for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantmentsComponent.entrySet()) {
                    Holder<Enchantment> registryEntry = (Holder<Enchantment>)entry.getKey();
                    int j = entry.getIntValue();
                    if (!registryEntry.is(EnchantmentTags.CURSE)) {
                        i += registryEntry.value().getMinCost(j);
                    }
                }

                return i;
            }
        };
    }

    @Inject(at = @At("HEAD"), method = "computeResult", cancellable = true)
    private void overrideOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> info) {
        if ((firstInput.is(Items.ENCHANTED_GOLDEN_APPLE) || firstInput.isEmpty()) &&
                (secondInput.is(Items.ENCHANTED_GOLDEN_APPLE) || secondInput.isEmpty())) {
            int count = 0;
            if (firstInput.is(Items.ENCHANTED_GOLDEN_APPLE)) count += firstInput.getCount();
            if (secondInput.is(Items.ENCHANTED_GOLDEN_APPLE)) count += secondInput.getCount();
            info.setReturnValue(new ItemStack(Items.GOLDEN_APPLE, Math.min(count, Items.GOLDEN_APPLE.getDefaultMaxStackSize())));
        }
    }

    @Inject(at = @At("HEAD"), method = "quickMoveStack", cancellable = true)
    private void overrideQuickMove(Player player, int slot, CallbackInfoReturnable<ItemStack> info) {
        ItemStack input1 = this.slots.get(0).getItem();
        ItemStack input2 = this.slots.get(1).getItem();
        Slot slotObject = this.slots.get(slot);
        ItemStack slotStack = slotObject.getItem();
        ItemStack originalSlotStack = slotStack.copy();
        if (slotStack.is(Items.GOLDEN_APPLE) || slotStack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            if (slot == 2) {
                if (!this.moveItemStackTo(new ItemStack(Items.GOLDEN_APPLE, input1.getCount() + input2.getCount()), 3, 39, true)) {
                    info.setReturnValue(ItemStack.EMPTY);
                }
                slotObject.onQuickCraft(slotStack, originalSlotStack);
            } else if (slot > 2) {
                if (!this.moveItemStackTo(slotStack, 0, 2, false)) {
                    info.setReturnValue(ItemStack.EMPTY);
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 3, 39, false)) {
                    info.setReturnValue(ItemStack.EMPTY);
                }
            }


            if (slotStack.isEmpty()) {
                slotObject.setByPlayer(ItemStack.EMPTY);
            } else {
                slotObject.setChanged();
            }

            if (slotStack.getCount() == originalSlotStack.getCount()) {
                info.setReturnValue(ItemStack.EMPTY);
            }

            slotObject.onTake(player, slotStack);
        }
    }
}
