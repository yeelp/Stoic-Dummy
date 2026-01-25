package yeelp.stoicdummy.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import yeelp.stoicdummy.SDLogger;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.render.entity.RenderStoicDummy;

@SuppressWarnings("unused")
public final class ClientProxy extends Proxy {
	
	@SuppressWarnings("DataFlowIssue")
    @Override
	public void preInit() {
		super.preInit();
		ModelLoader.setCustomModelResourceLocation(Proxy.dummyItem, 0, new ModelResourceLocation(Proxy.dummyItem.getRegistryName(), "inventory"));
		RenderingRegistry.registerEntityRenderingHandler(EntityStoicDummy.class, RenderStoicDummy::new);
		SDLogger.info("Registered Textures and Models");
	}
	
	@Override
	public void init() {
		super.init();
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		manager.entityRenderMap.put(EntityStoicDummy.class, new RenderStoicDummy(manager));
	}
}
