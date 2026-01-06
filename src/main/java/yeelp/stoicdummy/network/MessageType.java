package yeelp.stoicdummy.network;

import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import yeelp.stoicdummy.ModConsts.DummyNBT;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.handler.NetworkHandler;

public enum MessageType {
	ADD_POTION {
		@Override
		StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag) {
			return new StoicDummyPotionMessage(Potion.getPotionFromResourceLocation(tag.getString(DummyNBT.POTION_NAME)), tag.getInteger(DummyNBT.POTION_AMPLIFIER));
		}
		
		@Override
		void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
			StoicDummyPotionMessage msg = (StoicDummyPotionMessage) contents;
			dummy.addPermanentPotionEffect(msg.getPotion(), msg.getAmplifier());
		}
	},
	REMOVE_POTION {
		@Override
		StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag) {
			return ADD_POTION.decodeMessageContents(tag);
		}
		
		@Override
		void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
			StoicDummyPotionMessage msg = (StoicDummyPotionMessage) contents;
			dummy.removePermanentPotionEffect(msg.getPotion());
		}
	},
	SET_ATTRIBUTE {
		@Override
		StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag) {
			return new StoicDummyCreatureAttributeMessage(EnumCreatureAttribute.values()[tag.getByte(DummyNBT.CREATURE_ATTRIBUTE)]);
		}
		
		@Override
		void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
			StoicDummyCreatureAttributeMessage msg = (StoicDummyCreatureAttributeMessage) contents;
			dummy.setEnumCreatureAttribute(msg.getCreatureAttribute());
		}
	},
	CLEAR_HISTORY {
		@Override
		StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag) {
			return null;
		}
		
		@Override
		void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
			dummy.clearDamageHistory();
		}
		
		
	},
	STATUS_REQUEST {
		@Override
		StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag) {
			return null;
		}
		
		@Override
		void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
			return;
		}
	};
	
	abstract void handle(EntityStoicDummy dummy, StoicDummyMessageContents contents);
	
	abstract StoicDummyMessageContents decodeMessageContents(NBTTagCompound tag);
	
	byte encodeType() {
		return (byte) this.ordinal();
	}
	
	public void sendMessage(EntityStoicDummy dummy, StoicDummyMessageContents contents) {
		NetworkHandler.INSTANCE.sendToServer(new StoicDummyUpdateMessage(this, dummy, contents));
	}
	
	public void sendMessage(EntityStoicDummy dummy) {
		this.sendMessage(dummy, new StoicDummyEmptyMessage());
	}
	
	static MessageType decodeType(byte b) {
		return MessageType.values()[b];
	}
	
}
