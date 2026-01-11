package net.trollblox.superrecipes.mixin;

import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.util.Identifier;
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
    public static List<List<RegistryEntry<StatusEffect>>> EFFECTS_BY_LEVEL;

    @Final
    @Mutable
    @Shadow
    private static Set<RegistryEntry<StatusEffect>> EFFECTS;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void overrideBeaconEffects(CallbackInfo info) {
        RegistryEntry<StatusEffect> customEffectLevelThree = Registries.STATUS_EFFECT.getEntry(Identifier.ofVanilla(SuperConfigs.LEVEL_THREE_BEACON_EFFECT)).get();
        RegistryEntry<StatusEffect> customEffectLevelFour_0 = Registries.STATUS_EFFECT.getEntry(Identifier.ofVanilla(SuperConfigs.LEVEL_FOUR_BEACON_EFFECT_0)).get();
        RegistryEntry<StatusEffect> customEffectLevelFour_1 = Registries.STATUS_EFFECT.getEntry(Identifier.ofVanilla(SuperConfigs.LEVEL_FOUR_BEACON_EFFECT_1)).get();
        EFFECTS_BY_LEVEL = List.of(List.of(StatusEffects.SPEED, StatusEffects.HASTE),
                List.of(StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST),
                List.of(StatusEffects.STRENGTH, customEffectLevelThree),
                List.of(StatusEffects.REGENERATION, customEffectLevelFour_0, customEffectLevelFour_1));
        EFFECTS = EFFECTS_BY_LEVEL.stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

}
