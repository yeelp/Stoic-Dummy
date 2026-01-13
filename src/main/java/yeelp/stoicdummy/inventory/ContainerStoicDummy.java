package yeelp.stoicdummy.inventory;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;
import yeelp.stoicdummy.ModConsts;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.network.MessageType;
import yeelp.stoicdummy.network.StoicDummyCreatureAttributeMessage;
import yeelp.stoicdummy.network.StoicDummyPotionMessage;

public class ContainerStoicDummy extends Container {
	
	private final StoicDummyInventory inventory;
	private String[] potionEffectToAdd;
	private int amplifierSelected = -1;
	private List<DummySlot> dummySlots;
	private static final int SLOT_WIDTH = 18;
	private static final int X_PADDING = 8;
	private static final int Y_PADDING = 172;
	private static final int HOTBAR_Y_OFFSET = 40;
	private static final int DUMMY_INV_Y_OFFSET = -84;
	
	public ContainerStoicDummy(IInventory playerInventory, StoicDummyInventory dummyInventory) {
		this.inventory = dummyInventory;
		this.dummySlots = Lists.newArrayList();
		
		ModConsts.ARMOR_SLOTS.forEach((slot) -> this.dummySlots.add(new DummySlot.ArmorSlot(dummyInventory, slot)));
		ModConsts.HAND_SLOTS.forEach((slot) -> this.dummySlots.add(new DummySlot.HandSlot(dummyInventory, slot)));
		this.dummySlots.forEach(this::addSlotToContainer);
		
		for(int row = 0; row < 3; row++) {
			for(int index = 0; index < 9; this.addSlotToContainer(new Slot(playerInventory, index + (row + 1) * 9, X_PADDING + index++ * SLOT_WIDTH, Y_PADDING + (row - 1) * SLOT_WIDTH)));
		}
		
		for(int index = 0; index < 9; this.addSlotToContainer(new Slot(playerInventory, index, X_PADDING + index++ * SLOT_WIDTH, Y_PADDING + HOTBAR_Y_OFFSET)));
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return !this.inventory.dummyOwner.isDead && this.inventory.dummyOwner.getDistance(playerIn) < 8.0f;
	}
	
	public void updatePotionToAdd(String input) {
		this.potionEffectToAdd = input.split(":");
	}
	
	public void updateAmplifierSelected(int amplifier) {
		this.amplifierSelected = amplifier;
	}
	
	public int getAmplifier() {
		return this.amplifierSelected;
	}
	
	public boolean canAddPotionEffect() {
		if(this.potionEffectToAdd == null) {
			return false;
		}
		if(this.potionEffectToAdd.length != 2) {
			return false;
		}
		Potion potion = ForgeRegistries.POTIONS.getValue(this.createPotionResourceLocation());
		if(potion == null) {
			return false;
		}
		if(this.amplifierSelected < 0) {
			return false;
		}
		return !this.inventory.dummyOwner.hasPermanentPotionActive(potion, this.amplifierSelected);
	}
	
	public void addPotionEffect() {
		Potion potion = ForgeRegistries.POTIONS.getValue(this.createPotionResourceLocation());
		if(potion == null) {
			SDLogger.err("Potion from registry didn't exist but should be checked for beforehand! Potion was: {}", Arrays.toString(this.potionEffectToAdd));
			return;
		}
		MessageType.ADD_POTION.sendMessage(this.inventory.dummyOwner, new StoicDummyPotionMessage(potion, this.amplifierSelected));
		this.updatePotionToAdd("");
		this.updateAmplifierSelected(0);
	}
	
	public void removePotionEffect(Potion potion) {
		MessageType.REMOVE_POTION.sendMessage(this.inventory.dummyOwner, new StoicDummyPotionMessage(potion, 0));
	}
	
	private ResourceLocation createPotionResourceLocation() {
		return new ResourceLocation(this.potionEffectToAdd[0], this.potionEffectToAdd[1]);
	}
	
