package net.mirkiri.pokeball.item;

import net.mirkiri.pokeball.PokeballMod;
import net.mirkiri.pokeball.entity.PokeballEntity;
import net.mirkiri.pokeball.util.NBTHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class PokeballItem extends Item {
    public PokeballItem(Settings settings) {
        super(settings.maxDamage(10));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return NBTHelper.hasNbt(stack, "StoredEntity");
    }

    @Override
    public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack itemStackIn = playerIn.getStackInHand(hand);
        if (!worldIn.isClient) {
            PokeballEntity pokeball = new PokeballEntity(playerIn, worldIn, itemStackIn.copy());
            pokeball.setVelocity(playerIn.getRotationVector().x, playerIn.getRotationVector().y, playerIn.getRotationVector().z, 1.5F, 1.0F);
            worldIn.spawnEntity(pokeball);
        }

        worldIn.playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5F, 0.4F / (worldIn.getRandom().nextFloat() * 0.4F + 0.8F));
        playerIn.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!playerIn.isCreative() || NBTHelper.hasNbt(itemStackIn, "StoredEntity")) {
            return TypedActionResult.success(ItemStack.EMPTY);
        }

        return TypedActionResult.success(itemStackIn);
    }

    @Override
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
        if (NBTHelper.hasNbt(stack, "StoredEntity")) {
            NbtCompound stored = NBTHelper.getNbt(stack, "StoredEntity");

            String entityName = stored.getString("id");
            if (stored.contains("pokeball_name")) {
                entityName = stored.getString("pokeball_name");
            }
            if (stored.contains("CustomName")) {
                String s = stored.getString("CustomName");

                try {
                    MutableText customName = Text.Serializer.fromJson(stored.getString("CustomName"));
                    customName.formatted(Formatting.BLUE, Formatting.ITALIC);
                    tooltip.add(Text.translatable("tooltip.pokeball.stored_custom_name", customName, Text.translatable(entityName).formatted(Formatting.AQUA)));
                } catch (Exception exception) {
                    PokeballMod.LOGGER.warn("Failed to parse entity custom name {}", s, exception);
                    tooltip.add(Text.translatable("tooltip.pokeball.stored", Text.translatable(entityName).formatted(Formatting.AQUA)));
                }
            } else {
                tooltip.add(Text.translatable("tooltip.pokeball.stored", Text.translatable(entityName).formatted(Formatting.AQUA)));
            }
        } else {
            tooltip.add(Text.translatable("tooltip.pokeball.empty").formatted(Formatting.GRAY));
        }
    }
}
