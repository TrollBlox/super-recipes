package net.trollblox.superrecipes.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MusicDiscRemainderMixin {

    @Inject(at = @At("HEAD"), cancellable = true, method = "getCraftingRemainder")
    private void overrideSetRecipeRemainder(CallbackInfoReturnable<ItemStackTemplate> info) {
        if (((Item)(Object)this).components().has(DataComponents.JUKEBOX_PLAYABLE)) {
            info.setReturnValue(new ItemStackTemplate((Item)(Object)this));
        }
    }

}