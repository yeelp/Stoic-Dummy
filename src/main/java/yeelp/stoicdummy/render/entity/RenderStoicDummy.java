package yeelp.stoicdummy.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yeelp.stoicdummy.ModConsts;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.render.model.ModelStoicDummy;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public final class RenderStoicDummy extends RenderLivingBase<EntityStoicDummy> {

	enum Texture {
		BASE("dummy"),
		FACE("dummyeyes");
		
		private final String filename;
		
		Texture(String filename) {
			this.filename = filename;
		}
		
		private String getPath() {
			return String.format("textures/entity/%s.png", this.filename);
		}
		
		final ResourceLocation toResourceLocation() {
			return new ResourceLocation(ModConsts.MODID, this.getPath());
		}
	}
	
	private static final ResourceLocation TEXTURE = Texture.BASE.toResourceLocation();
	
	public RenderStoicDummy(RenderManager manager) {
		super(manager, new ModelStoicDummy(), 0.5f);
		this.addLayer(new LayerDummyFace(this));
	}
	
	@Override
	protected boolean canRenderName(EntityStoicDummy entity) {
		return super.canRenderName(entity) && (entity.getAlwaysRenderNameTagForRender() || (entity.hasCustomName() && this.renderManager.pointedEntity == entity));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityStoicDummy entity) {
		return TEXTURE;
	}
	
	@SideOnly(Side.CLIENT)
	private static final class LayerDummyFace implements LayerRenderer<EntityStoicDummy> {
		private final RenderStoicDummy dummyRender;
		private final ResourceLocation texture = Texture.FACE.toResourceLocation();
		
		public LayerDummyFace(RenderStoicDummy render) {
			this.dummyRender = render;
		}

		@SuppressWarnings({"IntegerDivisionInFloatingPointContext", "ConstantValue"})
        @Override
		public void doRenderLayer(EntityStoicDummy entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			//taken from LayerSpiderEyes#doRenderLayer
			this.dummyRender.bindTexture(this.texture);
	        GlStateManager.enableBlend();
	        GlStateManager.disableAlpha();
	        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
	        GlStateManager.depthMask(!entitylivingbaseIn.isInvisible());

	        int i = 61680;
	        int j = i % 65536;
	        int k = i / 65536;
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
	        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
	        this.dummyRender.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
	        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
	        i = entitylivingbaseIn.getBrightnessForRender();
	        j = i % 65536;
	        k = i / 65536;
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
	        int brightness = entitylivingbaseIn.getBrightnessForRender();
	        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 65536, brightness / 65536);
	        GlStateManager.disableBlend();
	        GlStateManager.enableAlpha();
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
		
	}
	
}
