package net.trollblox.superrecipes.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.trollblox.superrecipes.Constants;
import net.trollblox.superrecipes.HopperSpeedData;
import net.trollblox.superrecipes.SuperRecipes;
import net.trollblox.superrecipes.config.SuperConfigs;
import net.trollblox.superrecipes.enums.HopperSpeed;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin(HopperBlockEntity.class)
public class HopperSpeedMixin implements HopperSpeedData {
    @Unique
    private HopperSpeed hopperSpeed;

    @Shadow
    private int transferCooldown;

    @Unique
    public void super_recipes_1_21$setHopperSpeed(HopperSpeed hopperSpeed) {
        this.hopperSpeed = hopperSpeed;
    }

    @Unique
    public HopperSpeed super_recipes_1_21$getHopperSpeed() {
        return (hopperSpeed == null ? HopperSpeed.MODDED : hopperSpeed);
    }

    @Inject(at = @At("TAIL"), method = "setTransferCooldown")
    private void overrideNeedsCooldown(int transferCooldown, CallbackInfo info) {
        if (super_recipes_1_21$getHopperSpeed() == HopperSpeed.VANILLA) return;
        this.transferCooldown = transferCooldown - (8 - SuperConfigs.HOPPER_TICK_DELAY);
    }

    @Inject(at = @At("TAIL"), method = "writeData")
    private void writeData(WriteView view, CallbackInfo info) {
        view.putBoolean(Constants.HOPPER_SPEED_DATA_ID, hopperSpeed == HopperSpeed.MODDED);
    }

    @Inject(at = @At("TAIL"), method = "readData")
    private void readData(ReadView view, CallbackInfo info) {
        boolean hopperSpeedData = view.getBoolean(Constants.HOPPER_SPEED_DATA_ID, HopperSpeed.VANILLA.getValue());

        hopperSpeed = HopperSpeed.getHopperSpeedFromValue(hopperSpeedData);
    }

}
