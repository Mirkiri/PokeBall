package net.mirkiri.pokeball.entity;

import net.minecraft.util.Identifier;
import net.mirkiri.pokeball.PokeballMod;
import net.mirkiri.pokeball.config.PokeballConfig;
import net.mirkiri.pokeball.util.NBTHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class PokeballEntity extends ThrownItemEntity {

	private boolean hasEntity;
	private ItemStack currentPokeball = new ItemStack(PokeballMod.POKEBALL, 1);
	public PokeballEntity(EntityType<? extends PokeballEntity> type, World worldIn) {
		super(type, worldIn);
	}

	public PokeballEntity(EntityType<? extends PokeballEntity> type, double x, double y, double z, World worldIn) {
		super(type, x, y, z, worldIn);
	}

	public PokeballEntity(LivingEntity livingEntityIn, World worldIn, ItemStack stack) {
		super(PokeballMod.POKEBALL_ENTITY, livingEntityIn, worldIn);
		this.currentPokeball = stack;
		this.hasEntity = false;
		if (this.currentPokeball.hasNbt()) {
			this.hasEntity = NBTHelper.hasNbt(stack, "StoredEntity");
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!world.isClient) {
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				if (this.hasEntity) {
					Optional<Entity> loadEntity = EntityType.getEntityFromNbt(NBTHelper.getNbt(currentPokeball, "StoredEntity"), this.world);
					if (loadEntity.isPresent()) {
						Entity spawnEntity = loadEntity.get();
						spawnEntity.refreshPositionAndAngles(this.getX(), this.getY() + 1.0D, this.getZ(), this.getYaw(), 0.0F);
						this.world.spawnEntity(spawnEntity);
					}
					// Always reset pokeball
					NBTHelper.removeNbt(this.currentPokeball, "StoredEntity");
				}
			} else if (hitResult.getType() == HitResult.Type.ENTITY) {
				EntityHitResult entityResult = (EntityHitResult) hitResult;
				if (entityResult != null) {
					Entity hitEntity = entityResult.getEntity();
					Identifier id = EntityType.getId(hitEntity.getType());
					if (!this.hasEntity && !(hitEntity instanceof PlayerEntity || hitEntity instanceof EnderDragonEntity || hitEntity instanceof EnderDragonPart || PokeballConfig.getConfig().BLACKLIST.contains(id.toString()))) {
						boolean flag = hitEntity instanceof LivingEntity;
						if (PokeballConfig.getConfig().allowCaptureOfAllTypes && !flag)
							flag = true;
						if (flag) {
							NbtCompound ret = new NbtCompound();
							if (hitEntity.saveSelfNbt(ret)) {
								NbtCompound entity = hitEntity.writeNbt(ret);
								entity.putString("pokeball_name", hitEntity.getType().getTranslationKey());

								NBTHelper.putNbt(this.currentPokeball, "StoredEntity", entity);

								this.currentPokeball.setCount(1);
								if (hitEntity instanceof LivingEntity)
									hitEntity.discard();
								else hitEntity.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
							}
						}
					}
				}
			}
				this.dropStack(this.currentPokeball, 0.2F);
				this.world.sendEntityStatus(this, (byte) 3);
				this.removeFromDimension();
		}
	}

	@Override
	public void handleStatus(byte id) {
		if (id == 3) {
			for (int i = 0; i < 8; ++i) {
				this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, currentPokeball)/*ParticleTypes.ITEM_SNOWBALL*/, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	@Override
	protected Item getDefaultItem() {
		return PokeballMod.POKEBALL;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
