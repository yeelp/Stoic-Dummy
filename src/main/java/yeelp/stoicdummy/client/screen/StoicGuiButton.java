package yeelp.stoicdummy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yeelp.stoicdummy.ModConsts;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
abstract class StoicGuiButton extends GuiButton {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ModConsts.MODID, "textures/gui/stoicuiwidgets.png");
    protected StoicGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    protected abstract int getU();

    protected abstract int getV();

    protected void drawText(Minecraft mc) {
        //empty
    }

    @Override
    @ParametersAreNonnullByDefault
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if(this.visible) {
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int u = this.getU();
            int v = this.getV();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            this.drawText(mc);
        }
    }
}
