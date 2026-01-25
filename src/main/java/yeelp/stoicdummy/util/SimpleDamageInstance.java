package yeelp.stoicdummy.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.ModConsts.TranslationKeys;

public final class SimpleDamageInstance extends AbstractDamageInstance {

	public static final byte ID = (byte) 1;
	
	private static final ITextComponent DAMAGE_DEALT = TRANSLATOR.getComponent(TranslationKeys.DAMAGE_DEALT);
	private static final ITextComponent DAMAGE_TAKEN = TRANSLATOR.getComponent(TranslationKeys.DAMAGE_TAKEN);
	
	private SimpleDamageInstance(DamageSource src, float initialDamage, float finalDamage) {
		super(src, initialDamage, finalDamage);
	}
	
	public SimpleDamageInstance(NBTTagCompound nbt) {
		super(nbt);
	}
	
	@Override
	protected Collection<String> getSpecificInfoForIteration() {
		List<String> lines = Lists.newArrayList();
		lines.add(highlight(DAMAGE_DEALT.getFormattedText()) + SPLIT + AbstractDamageInstance.formatDamage(this.getInitialDamage()));
		lines.add(highlight(DAMAGE_TAKEN.getFormattedText()) + SPLIT + AbstractDamageInstance.formatDamage(this.getFinalDamage()));
		return lines;
	}

	@Override
	protected void writeSpecificNBT(NBTTagCompound compound) {
		compound.setByte(DummyNBT.TYPE, ID);
	}

	public static final class SimpleDamageInstanceBuilder extends AbstractDamageInstanceBuilder {
		public SimpleDamageInstanceBuilder(DamageSource src, float initialDamage) {
			super(src, initialDamage);
		}
		
		@SuppressWarnings("synthetic-access")
		@Override
		public AbstractDamageInstance build() {
			return new SimpleDamageInstance(this.getSource(), this.getInitialDamage(), this.getFinalDamage());
		}
	}
}
