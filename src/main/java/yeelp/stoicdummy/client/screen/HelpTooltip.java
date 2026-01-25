package yeelp.stoicdummy.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yeelp.stoicdummy.ModConsts.TranslationKeys;
import yeelp.stoicdummy.util.Translations;
import yeelp.stoicdummy.util.Translations.Translator;

@SideOnly(Side.CLIENT)
final class HelpTooltip {
	private static final Translator TRANSLATOR = Translations.INSTANCE.getTranslator("ui");
	private static final int HOVER_WIDTH = Minecraft.getMinecraft().fontRenderer.getCharWidth('?');
	private static final int HOVER_HEIGHT = 8;
	static final int HELP_TOOLTIP_Y_OFFSET = -11;
	private final String key;
	private final int x, y;
	
	public HelpTooltip(String key, int x, int y) {
		this.key = key;
		this.x = x;
		this.y = y;
	}
	
	String getTooltipText() {
		return TRANSLATOR.translate(this.key);
	}
	
	boolean isHovering(int mouseX, int mouseY) {
		return mouseX > this.x - 2 && this.x + HOVER_WIDTH > mouseX && mouseY > this.y - 2 && this.y + HOVER_HEIGHT > mouseY;
	}
	
	int getX() {
		return this.x;
	}
	
	int getY() {
		return this.y;
	}
	
	static String getDefaultText() {
		return TRANSLATOR.translate(TranslationKeys.HELP_TOOLTIP);
	}
	
}