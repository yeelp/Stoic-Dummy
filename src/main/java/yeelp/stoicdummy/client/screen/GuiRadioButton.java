package yeelp.stoicdummy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class GuiRadioButton extends StoicGuiButton {
	
	static final int BUTTON_SIZE = 10;
	enum CreatureAttributeDisplay {
		DEFAULT(EnumCreatureAttribute.UNDEFINED),
		UNDEAD(EnumCreatureAttribute.UNDEAD),
		ARTHROPOD(EnumCreatureAttribute.ARTHROPOD),
		ILLAGER(EnumCreatureAttribute.ILLAGER);
		
		private final EnumCreatureAttribute mapsTo;
		
		CreatureAttributeDisplay(EnumCreatureAttribute attribute) {
			this.mapsTo = attribute;
		}
		
		EnumCreatureAttribute getCreatureAttribute() {
			return this.mapsTo;
		}
	}

	private boolean set = false;
	private final GuiRadioButton.CreatureAttributeDisplay display;
	public GuiRadioButton(GuiRadioButton.CreatureAttributeDisplay display, int buttonId, int x, int y) {
		super(buttonId, x, y, BUTTON_SIZE, BUTTON_SIZE, display.name());
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

	@Override
	protected int getU() {
		return 0;
	}

	@Override
	protected int getV() {
		return this.set ? 20 : this.hovered ? 10 : 0;
	}

	@Override
	protected void drawText(Minecraft mc) {
		mc.fontRenderer.drawString(this.displayString, this.x + this.width + 3, this.y + (this.height - 8) / 2, 0x404040);
	}

	GuiRadioButton.CreatureAttributeDisplay getDisplay() {
		return this.display;
	}
	
}