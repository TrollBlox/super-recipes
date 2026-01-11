package net.trollblox.superrecipes.mixin;

import net.minecraft.block.*;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.trollblox.superrecipes.DispenserCauldronHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin {

	@Inject(at = @At("HEAD"), method = "dispenseSilently", cancellable = true)
	public void cauldronMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        World world = pointer.world();
        if (world.isClient()) {
            return;
        }

        if (pointer.state().getBlock() != Blocks.DISPENSER) {
            return;
        }

        DispenserBlockEntity dispenser = pointer.blockEntity();
        BlockPos pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        BlockState block_state = world.getBlockState(pos);
        Block block = block_state.getBlock();
        Item item = stack.getItem();

        if (!(block instanceof AbstractCauldronBlock)) {
            return;
        }

        // Handles buckets on full cauldrons
        if (item == Items.BUCKET) {
            if (DispenserCauldronHelper.getCauldronLevel(block_state) != DispenserCauldronHelper.CauldronLevel.FULL) {
                return;
            }

            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
            ItemStack bucket = new ItemStack(DispenserCauldronHelper.CauldronToBucket.getOrDefault(block_state.getBlock(), Items.AIR));

            if (handleDispense(stack, bucket, dispenser, cir)) {
                customDispenseSilently(pointer, bucket);
            }

        // Fills a cauldron with the bucket contents regardless of cauldron type and level (Vanilla Mechanic)
        } else if (DispenserCauldronHelper.BucketToCauldron.containsKey(item)) {
            cir.setReturnValue(new ItemStack(Items.BUCKET));
            world.setBlockState(pos, (BlockState) DispenserCauldronHelper.BucketToCauldron.get(item).get("block_state"));
        }
    }

    @Unique
    public boolean handleDispense(ItemStack stack, ItemStack stack2, DispenserBlockEntity dispenser, CallbackInfoReturnable<ItemStack> cir) {
        stack.decrement(1);
        if (stack.isEmpty()) {
            cir.setReturnValue(stack2);

        } else {
            dispenser.addToFirstFreeSlot(stack2);
            cir.setReturnValue(stack);
            return true;
        }

        return false;
    }

    @Unique
    public void customDispenseSilently(BlockPointer pointer, ItemStack stack) {
        BlockPos block_pos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        BlockPos dispenser_pos = pointer.pos();

        double velocity_x = block_pos.getX() - dispenser_pos.getX();
        double velocity_y = block_pos.getY() - dispenser_pos.getY();
        double velocity_z = block_pos.getZ() - dispenser_pos.getZ();

        double offset_x = 0;
        double offset_y = 0;
        double offset_z = 0;

        if (velocity_x != 0) { velocity_x /= 1.5; offset_x = 0.2; }
        if (velocity_y != 0) { velocity_y /= 1.5; offset_y = 0.2; }
        if (velocity_z != 0) { velocity_z /= 1.5; offset_z = 0.2; }
        
        World world = pointer.world();
        ItemEntity entity = new ItemEntity(world, pointer.pos().getX() + offset_x, pointer.pos().getY() + offset_y, pointer.pos().getZ() + offset_z, stack, velocity_x, velocity_y, velocity_z);
        world.spawnEntity(entity);
    }
}