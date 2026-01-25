package net.trollblox.superrecipes.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.trollblox.superrecipes.config.SuperConfigs;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneMixin extends ScreenHandler {

    protected GrindstoneMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Final
    @Shadow
    Inventory input;

    @Final
    @Shadow
    private Inventory result;

    @ModifyArg(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                    ordinal = 0
            ),
            index = 0
    )
    private Slot createCustomSlot(Slot slot) {
        return new Slot(input, 0, 49, 19) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
            }
        };
    }

    @ModifyArg(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                    ordinal = 1
            ),
            index = 0
    )
    private Slot createSecondCustomSlot(Slot slot) {
        return new Slot(input, 1, 49, 40) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isDamageable() || EnchantmentHelper.hasEnchantments(stack) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
            }
        };
    }

    @ModifyArg(
            method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/screen/GrindstoneScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
                    ordinal = 2
            ),
            index = 0
    )
    private Slot createThirdCustomSlot(Slot slot) {
        return new Slot(result, 2, 129, 34) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                World world = player.getEntityWorld();
                BlockPos pos = player.getBlockPos();

                if (world instanceof ServerWorld) {
                    ExperienceOrbEntity.spawn((ServerWorld)world, Vec3d.ofCenter(pos), this.getExperience(world));
                }

                world.syncWorldEvent(WorldEvents.GRINDSTONE_USED, pos, 0);

                ItemStack input1 = input.getStack(0);
                ItemStack input2 = input.getStack(1);
                if (stack.isOf(Items.GOLDEN_APPLE)) {
                    input1.setCount(input1.getCount() - (stack.getMaxCount() - input2.getCount()));
                    input.setStack(1, ItemStack.EMPTY);
                } else {
                    input.setStack(0, ItemStack.EMPTY);
                    input.setStack(1, ItemStack.EMPTY);
                }
            }

            private int getExperience(World world) {
                int i = 0;
                i += this.getExperience(input.getStack(0));
                i += this.getExperience(input.getStack(1));
                ItemStack input1 = input.getStack(0);
                ItemStack input2 = input.getStack(1);
                if (input1.isOf(Items.ENCHANTED_GOLDEN_APPLE) || input2.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                    int math = Math.min((input1.getCount() + input2.getCount()), input1.getMaxCount());
                    math *= SuperConfigs.XP_FROM_ENCHANTED_GOLDEN_APPLE;
                    i += math;
                }
                if (i > 0) {
                    int j = (int)Math.ceil(i / 2.0);
                    return j + world.random.nextInt(j);
                } else {
                    return 0;
                }
            }

            private int getExperience(ItemStack stack) {
                int i = 0;
                ItemEnchantmentsComponent itemEnchantmentsComponent = EnchantmentHelper.getEnchantments(stack);

                for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
                    RegistryEntry<Enchantment> registryEntry = (RegistryEntry<Enchantment>)entry.getKey();
                    int j = entry.getIntValue();
                    if (!registryEntry.isIn(EnchantmentTags.CURSE)) {
                        i += registryEntry.value().getMinPower(j);
                    }
                }

                return i;
            }
        };
    }

    @Inject(at = @At("HEAD"), method = "getOutputStack", cancellable = true)
    private void overrideOutputStack(ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> info) {
        if ((firstInput.isOf(Items.ENCHANTED_GOLDEN_APPLE) || firstInput.isEmpty()) &&
                (secondInput.isOf(Items.ENCHANTED_GOLDEN_APPLE) || secondInput.isEmpty())) {
            int count = 0;
            if (firstInput.isOf(Items.ENCHANTED_GOLDEN_APPLE)) count += firstInput.getCount();
            if (secondInput.isOf(Items.ENCHANTED_GOLDEN_APPLE)) count += secondInput.getCount();
            info.setReturnValue(new ItemStack(Items.GOLDEN_APPLE, Math.min(count, Items.GOLDEN_APPLE.getMaxCount())));
        }
    }
}
