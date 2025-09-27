package net.trollblox.superrecipes.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class PhantomBugFix extends PlayerEntity {
    public PhantomBugFix(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Inject(at = @At("HEAD"), method = "sleep", cancellable = true)
    private void stopWrongReset(BlockPos pos, CallbackInfo info) {
        super.sleep(pos);
        info.cancel();
    }

    @Inject(at = @At("HEAD"), method = "wakeUp")
    private void correctReset(CallbackInfo info) {
        this.resetStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST));
    }
}
