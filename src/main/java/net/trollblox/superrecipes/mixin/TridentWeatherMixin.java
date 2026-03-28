package net.trollblox.superrecipes.mixin;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import net.trollblox.superrecipes.SuperRecipes;
import net.trollblox.superrecipes.config.SuperConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public abstract class TridentWeatherMixin extends Entity {
    @Shadow public abstract ItemStack getWeaponItem();
    @Shadow private boolean dealtDamage;

    @Unique
    private ItemStack itemStack;

    @Unique
    private void changeWeather(boolean block) {
        Level world = this.level();
        if (dealtDamage) return;
        if (!SuperConfigs.CHANNELING_AFFECTS_WEATHER) return;
        if (itemStack == null) return;
        if (!itemStack.getEnchantments().keySet().contains(world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.CHANNELING).orElse(null))) return;
        if (!world.isClientSide()) {
            MinecraftServer server = world.getServer();
            if (server == null) return;
            ServerLevel overworld = server.overworld();
            Entity bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, overworld);
            bolt.setPos(this.trackingPosition());
            boolean thundering = overworld.isThundering();
            if (block || !thundering) {
                overworld.addFreshEntity(bolt);
            }
            SuperRecipes.LOGGER.info("thundering: {}", thundering);
            if (!thundering) {
                overworld.getServer().setWeatherParameters(0, ServerLevel.THUNDER_DURATION.sample(overworld.getRandom()), true, true);
                overworld.setRainLevel(1.0f);
                overworld.setThunderLevel(1.0f);
                overworld.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 1.0f), overworld.dimension());
                overworld.getServer().getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 1.0f), overworld.dimension());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "onHitEntity")
    private void overrideEntityHit(EntityHitResult entityHitResult, CallbackInfo info) {
        changeWeather(false);
    }

    @Inject(at = @At("HEAD"), method = "hitBlockEnchantmentEffects")
    private void overrideBlockHit(ServerLevel world, BlockHitResult blockHitResult, ItemStack weaponStack, CallbackInfo info) {
        changeWeather(true);
    }

    public TridentWeatherMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V")
    private void overrideConstructor(Level world, LivingEntity owner, ItemStack stack, CallbackInfo info) {
        itemStack = stack;
    }
}
