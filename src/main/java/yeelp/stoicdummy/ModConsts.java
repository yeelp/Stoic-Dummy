package yeelp.stoicdummy;

import com.google.common.collect.Lists;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;

public interface ModConsts {
	String NAME = "Stoic Dummy";
	String MODID = "stoicdummy";
	String VERSION = "@version@";

	String CLIENT_PROXY = "yeelp.stoicdummy.proxy.ClientProxy";
	String SERVER_PROXY = "yeelp.stoicdummy.proxy.Proxy";
	
	Iterable<EntityEquipmentSlot> ARMOR_SLOTS = Lists.newArrayList(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET);
	Iterable<EntityEquipmentSlot> HAND_SLOTS = Lists.newArrayList(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);

	public interface DummyNBT {
		String HAND = "righthanded";
		String ROTATION = "rotation";
		String INVENTORY = "inventory";
		String CREATURE_ATTRIBUTE = "creatureattribute";
		String SLOT = "slot";
		String ITEM = "item";
		
		String POTIONS = "potioneffects";
		String POTION_NAME = "potion";
		String POTION_AMPLIFIER = "amplifier";
		
		String DAMAGE_HISTORY = "history";
		String TYPE = "type";
		String ATTACKER = "attacker";
		String TRUE_ATTACKER = "trueAttacker";
		String INITIAL_AMOUNT = "initialAmount";
		String FINAL_AMOUNT = "finalAmount";
		String SOURCE = "source";
		
		byte TAG_COMPOUND_ID = new NBTTagCompound().getId();
	}
	
	public interface TranslationKeys {
		String HISTORY_ROOT = "history";
		String ATTACKER = "attacker";
		String TRUE_ATTACKER = "trueAttacker";
		String SOURCE = "source";
		String DAMAGE_DEALT = "damageDealt";
		String DAMAGE_TAKEN = "damageTaken";
		
		String MOST_RECENT = "mostRecent";
		String LEAST_RECENT = "leastRecent";
		String NTH_RECENT = "nthRecent";
		
		String UI_ROOT = "ui";
		String CLEAR_HISTORY = "clearHistory";
		String HELP_TOOLTIP = "helpTooltip";
		String POTION_HELP = "potionHelp";
		String POTION_INPUT_HELP = "potionInputHelp";
		String POTION_EFFECTS_HELP = "potionEffectsHelp";
		String ATTRIBUTE_HELP = "attributeHelp";
		String CLEAR_HISTORY_HELP = "clearHelp";
	}
}