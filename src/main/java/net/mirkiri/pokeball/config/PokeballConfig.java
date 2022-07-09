package net.mirkiri.pokeball.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "pokeball")
public class PokeballConfig implements ConfigData {
    @Comment("Blacklisted mobs will not be captured by the pokeball, ex: minecraft:zombie")
    public List<String> BLACKLIST = new ArrayList<>();

    @Comment("Is it possible to capture all types of entities, if true, non-living things like boats can be captured")
    public boolean allowCaptureOfAllTypes = false;

    public static PokeballConfig getConfig() {
        return AutoConfig.getConfigHolder(PokeballConfig.class).getConfig();
    }
}
