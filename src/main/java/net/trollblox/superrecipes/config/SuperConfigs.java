package net.trollblox.superrecipes.config;

import com.mojang.datafixers.util.Pair;
import net.trollblox.superrecipes.Constants;
import net.trollblox.superrecipes.SuperRecipes;

public class SuperConfigs {
    public static SimpleConfig CONFIG;
    private static SuperConfigProvider configs;

    public static int HOPPER_TICK_DELAY;
    public static float ANCIENT_DEBRIS_DROP_RATE;
    public static String HOPPER_TOGGLE_ITEM;
    public static boolean CHANNELING_AFFECTS_WEATHER;
    public static String LEVEL_THREE_BEACON_EFFECT;
    public static String LEVEL_FOUR_BEACON_EFFECT_0;
    public static String LEVEL_FOUR_BEACON_EFFECT_1;

    public static void registerConfigs() {
        configs = new SuperConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(Constants.MOD_ID).provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("hopper-tick-delay", 0), "Delay in tick for hopper transfer speed");
        configs.addKeyValuePair(new Pair<>("ancient-debris-droprate", 0.005f), "Drop chance for ancient debris from Zombified Piglins");
        configs.addKeyValuePair(new Pair<>("hopper-toggle-item", "wheat_seeds"), "Item held when opening a hopper to toggle speed");
        configs.addKeyValuePair(new Pair<>("channeling-affects-weather", true), "Channeling Tridents start thunderstorms");
        configs.addKeyValuePair(new Pair<>("level-three-beacon-effect", "health_boost"), "Custom level three beacon effect");
        configs.addKeyValuePair(new Pair<>("level-four-beacon-effect-0", "night_vision"), "Custom level four beacon effect");
        configs.addKeyValuePair(new Pair<>("level-four-beacon-effect-1", "fire_resistance"), "Custom level four beacon effect");
    }

    private static void assignConfigs() {
        HOPPER_TICK_DELAY = CONFIG.getOrDefault("hopper-tick-delay", 0);
        SuperRecipes.LOGGER.info("Assigned HOPPER_TICK_DELAY to {}", HOPPER_TICK_DELAY);
        ANCIENT_DEBRIS_DROP_RATE = (float) CONFIG.getOrDefault("ancient-debris-droprate", 0.005f);
        SuperRecipes.LOGGER.info("Assigned ANCIENT_DEBRIS_DROP_RATE to {}", ANCIENT_DEBRIS_DROP_RATE);
        HOPPER_TOGGLE_ITEM = CONFIG.getOrDefault("hopper-toggle-item", "wheat_seeds");
        SuperRecipes.LOGGER.info("Assigned HOPPER_TOGGLE_ITEM to {}", HOPPER_TOGGLE_ITEM);
        CHANNELING_AFFECTS_WEATHER = CONFIG.getOrDefault("channeling-affects-weather", true);
        SuperRecipes.LOGGER.info("Assigned CHANNELING_AFFECTS_WEATHER to {}", CHANNELING_AFFECTS_WEATHER);
        LEVEL_THREE_BEACON_EFFECT = CONFIG.getOrDefault("level-three-beacon-effect", "health_boost");
        SuperRecipes.LOGGER.info("Assigned LEVEL_THREE_BEACON_EFFECT to {}", LEVEL_THREE_BEACON_EFFECT);
        LEVEL_FOUR_BEACON_EFFECT_0 = CONFIG.getOrDefault("level-four-beacon-effect-0", "night_vision");
        SuperRecipes.LOGGER.info("Assigned LEVEL_FOUR_BEACON_EFFECT to {}", LEVEL_FOUR_BEACON_EFFECT_0);
        LEVEL_FOUR_BEACON_EFFECT_1 = CONFIG.getOrDefault("level-four-beacon-effect-1", "fire_resistance");
        SuperRecipes.LOGGER.info("Assigned LEVEL_FOUR_BEACON_EFFECT to {}", LEVEL_FOUR_BEACON_EFFECT_1);
    }
}
