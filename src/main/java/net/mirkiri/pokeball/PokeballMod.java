package net.mirkiri.pokeball;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.mirkiri.pokeball.config.PokeballConfig;
import net.mirkiri.pokeball.entity.PokeballEntity;
import net.mirkiri.pokeball.item.PokeballItem;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PokeballMod implements ModInitializer {
	public static final String MODID = "pokeball";

	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final Item POKEBALL = new PokeballItem(new FabricItemSettings().group(ItemGroup.MISC));

	public static final EntityType<PokeballEntity> POKEBALL_ENTITY = Registry.register(
			Registry.ENTITY_TYPE, new Identifier(MODID, "pokeball"),
			EntityType.Builder.<PokeballEntity>create(PokeballEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).trackingTickInterval(10).build("pokeball")
	);

	@Override
	public void onInitialize() {
		AutoConfig.register(PokeballConfig.class, JanksonConfigSerializer::new);
		Registry.register(Registry.ITEM, new Identifier(MODID, "pokeball"), POKEBALL);
		EntityRendererRegistry.register(POKEBALL_ENTITY, FlyingItemEntityRenderer::new);
	}

}
