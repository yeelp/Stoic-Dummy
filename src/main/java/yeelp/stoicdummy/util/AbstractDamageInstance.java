package yeelp.stoicdummy.util;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.ModConsts.TranslationKeys;
import yeelp.stoicdummy.util.Translations.Translator;

public abstract class AbstractDamageInstance {
	protected static final Translator TRANSLATOR = Translations.INSTANCE.getTranslator(TranslationKeys.HISTORY_ROOT);
	private static final ITextComponent SOURCE = TRANSLATOR.getComponent(TranslationKeys.SOURCE);
	protected static final ITextComponent ATTACKER = TRANSLATOR.getComponent(TranslationKeys.ATTACKER);
	protected static final ITextComponent TRUE_ATTACKER = TRANSLATOR.getComponent(TranslationKeys.TRUE_ATTACKER);
	protected static final String SPLIT = ": ";

	private final String attacker, trueAttacker, source;
	private final float amountBefore, amountAfter;
	
	private static final DecimalFormat FORMAT_TWO_DECIMALS = new DecimalFormat("##.##");
	private static final DecimalFormat SCI_NOTATE = new DecimalFormat("##.#E0");
	
	protected AbstractDamageInstance(DamageSource src, float amountBefore, float amountAfter) {
		this.attacker = mapIfNonNullElseGetDefault(src.getImmediateSource(), Entity::getName, "null");
		this.trueAttacker = mapIfNonNullElseGetDefault(src.getTrueSource(), Entity::getName, "null");
		this.source = src.damageType;
		this.amountBefore = amountBefore;
		this.amountAfter = amountAfter;
	}
	
	protected AbstractDamageInstance(NBTTagCompound nbt) {
		this.attacker = nbt.getString(DummyNBT.ATTACKER);
		this.trueAttacker = nbt.getString(DummyNBT.TRUE_ATTACKER);
		this.source = nbt.getString(DummyNBT.SOURCE);
		this.amountBefore = nbt.getFloat(DummyNBT.INITIAL_AMOUNT);
		this.amountAfter = nbt.getFloat(DummyNBT.FINAL_AMOUNT);
	}
	
	public String getSource() {
		return this.source;
	}
	
	public String getImmediateAttacker() {
		return this.attacker;
	}
	
	public String getTrueAttacker() {
		return this.trueAttacker;
	}
	
	public float getInitialDamage() {
		return this.amountBefore;
	}
	
	public float getFinalDamage() {
		return this.amountAfter;
	}
	
	protected static String formatDamage(float f) {
		return (f >= 1000 ? SCI_NOTATE : FORMAT_TWO_DECIMALS).format(f);
	}
	
	@SuppressWarnings("SameParameterValue")
    protected static <T, U> U mapIfNonNullElseGetDefault(T t, Function<T, U> function, U backup) {
		if(t != null) {
			return function.apply(t);
		}
		return backup;
	}
	
	protected Iterable<String> getAttackerStringComponents() {
		if(this.getImmediateAttacker().equals(this.getTrueAttacker())) {
			return Lists.newArrayList(ATTACKER.getFormattedText(), this.getImmediateAttacker());
		}
		return Lists.newArrayList(ATTACKER.getFormattedText(), this.getImmediateAttacker(), TRUE_ATTACKER.getFormattedText(), this.getTrueAttacker());
	}
	
	protected static String highlight(String s) {
		return TextFormatting.YELLOW+s+TextFormatting.RESET;
	}
	
	public final NBTTagCompound writeToNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setString(DummyNBT.ATTACKER, this.getImmediateAttacker());
		compound.setString(DummyNBT.TRUE_ATTACKER, this.getTrueAttacker());
		compound.setString(DummyNBT.SOURCE, this.getSource());
		compound.setFloat(DummyNBT.INITIAL_AMOUNT, this.getInitialDamage());
		compound.setFloat(DummyNBT.FINAL_AMOUNT, this.getFinalDamage());
		this.writeSpecificNBT(compound);
		return compound;
	}

	protected abstract void writeSpecificNBT(NBTTagCompound compound);
	
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<String> iter = this.linesIterator();
		sb.append(iter.next());
		while(iter.hasNext()) {
			sb.append(System.lineSeparator());
			sb.append(iter.next());
		}
		return sb.toString();
	}
	
	protected final Iterator<String> linesIterator() {
		List<String> lines = Lists.newArrayList();
		boolean highlight = true;
		StringBuilder sb = new StringBuilder();
		for(String s : this.getAttackerStringComponents()) {
			if(highlight) {
				sb = new StringBuilder();
				sb.append(highlight(s)).append(SPLIT);
			}
			else {
				sb.append(s);
				lines.add(sb.toString());
			}
			highlight = !highlight;
		}
		lines.add(highlight(SOURCE.getFormattedText()) + SPLIT + this.getSource());
		lines.addAll(this.getSpecificInfoForIteration());
		return lines.iterator();
	}

	protected abstract Collection<String> getSpecificInfoForIteration();
	
	public static abstract class AbstractDamageInstanceBuilder {
		private final DamageSource src;
		private final float amountBefore;
		private float amountAfter;
		
		protected AbstractDamageInstanceBuilder(DamageSource src, float amountBefore) {
			this.src = src;
			this.amountBefore = amountBefore;
		}
		
		public final void setFinalDamageTaken(float amount) {
			this.amountAfter = amount;
		}
		
		protected final DamageSource getSource() {
			return this.src;
		}
		
		protected final float getInitialDamage() {
			return this.amountBefore;
		}
		
		protected final float getFinalDamage() {
			return this.amountAfter;
		}
		
		public abstract AbstractDamageInstance build();
	}
}
