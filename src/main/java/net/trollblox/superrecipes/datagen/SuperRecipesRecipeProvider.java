package net.trollblox.superrecipes.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.trollblox.superrecipes.Constants;

import java.util.concurrent.CompletableFuture;

public class SuperRecipesRecipeProvider extends FabricRecipeProvider {
    public SuperRecipesRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {

        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                // Crossbow Dispenser Recipe
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.REDSTONE, Items.DISPENSER, 1)
                        .pattern("###")
                        .pattern("#C#")
                        .pattern("#D#")
                        .input('#', Items.COBBLESTONE).input('D', Items.REDSTONE).input('C', Items.CROSSBOW)
                        .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                        .offerTo(exporter, "crossbow_dispenser");

                // Crossbow Dispenser + Deepslate Recipe
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.REDSTONE, Items.DISPENSER, 1)
                        .pattern("###")
                        .pattern("#C#")
                        .pattern("#D#")
                        .input('#', Items.COBBLED_DEEPSLATE).input('D', Items.REDSTONE).input('C', Items.CROSSBOW)
                        .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                        .offerTo(exporter, "deepslate_crossbow_dispenser");

                // Deepslate Dropper
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.REDSTONE, Items.DROPPER, 1)
                        .pattern("###")
                        .pattern("# #")
                        .pattern("#R#")
                        .input('#', Blocks.COBBLED_DEEPSLATE).input('R', Items.REDSTONE)
                        .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                        .offerTo(exporter, "deepslate_dropper");

                // Music Discs
                generateMusicDiscRecipes(exporter);
            }

            private void generateMusicDiscRecipes(RecipeExporter exporter) {
                for (Item item : Registries.ITEM.stream().toList()) {
                    if (item.getComponents().contains(DataComponentTypes.JUKEBOX_PLAYABLE)) {
                        offerMusicDiscDuplicationRecipe(exporter, item);
                    }
                }
            }

            private void offerMusicDiscDuplicationRecipe(RecipeExporter exporter, ItemConvertible item) {
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.MISC, item, 1 )
                        .pattern("###")
                        .pattern("#X#")
                        .pattern("###")
                        .input('#', Items.DIAMOND).input('X', item)
                        .criterion(hasItem(item), conditionsFromItem(item))
                        .offerTo(exporter, Identifier.of(getRecipeName(item)) + "_duplication");
            }
        };
    }

    @Override
    public String getName() {
        return Constants.MOD_ID;
    }
}
