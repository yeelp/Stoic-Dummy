package yeelp.stoicdummy.client.screen;

import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class GuiRemovePotionButton extends StoicGuiButton {
	private final PotionEffect effect;
	static final int BUTTON_SIZE = 15;
	public GuiRemovePotionButton(PotionEffect effect, int id, int x, int y) {
		super(id, x, y, BUTTON_SIZE, BUTTON_SIZE, "X");
		this.effect = effect;
	}
	
	PotionEffect getPotionEffect() {
		return this.effect;
	}

	@Override
	protected int getU() {
		return 0;
	}

	@Override
	protected int getV() {
		return 44 + (this.hovered ? BUTTON_SIZE : 0);
	}
}