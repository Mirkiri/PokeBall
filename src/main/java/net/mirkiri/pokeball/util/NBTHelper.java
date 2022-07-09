package net.mirkiri.pokeball.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class NBTHelper {

	public static boolean hasNbt(ItemStack itemStack, String keyName) {
		return !itemStack.isEmpty() && itemStack.getNbt() != null && itemStack.getNbt().contains(keyName);
	}
	/**
	 * Initializes the NBT Tag Compound for the given ItemStack if it is null
	 * 
	 * @param itemStack The ItemStack for which its NBT Tag Compound is being
	 *                  checked for initialization
	 */
	private static void initCompoundNBT(ItemStack itemStack) {
		if (itemStack.getNbt() == null) {
			itemStack.setNbt(new NbtCompound());
		}
	}

	public static NbtCompound getNbt(ItemStack stack, String keyName) {
		initCompoundNBT(stack);

		if (!stack.getNbt().contains(keyName)) {
			putNbt(stack, keyName, new NbtCompound());
		}

		return stack.getNbt().getCompound(keyName);
	}

	public static void putNbt(ItemStack stack, String keyName, NbtCompound compound) {
		initCompoundNBT(stack);

		stack.getNbt().put(keyName, compound);
	}

	public static void removeNbt(ItemStack stack, String keyName) {
		if (stack.getNbt() != null) {
			stack.getNbt().remove(keyName);
		}
	}

}