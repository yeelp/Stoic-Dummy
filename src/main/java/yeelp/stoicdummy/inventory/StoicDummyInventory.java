package yeelp.stoicdummy.inventory;

import java.util.Arrays;
import java.util.Objects;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.util.InventoryUtils;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class StoicDummyInventory implements IInventory {

	EntityStoicDummy dummyOwner;
	
	public StoicDummyInventory(EntityStoicDummy dummy) {
		this.dummyOwner = Objects.requireNonNull(dummy);
	}
	
	@Override
	public String getName() {
		return this.dummyOwner.getName();
	}

	@Override
	public boolean hasCustomName() {
		return this.dummyOwner.hasCustomName();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.dummyOwner.getName());
	}

	@Override
	public int getSizeInventory() {
		return EntityEquipmentSlot.values().length;
	}

	@Override
	public boolean isEmpty() {
		return this.dummyOwner.hasEmptyInventory();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return InventoryUtils.getSlotFromSlotIndex(index).map(this.dummyOwner::getItemStackFromSlot).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if(count == 0) {
			return ItemStack.EMPTY;
		}
		return InventoryUtils.getSlotFromSlotIndex(index).map((slot) -> {
			ItemStack newStack = this.dummyOwner.getItemStackFromSlot(slot).splitStack(count);
			if(!newStack.isEmpty()) {
				this.markDirty();
			}
			return newStack;
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return InventoryUtils.getSlotFromSlotIndex(index).map((slot) -> {
			ItemStack stack = this.dummyOwner.getItemStackFromSlot(slot);
			this.dummyOwner.setItemStackToSlot(slot, ItemStack.EMPTY);
			return stack;
		}).orElse(ItemStack.EMPTY);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		InventoryUtils.getSlotFromSlotIndex(index).ifPresent((slot) -> {
			this.dummyOwner.setItemStackToSlot(slot, stack);
			if(!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
				stack.setCount(this.getInventoryStackLimit());
			}
			this.markDirty();
		});
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		//nothing
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		//nothing
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		//nothing
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return InventoryUtils.getSlotFromSlotIndex(index).filter((slot) -> {
			if(slot.getSlotType() == EntityEquipmentSlot.Type.HAND) {
				return true;
			}
			return stack.getItem().isValidArmor(stack, slot, this.dummyOwner);
		}).isPresent();
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		//nothing
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		Arrays.stream(EntityEquipmentSlot.values()).map(EntityEquipmentSlot::getSlotIndex).forEach(this::removeStackFromSlot);
	}
	


}
