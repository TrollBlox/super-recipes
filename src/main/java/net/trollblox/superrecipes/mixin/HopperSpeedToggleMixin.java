package net.trollblox.superrecipes.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.trollblox.superrecipes.HopperSpeedData;
import net.trollblox.superrecipes.SuperRecipes;
import net.trollblox.superrecipes.config.SuperConfigs;
import net.trollblox.superrecipes.enums.HopperSpeed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public class HopperSpeedToggleMixin {
    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    private void overrideOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info) {
        if (player.getStackInHand(Hand.MAIN_HAND).getItem().equals(Registries.ITEM.get(Identifier.of(SuperConfigs.HOPPER_TOGGLE_ITEM)))) {
            HopperSpeedData blockEntity = (HopperSpeedData) world.getBlockEntity(pos);
            blockEntity.super_recipes_1_21$setHopperSpeed(HopperSpeed.getHopperSpeedFromValueInverse(blockEntity.super_recipes_1_21$getHopperSpeed().getValue()));
            String text = "Set hopper at (X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ() + ") to " + (blockEntity.super_recipes_1_21$getHopperSpeed().getValue() ? "slow" : "fast") + ".";
            player.sendMessage(Text.of(text), true);
            SuperRecipes.LOGGER.info("{} {}", player.getName().toString(), text.replace('S', 's'));
            info.setReturnValue(ActionResult.FAIL);
        }
    }

}