	public void setEnumCreatureAttribute(EnumCreatureAttribute attribute) {
		MessageType.SET_ATTRIBUTE.sendMessage(this.inventory.dummyOwner, new StoicDummyCreatureAttributeMessage(attribute));
	}
	
	public void clearHistory() {
		MessageType.CLEAR_HISTORY.sendMessage(this.inventory.dummyOwner);
	}
	
	public Iterable<String> getDamageHistoryText() {
		return this.inventory.dummyOwner.getDamageHistoryNumbered();
	}
	
	public Iterable<PotionEffect> getPotionEffects() {
		List<PotionEffect> effects = Lists.newArrayList();
		this.inventory.dummyOwner.getPermanentPotions().forEach((potion) -> {
			effects.add(this.inventory.dummyOwner.getActivePotionEffect(potion));
		});
		return effects;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slot = this.getSlot(index);
		ItemStack copy = ItemStack.EMPTY;
		if(slot != null && slot.getHasStack()) {
			ItemStack stack = slot.getStack();
			copy = stack.copy();
			if(slot instanceof DummySlot && !this.mergeItemStack(stack, 6, this.inventorySlots.size(), false)) {
				return ItemStack.EMPTY;
			}
			else if(!mergeItemStack(stack, 0, 6, false)){
				return ItemStack.EMPTY;
			}
			if(stack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			}
			else {
				slot.onSlotChanged();
			}
			if(copy.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
		}
		return copy;
	}
	
	private static abstract class DummySlot extends Slot {
		protected final EntityEquipmentSlot slot;
		public DummySlot(IInventory inventory, EntityEquipmentSlot slot, int x, int y) {
			super(inventory, slot.getSlotIndex(), x, y);
			this.slot = slot;
		}
		
		protected abstract boolean isAffectedByBindingCurse();
		
		@Override
		public abstract int getSlotStackLimit();
		
		@Override
		public abstract boolean isItemValid(ItemStack stack);
		
		@Override
		public boolean canTakeStack(EntityPlayer playerIn) {
			if(this.isAffectedByBindingCurse()) {
				ItemStack stack = this.getStack();
				return !stack.isEmpty() && (playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(stack));
			}
			return super.canTakeStack(playerIn);
		}
		
		@SideOnly(Side.CLIENT)
		@Override
		public abstract String getSlotTexture();
		
		private static final class HandSlot extends DummySlot {
			
			public HandSlot(IInventory inventory, EntityEquipmentSlot slot) {
				super(inventory, slot, X_PADDING + SLOT_WIDTH, computeYPos(slot.getIndex() + 1));
			}
			
			@Override
			public int getSlotStackLimit() {
				return 64;
			}
			
			@Override
			protected boolean isAffectedByBindingCurse() {
				return false;
			}
			
			@Override
			public boolean isItemValid(ItemStack stack) {
				return true;
			}
			
			@SideOnly(Side.CLIENT)
			@Override
			public String getSlotTexture() {
				if(this.slotNumber == EntityEquipmentSlot.OFFHAND.getSlotIndex()) {
					return "minecraft:items/empty_armor_slot_shield";
				}
				return null;
			}
		}
		
		
		private static final class ArmorSlot extends DummySlot {
			public ArmorSlot(IInventory inventory, EntityEquipmentSlot slot) {
				super(inventory, slot, X_PADDING, computeYPos(slot.getSlotIndex()));
			}
			
			@Override
			public int getSlotStackLimit() {
				return 1;
			}
			
			@Override
			protected boolean isAffectedByBindingCurse() {
				return true;
			}
			
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.getItem().isValidArmor(stack, this.slot, ((StoicDummyInventory) this.inventory).dummyOwner);
			}
			
			@SideOnly(Side.CLIENT)
			@Override
			public String getSlotTexture() {
				return ItemArmor.EMPTY_SLOT_NAMES[this.slot.getIndex()];
			}
			
		}
		
		static int computeYPos(int index) {
			return Y_PADDING - index * SLOT_WIDTH + DUMMY_INV_Y_OFFSET;
		}
	}
}
