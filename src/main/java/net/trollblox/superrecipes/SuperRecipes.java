package net.trollblox.superrecipes;

import net.fabricmc.api.ModInitializer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.trollblox.superrecipes.config.SuperConfigs;
import net.trollblox.superrecipes.util.SuperLootTableModifiers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.trollblox.superrecipes.Constants.MOD_ID;

public class SuperRecipes implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Super Recipes...");

		SuperConfigs.registerConfigs();

		SuperLootTableModifiers.modifyLootTables();
	}
}