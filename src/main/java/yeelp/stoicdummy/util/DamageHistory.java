package yeelp.stoicdummy.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yeelp.stoicdummy.ModConsts.DDDConsts;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.ModConsts.TranslationKeys;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.StoicDummy;
import yeelp.stoicdummy.config.ModConfig;
import yeelp.stoicdummy.integration.ddd.DDDDamageInstance;
import yeelp.stoicdummy.util.Translations.Translator;

import javax.annotation.Nonnull;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

public final class DamageHistory implements Iterable<AbstractDamageInstance> {
	
	private static final Translator TRANSLATOR = Translations.INSTANCE.getTranslator(TranslationKeys.HISTORY_ROOT);

	private final Deque<AbstractDamageInstance> delegate;
	
	public DamageHistory() {
		this.delegate = Lists.newLinkedList();
	}

	public boolean add(AbstractDamageInstance e) {
		boolean result = this.delegate.add(e);
		if(this.size() > ModConfig.dummy.historyLength) {
			this.delegate.poll();
		}
		return result;
	}
	
	public int size() {
		return this.delegate.size();
	}
	
	public void clear() {
		this.delegate.clear();
	}
	
	public boolean isEmpty() {
		return this.size() == 0;
	}
	
	public Optional<AbstractDamageInstance> getLatestEntry() {
		return Optional.ofNullable(this.delegate.peekLast());
	}
	
	@Override
	@Nonnull
	public Iterator<AbstractDamageInstance> iterator() {
		return this.delegate.iterator();
	}
	
	public NBTTagList writeToNBT() {
		NBTTagList lst = new NBTTagList();
		this.forEach((instance) -> lst.appendTag(instance.writeToNBT()));
		return lst;
	}
	
	public void readFromNBT(NBTTagList lst) {
		this.clear();
		lst.forEach((nbt) -> {
			NBTTagCompound compound = (NBTTagCompound) nbt;
			byte type = compound.getByte(DummyNBT.TYPE);
			if(type == DDDConsts.DDD_DAMAGE_INSTANCE_ID && StoicDummy.hasDDD()) {
				this.add(new DDDDamageInstance(compound));
			}
			else if(type == SimpleDamageInstance.ID || !StoicDummy.hasDDD()) {
				this.add(new SimpleDamageInstance(compound));
			}
			else {
				SDLogger.err("Invalid damage instance ID! {}", type);				
			}
		});
	}
	
	public Iterable<String> getNumberedHistory() {
		return () -> DamageHistory.this.new NumberedDamageHistoryIterator();
	}
	
	private final class NumberedDamageHistoryIterator implements Iterator<String> {
		
		private final Iterator<AbstractDamageInstance> normalIt;
		private Iterator<String> damageInstanceIterator = null;
		boolean isNumbering = true;
		int total; 
		private final int historySize;
		
		NumberedDamageHistoryIterator() {
			this.normalIt = DamageHistory.this.iterator();
			this.total = DamageHistory.this.delegate.size();
			this.historySize = this.total;
		}
		
		@Override
		public boolean hasNext() {
			return this.normalIt.hasNext() || (this.damageInstanceIterator != null && this.damageInstanceIterator.hasNext());
		}
		
		@Override
		public String next() {
			if(this.isNumbering) {
				String s;
				if(this.total == this.historySize) {
					s = TRANSLATOR.getComponent(TranslationKeys.LEAST_RECENT).getUnformattedText();
				}
				else if (this.total == 1) {
					s = TRANSLATOR.getComponent(TranslationKeys.MOST_RECENT).getUnformattedText();
				}
				else {
					s = TRANSLATOR.getComponent(TranslationKeys.NTH_RECENT, StringUtils.number(this.total)).getUnformattedText();
				}
				this.isNumbering = !this.isNumbering;
				this.total--;
				return StringUtils.pad(s, '-', 21);
			}
			if(this.damageInstanceIterator == null) {
				this.damageInstanceIterator = Iterators.forArray(this.normalIt.next().toString().split(System.lineSeparator()));
			}
			String s = this.damageInstanceIterator.next();
			if(!this.damageInstanceIterator.hasNext()) {
				this.damageInstanceIterator = null;
				this.isNumbering = !this.isNumbering;
			}
			return s;
		}
	}

}
