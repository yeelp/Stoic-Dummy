package yeelp.stoicdummy.network;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.nbt.NBTTagCompound;
import yeelp.stoicdummy.ModConsts.DummyNBT;

public final class StoicDummyCreatureAttributeMessage extends StoicDummyMessageContents {

	private final EnumCreatureAttribute attribute;
	
	public StoicDummyCreatureAttributeMessage(EnumCreatureAttribute attribute) {
		this.attribute = attribute;
	}
	
	public EnumCreatureAttribute getCreatureAttribute() {
		return this.attribute;
	}
	
	@Override
	NBTTagCompound writeMessageContents() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setByte(DummyNBT.CREATURE_ATTRIBUTE, (byte) this.attribute.ordinal());
		return tag;
	}

}
