package net.trollblox.superrecipes.mixin;

import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.trollblox.superrecipes.DispenserCauldronHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class DefaultDispenseItemBehaviorMixin {

	@Inject(at = @At("HEAD"), method = "execute", cancellable = true)
	public void cauldronMixin(BlockSource pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        Level world = pointer.level();
        if (world.isClientSide()) {
            return;
        }

        if (pointer.state().getBlock() != Blocks.DISPENSER) {
            return;
        }

        DispenserBlockEntity dispenser = pointer.blockEntity();
        BlockPos pos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
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

            world.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
            ItemStack bucket = new ItemStack(DispenserCauldronHelper.CauldronToBucket.getOrDefault(block_state.getBlock(), Items.AIR));

            if (handleDispense(stack, bucket, dispenser, cir)) {
                customDispenseSilently(pointer, bucket);
            }

        // Fills a cauldron with the bucket contents regardless of cauldron type and level (Vanilla Mechanic)
        } else if (DispenserCauldronHelper.BucketToCauldron.containsKey(item)) {
            cir.setReturnValue(new ItemStack(Items.BUCKET));
            world.setBlockAndUpdate(pos, (BlockState) DispenserCauldronHelper.BucketToCauldron.get(item).get("block_state"));
        }
    }

    @Unique
    public boolean handleDispense(ItemStack stack, ItemStack stack2, DispenserBlockEntity dispenser, CallbackInfoReturnable<ItemStack> cir) {
        stack.shrink(1);
        if (stack.isEmpty()) {
            cir.setReturnValue(stack2);

        } else {
            dispenser.insertItem(stack2);
            cir.setReturnValue(stack);
            return true;
        }

        return false;
    }

    @Unique
    public void customDispenseSilently(BlockSource pointer, ItemStack stack) {
        BlockPos block_pos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
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
        
        Level world = pointer.level();
        ItemEntity entity = new ItemEntity(world, pointer.pos().getX() + offset_x, pointer.pos().getY() + offset_y, pointer.pos().getZ() + offset_z, stack, velocity_x, velocity_y, velocity_z);
        world.addFreshEntity(entity);
    }
}