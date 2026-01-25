package yeelp.stoicdummy.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import yeelp.stoicdummy.entity.EntityStoicDummy;

public class StoicDummyUpdateMessageHandler implements IMessageHandler<StoicDummyUpdateMessage, IMessage> {

	@SuppressWarnings("DataFlowIssue")
    @Override
	public IMessage onMessage(StoicDummyUpdateMessage message, MessageContext ctx) {
		EntityStoicDummy dummy = (EntityStoicDummy) ctx.getServerHandler().player.world.getEntityByID(message.getDummyID());
		message.getMessageType().handle(dummy, message.getMessageContents());			
		return new StoicDummyStatusResponse(dummy);
	}
}
