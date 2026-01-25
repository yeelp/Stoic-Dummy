package yeelp.stoicdummy.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yeelp.stoicdummy.entity.EntityStoicDummy;

public class StoicDummyStatusResponse implements IMessage {
	
	int id;
	NBTTagCompound tag;
	
	public StoicDummyStatusResponse() {
		//empty intentionally
	}
	
	StoicDummyStatusResponse(EntityStoicDummy dummy) {
		this.id = dummy.getEntityId();
		this.tag = dummy.serializeNBT();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer pbuf = new PacketBuffer(buf);
		this.id = pbuf.readInt();
		try {
			this.tag = pbuf.readCompoundTag();
		}
		catch(IOException e) {
			throw new RuntimeException("Failed to read NBT Tag Compound.", e);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer pbuf = new PacketBuffer(buf);
		pbuf.writeInt(this.id);
		pbuf.writeCompoundTag(this.tag);
	}
	
	public static final class StoicDummyStatusResponseReceiver implements IMessageHandler<StoicDummyStatusResponse, IMessage> {
		@SuppressWarnings("DataFlowIssue")
        @Override
		public IMessage onMessage(StoicDummyStatusResponse message, MessageContext ctx) {
			FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> ((EntityStoicDummy) FMLClientHandler.instance().getWorldClient().getEntityByID(message.id)).readEntityFromNBT(message.tag));
			return null;
		}
	}

}
