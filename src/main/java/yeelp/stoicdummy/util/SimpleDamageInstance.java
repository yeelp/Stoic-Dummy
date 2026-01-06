package yeelp.stoicdummy.util;

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
	
	private static final ITextComponent SOURCE = TRANSLATOR.getComponent(TranslationKeys.SOURCE);
	private static final ITextComponent DAMAGE_DEALT = TRANSLATOR.getComponent(TranslationKeys.DAMAGE_DEALT);
	private static final ITextComponent DAMAGE_TAKEN = TRANSLATOR.getComponent(TranslationKeys.DAMAGE_TAKEN);
	
	private SimpleDamageInstance(DamageSource src, float initialDamage, float finalDamage) {
		super(src, initialDamage, finalDamage);
	}
	
	public SimpleDamageInstance(NBTTagCompound nbt) {
		super(nbt);
	}
	
	@Override
	protected Iterator<String> linesIterator() {
		List<String> lines = Lists.newArrayList();
		boolean highlight = true;
		StringBuilder sb = new StringBuilder();
		for(String s : this.getAttackerStringComponents()) {
			if(highlight) {
				sb = new StringBuilder();
				sb.append(highlight(s)+SPLIT);
			}
			else {
				sb.append(s);
				lines.add(sb.toString());
			}
			highlight = !highlight;
		}
		lines.add(highlight(SOURCE.getFormattedText()) + SPLIT + this.getSource());
		lines.add(highlight(DAMAGE_DEALT.getFormattedText()) + SPLIT + AbstractDamageInstance.formatDamage(this.getInitialDamage()));
		lines.add(highlight(DAMAGE_TAKEN.getFormattedText()) + SPLIT + AbstractDamageInstance.formatDamage(this.getFinalDamage()));
		return lines.iterator();
	}
	
	@Override
	public NBTTagCompound writeToNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setByte(DummyNBT.TYPE, ID);
		compound.setString(DummyNBT.ATTACKER, this.getImmediateAttacker());
		compound.setString(DummyNBT.TRUE_ATTACKER, this.getTrueAttacker());
		compound.setString(DummyNBT.SOURCE, this.getSource());
		compound.setFloat(DummyNBT.INITIAL_AMOUNT, this.getInitialDamage());
		compound.setFloat(DummyNBT.FINAL_AMOUNT, this.getFinalDamage());
		return compound;
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
