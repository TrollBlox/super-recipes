package net.trollblox.superrecipes.listeners;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.trollblox.superrecipes.HopperSpeedData;
import net.trollblox.superrecipes.SuperRecipes;
import net.trollblox.superrecipes.config.SuperConfigs;
import net.trollblox.superrecipes.enums.HopperSpeed;

public class HopperSpeedToggleListener {
    public static void init() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos blockPosition = hitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPosition);
            if (!blockState.getBlock().equals(Blocks.HOPPER)) return ActionResult.PASS;
            if (!player.getStackInHand(Hand.MAIN_HAND).getItem().equals(Registries.ITEM.get(Identifier.of(SuperConfigs.HOPPER_TOGGLE_ITEM)))) return ActionResult.PASS;
            HopperSpeedData blockEntity = (HopperSpeedData) world.getBlockEntity(blockPosition);
            blockEntity.super_recipes_1_21$setHopperSpeed(HopperSpeed.getHopperSpeedFromValueInverse(blockEntity.super_recipes_1_21$getHopperSpeed().getValue()));
            String text = "Set hopper at (X: " + blockPosition.getX() + " Y: " + blockPosition.getY() + " Z: " + blockPosition.getZ() + ") to " + (blockEntity.super_recipes_1_21$getHopperSpeed().getValue() ? "slow" : "fast") + ".";
            player.sendMessage(Text.of(text), true);
            SuperRecipes.LOGGER.info("{} {}", player.getName(), text.replace('S', 's'));
            return ActionResult.FAIL;
        });
    }
}
