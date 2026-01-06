package yeelp.stoicdummy.client.screen;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.potion.PotionEffect;

final class GuiRemovePotionButton extends GuiButton {
	private final PotionEffect effect;
	static final int BUTTON_WIDTH = 15;
	public GuiRemovePotionButton(PotionEffect effect, int id, int x, int y) {
		super(id, x, y, BUTTON_WIDTH, BUTTON_WIDTH, "X");
		this.effect = effect;
	}
	
	PotionEffect getPotionEffect() {
		return this.effect;
	}
}