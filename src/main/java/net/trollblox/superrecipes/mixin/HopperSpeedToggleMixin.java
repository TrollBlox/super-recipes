package net.trollblox.superrecipes.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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
    @Inject(at = @At("HEAD"), method = "useWithoutItem", cancellable = true)
    private void overrideOnUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> info) {
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem().equals(BuiltInRegistries.ITEM.getValue(Identifier.parse(SuperConfigs.HOPPER_TOGGLE_ITEM)))) {
            HopperSpeedData blockEntity = (HopperSpeedData) world.getBlockEntity(pos);
            blockEntity.super_recipes_1_21$setHopperSpeed(HopperSpeed.getHopperSpeedFromValueInverse(blockEntity.super_recipes_1_21$getHopperSpeed().getValue()));
            String text = "Set hopper at (X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ() + ") to " + (blockEntity.super_recipes_1_21$getHopperSpeed().getValue() ? "slow" : "fast") + ".";
            player.sendOverlayMessage(Component.nullToEmpty(text));
            SuperRecipes.LOGGER.info("{} {}", player.getName().toString(), text.replace('S', 's'));
            info.setReturnValue(InteractionResult.FAIL);
        }
    }

}
