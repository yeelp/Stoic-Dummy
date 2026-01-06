package yeelp.stoicdummy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import yeelp.stoicdummy.client.screen.GuiScreenStoicDummy;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.handler.NetworkHandler;
import yeelp.stoicdummy.inventory.ContainerStoicDummy;
import yeelp.stoicdummy.inventory.StoicDummyInventory;
import yeelp.stoicdummy.network.MessageType;
import yeelp.stoicdummy.proxy.Proxy;

@Mod(modid = ModConsts.MODID, name = ModConsts.NAME, version = ModConsts.VERSION)
public final class StoicDummy {

	@Instance
	public static StoicDummy instance;
	
	@SidedProxy(clientSide = ModConsts.CLIENT_PROXY, serverSide = ModConsts.SERVER_PROXY)
	public static Proxy proxy;
	
	@SuppressWarnings("static-method")
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		SDLogger.init(event.getModLog());
		SDLogger.info("Stoic Dummy is version: {}", ModConsts.VERSION);
		SDLogger.debug("debug mode enabled");
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new IGuiHandler() {

			@Override
			public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				EntityStoicDummy dummy = this.getStoicDummy(ID, world);
				MessageType.STATUS_REQUEST.sendMessage(dummy);
				return new GuiScreenStoicDummy(new ContainerStoicDummy(player.inventory, new StoicDummyInventory(dummy)), dummy);
			}

			@Override
			public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
				return new ContainerStoicDummy(player.inventory, new StoicDummyInventory(this.getStoicDummy(ID, world)));
			}
			
			private EntityStoicDummy getStoicDummy(int id, World world) {
				Entity entity = world.getEntityByID(id);
				if(entity instanceof EntityStoicDummy) {
					return (EntityStoicDummy) entity;
				}
				throw new RuntimeException("Could not find correct Stoic Dummy to load inventory for!");
			}
			
		});
		proxy.preInit();
	}

	@SuppressWarnings("static-method")
	@EventHandler
	public void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
		NetworkHandler.init();
		proxy.init();
	}
	
	@SuppressWarnings("static-method")
	@EventHandler
	public void postInit(@SuppressWarnings("unused") FMLPostInitializationEvent event) {
		proxy.postInit();
	}
}
