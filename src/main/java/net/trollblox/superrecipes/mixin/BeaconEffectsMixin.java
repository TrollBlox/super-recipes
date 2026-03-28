package net.trollblox.superrecipes.mixin;

import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.trollblox.superrecipes.config.SuperConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(BeaconBlockEntity.class)
public class BeaconEffectsMixin {
    @Final
    @Mutable
    @Shadow
    public static List<List<Holder<MobEffect>>> BEACON_EFFECTS;

    @Final
    @Mutable
    @Shadow
    private static Set<Holder<MobEffect>> VALID_EFFECTS;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void overrideBeaconEffects(CallbackInfo info) {
        Holder<MobEffect> customEffectLevelThree = BuiltInRegistries.MOB_EFFECT.get(Identifier.withDefaultNamespace(SuperConfigs.LEVEL_THREE_BEACON_EFFECT)).get();
        Holder<MobEffect> customEffectLevelFour_0 = BuiltInRegistries.MOB_EFFECT.get(Identifier.withDefaultNamespace(SuperConfigs.LEVEL_FOUR_BEACON_EFFECT_0)).get();
        Holder<MobEffect> customEffectLevelFour_1 = BuiltInRegistries.MOB_EFFECT.get(Identifier.withDefaultNamespace(SuperConfigs.LEVEL_FOUR_BEACON_EFFECT_1)).get();
        BEACON_EFFECTS = List.of(List.of(MobEffects.SPEED, MobEffects.HASTE),
                List.of(MobEffects.RESISTANCE, MobEffects.JUMP_BOOST),
                List.of(MobEffects.STRENGTH, customEffectLevelThree),
                List.of(MobEffects.REGENERATION, customEffectLevelFour_0, customEffectLevelFour_1));
        VALID_EFFECTS = BEACON_EFFECTS.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

}
