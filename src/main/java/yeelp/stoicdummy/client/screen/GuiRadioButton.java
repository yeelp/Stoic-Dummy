package yeelp.stoicdummy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EnumCreatureAttribute;

final class GuiRadioButton extends GuiButton {
	
	static final int BUTTON_WIDTH = 10;
	enum CreatureAttributeDisplay {
		DEFAULT(EnumCreatureAttribute.UNDEFINED),
		UNDEAD(EnumCreatureAttribute.UNDEAD),
		ARTHROPOD(EnumCreatureAttribute.ARTHROPOD),
		ILLAGER(EnumCreatureAttribute.ILLAGER);
		
		private final EnumCreatureAttribute mapsTo;
		
		private CreatureAttributeDisplay(EnumCreatureAttribute attribute) {
			this.mapsTo = attribute;
		}
		
		EnumCreatureAttribute getCreatureAttribute() {
			return this.mapsTo;
		}
	}

	private boolean set = false;
	private final GuiRadioButton.CreatureAttributeDisplay display;
	public GuiRadioButton(GuiRadioButton.CreatureAttributeDisplay display, int buttonId, int x, int y) {
		super(buttonId, x, y, BUTTON_WIDTH, BUTTON_WIDTH, display.name());
		this.display = display;
	}
	
	void set() {
		this.set = true;
		this.enabled = false;
	}
	
	void unset() {
		this.set = false;
		this.enabled = true;
	}
	
	GuiRadioButton.CreatureAttributeDisplay getDisplay() {
		return this.display;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(this.visible) {
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
			int state = this.getHoverState(this.hovered);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(this.x, this.y, 0, 46 + state * 20, this.width / 2, this.height);
			this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + state * 20, this.width / 2, this.height);
			this.mouseDragged(mc, mouseX, mouseY);
			mc.fontRenderer.drawStringWithShadow(this.displayString, this.x + this.width + 3, this.y + (this.height - 8) / 2, 0xffffffff);
		}
	}
	
}