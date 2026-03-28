package net.trollblox.superrecipes.util;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.resources.Identifier;
import net.trollblox.superrecipes.config.SuperConfigs;

public class SuperLootTableModifiers {
    public static final Identifier ZOMBIFIED_PIGLIN_ID = Identifier.parse("entities/zombified_piglin");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, holder) -> {
            if (source.isBuiltin() && key.identifier().equals(ZOMBIFIED_PIGLIN_ID)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0f))
                        .when(LootItemRandomChanceCondition.randomChance(SuperConfigs.ANCIENT_DEBRIS_DROP_RATE))
                        .add(LootItem.lootTableItem(Items.ANCIENT_DEBRIS))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
