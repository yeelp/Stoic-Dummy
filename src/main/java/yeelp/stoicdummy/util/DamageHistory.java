package yeelp.stoicdummy.util;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.config.ModConfig;

public final class DamageHistory implements Iterable<AbstractDamageInstance> {

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
			if(type == SimpleDamageInstance.ID) {
				this.add(new SimpleDamageInstance(compound));				
			}
			else {
				SDLogger.err("Invalid damage instance ID! {}", type);				
			}
		});
	}

}
