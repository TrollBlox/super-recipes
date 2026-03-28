package net.trollblox.superrecipes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.trollblox.superrecipes.Constants;

import java.util.concurrent.CompletableFuture;

public class SuperRecipesRecipeProvider extends FabricRecipeProvider {
    public SuperRecipesRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {

        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                // Crossbow Dispenser Recipe
                ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.REDSTONE, Items.DISPENSER, 1)
                        .pattern("###")
                        .pattern("#C#")
                        .pattern("#D#")
                        .define('#', Items.COBBLESTONE).define('D', Items.REDSTONE).define('C', Items.CROSSBOW)
                        .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output, "crossbow_dispenser");

                // Crossbow Dispenser + Deepslate Recipe
                ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.REDSTONE, Items.DISPENSER, 1)
                        .pattern("###")
                        .pattern("#C#")
                        .pattern("#D#")
                        .define('#', Items.COBBLED_DEEPSLATE).define('D', Items.REDSTONE).define('C', Items.CROSSBOW)
                        .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output, "deepslate_crossbow_dispenser");

                // Deepslate Dropper
                ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.REDSTONE, Items.DROPPER, 1)
                        .pattern("###")
                        .pattern("# #")
                        .pattern("#R#")
                        .define('#', Blocks.COBBLED_DEEPSLATE).define('R', Items.REDSTONE)
                        .unlockedBy(getHasName(Items.REDSTONE), has(Items.REDSTONE))
                        .save(output, "deepslate_dropper");

                // Music Discs
                generateMusicDiscRecipes(output);
            }

            private void generateMusicDiscRecipes(RecipeOutput exporter) {
                for (Item item : BuiltInRegistries.ITEM.stream().toList()) {
                    if (item.components().has(DataComponents.JUKEBOX_PLAYABLE)) {
                        offerMusicDiscDuplicationRecipe(exporter, item);
                    }
                }
            }

            private void offerMusicDiscDuplicationRecipe(RecipeOutput exporter, ItemLike item) {
                ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.MISC, item, 1 )
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .define('#', Items.DIAMOND).define('X', item)
                        .unlockedBy(getHasName(item), has(item))
                        .save(exporter, Identifier.parse(getSimpleRecipeName(item)) + "_duplication");
            }
        };
    }

    @Override
    public String getName() {
        return Constants.MOD_ID;
    }
}
