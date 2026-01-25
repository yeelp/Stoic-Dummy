package yeelp.stoicdummy.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import yeelp.stoicdummy.ModConsts.DummyNBT;

public final class StoicDummyPotionMessage extends StoicDummyMessageContents {

	private final Potion potion;
	private final int amp;
	
	public StoicDummyPotionMessage(Potion potion, int amp) {
		this.potion = potion;
		this.amp = amp;
	}
	
	Potion getPotion() {
		return this.potion;
	}
	
	int getAmplifier() {
		return this.amp;
	}
	
	@SuppressWarnings("DataFlowIssue")
    @Override
	NBTTagCompound writeMessageContents() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString(DummyNBT.POTION_NAME, this.potion.getRegistryName().toString());
		tag.setInteger(DummyNBT.POTION_AMPLIFIER, this.amp);
		return tag;
	}
}
