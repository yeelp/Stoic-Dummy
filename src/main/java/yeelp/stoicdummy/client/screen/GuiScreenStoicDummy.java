package yeelp.stoicdummy.client.screen;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.Container;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import yeelp.stoicdummy.ModConsts.TranslationKeys;
import yeelp.stoicdummy.entity.EntityStoicDummy;
import yeelp.stoicdummy.inventory.ContainerStoicDummy;
import yeelp.stoicdummy.util.StringUtils;
import yeelp.stoicdummy.util.Translations;

@SideOnly(Side.CLIENT)
public final class GuiScreenStoicDummy extends GuiContainer {
	
	private GuiTextField inputPotion, inputAmp;
	private GuiButton ampUp, ampDown, addPotion, clearHistory;
	private ContentPane<GuiRemovePotionButton> potionPane;
	private ContentPane<String> historyPane;
	private List<GuiRadioButton> creatureAttributeButtons;
	private Set<HelpTooltip> helpTooltips;
	private final ContainerStoicDummy dummy;
	private final EnumCreatureAttribute startingCreatureAttribute;
	private final Supplier<String> nameFetcher;
	private static final int TEXT_COLOUR = 0xffffffff;
	private static final int HISTORY_LINE_HEIGHT = 10;
	private static final int POTION_EFFECT_MAX_LENGTH = 80;
	private static int trailingLength = -1;

	public GuiScreenStoicDummy(Container inventorySlotsIn, EntityStoicDummy dummy) {
		super(inventorySlotsIn);
		this.dummy = (ContainerStoicDummy) this.inventorySlots;
		this.creatureAttributeButtons = Lists.newArrayList();
		this.helpTooltips = Sets.newHashSet();
		this.startingCreatureAttribute = dummy.getCreatureAttribute();
		this.nameFetcher = () -> dummy.getDisplayName().getFormattedText();
	}
	
	@Override
	public void initGui() {
		super.initGui();
		trailingLength = trailingLength < 0 ? this.fontRenderer.getStringWidth("...") : trailingLength;
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		int inputPotionY = y - 10; 
		this.inputPotion = new GuiTextField(0, this.fontRenderer, x + 48, inputPotionY, 52, 12);
		this.inputAmp = new GuiTextField(1, this.fontRenderer, this.inputPotion.x + this.inputPotion.width + 6, inputPotionY, 28, 12);
		this.inputAmp.setValidator((s) -> s.chars().allMatch((c) -> Character.isDigit((char) c)));
		this.inputAmp.setMaxStringLength(3);
		
		int buttonId = 0;
		int ampY = (this.inputAmp.height + 2)/2;
		int ampX = this.inputAmp.x + this.inputAmp.width + 1;
		this.ampUp = new GuiButton(buttonId++, ampX, inputPotionY - 1, 10, ampY, "^");
		this.ampDown = new GuiButton(buttonId++, ampX, inputPotionY + ampY - 1, 10, ampY, "V");
		this.addPotion = new GuiButton(buttonId++, ampX + this.ampUp.width, inputPotionY - 1, ampY * 2, ampY * 2, "+");
		this.addPotion.enabled = false;
		this.addButton(this.addPotion);
		this.addButton(this.ampUp);
		this.addButton(this.ampDown);
		
		this.potionPane = new ContentPane<GuiRemovePotionButton>(this.inputPotion.x + 3, this.inputPotion.y + this.inputPotion.height + 15, 120, GuiRemovePotionButton.BUTTON_WIDTH * 5, GuiRemovePotionButton.BUTTON_WIDTH);
		
		int attributeX = x + 10, attributeY = y + 105;
		for(GuiRadioButton.CreatureAttributeDisplay display : GuiRadioButton.CreatureAttributeDisplay.values()) {
			GuiRadioButton button = new GuiRadioButton(display, buttonId++, attributeX, attributeY);
			if(display.getCreatureAttribute() == this.startingCreatureAttribute) {
				button.set();
			}
			this.creatureAttributeButtons.add(button);
			attributeY += GuiRadioButton.BUTTON_WIDTH;
		}
		this.creatureAttributeButtons.forEach(this::addButton);
		this.clearHistory = new GuiButton(buttonId++, this.addPotion.x + this.addPotion.width + 60, this.addPotion.y, 80, 20, Translations.INSTANCE.translate(TranslationKeys.UI_ROOT, TranslationKeys.CLEAR_HISTORY));
		this.addButton(this.clearHistory);
		
		this.historyPane = new ContentPane<String>(this.clearHistory.x + this.clearHistory.width/2 - 75, this.clearHistory.y + this.clearHistory.height + 5, 150, HISTORY_LINE_HEIGHT * 15, HISTORY_LINE_HEIGHT);
		
		this.helpTooltips.add(new HelpTooltip(TranslationKeys.POTION_HELP, this.inputPotion.x + this.inputPotion.width, this.inputPotion.y + HelpTooltip.HELP_TOOLTIP_Y_OFFSET));
		this.helpTooltips.add(new HelpTooltip(TranslationKeys.POTION_INPUT_HELP, this.addPotion.x + this.addPotion.width, this.addPotion.y + HelpTooltip.HELP_TOOLTIP_Y_OFFSET));
		this.helpTooltips.add(new HelpTooltip(TranslationKeys.POTION_EFFECTS_HELP, this.inputPotion.x, this.inputPotion.y + 18));
		this.helpTooltips.add(new HelpTooltip(TranslationKeys.CLEAR_HISTORY_HELP, this.clearHistory.x + this.clearHistory.width, this.clearHistory.y + HelpTooltip.HELP_TOOLTIP_Y_OFFSET));
		this.helpTooltips.add(new HelpTooltip(TranslationKeys.ATTRIBUTE_HELP, attributeX, attributeY - 4 * GuiRadioButton.BUTTON_WIDTH + HelpTooltip.HELP_TOOLTIP_Y_OFFSET));
		this.updateGUI();
		this.historyPane.scrollBot();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == Keyboard.KEY_RETURN && (this.inputPotion.isFocused() || this.inputAmp.isFocused())) {
			this.tryAddPotion();
		}
		else if (keyCode == Keyboard.KEY_TAB) {
			if(this.inputPotion.isFocused()) {
				this.inputPotion.setFocused(false);
				this.inputAmp.setFocused(true);
			}
			else if (this.inputAmp.isFocused()) {
				this.inputAmp.setFocused(false);
				this.inputPotion.setFocused(true);
			}
		}
		else if(this.inputPotion.textboxKeyTyped(typedChar, keyCode)) {
			this.dummy.updatePotionToAdd(this.inputPotion.getText());
		}
		else if (this.inputAmp.textboxKeyTyped(typedChar, keyCode)) {
			int newAmp = this.inputAmp.getText().isEmpty() ? 0 : Integer.parseInt(this.inputAmp.getText());
			this.updateAmplifier(newAmp);
		}
		else {
			super.keyTyped(typedChar, keyCode);			
		}
		this.addPotion.enabled = this.dummy.canAddPotionEffect();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.inputPotion.mouseClicked(mouseX, mouseY, mouseButton);
		this.inputAmp.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		ScaledResolution scaledRes = new ScaledResolution(this.mc);
		int x = Mouse.getX() * scaledRes.getScaledWidth() / this.mc.displayWidth;
		int y = scaledRes.getScaledHeight() - Mouse.getY() * scaledRes.getScaledHeight() / this.mc.displayHeight;
		this.potionPane.handleMouseInput(x, y);
		this.historyPane.handleMouseInput(x, y);
		int scroll = Mouse.getEventDWheel();
		if(scroll == 0) {
			return;
		}
		Consumer<ContentPane<?>> action = scroll < 0 ? ContentPane::scrollDown : ContentPane::scrollUp;
		if(this.potionPane.isHovering(x, y)) {
			action.accept(this.potionPane);
		}
		else if (this.historyPane.isHovering(x, y)) {
			action.accept(this.historyPane);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		int buttonClicked = button.id;
		if(buttonClicked == this.ampUp.id) {
			this.updateAmplifier(this.dummy.getAmplifier() + 1);
		}
		else if (buttonClicked == this.ampDown.id) {
			this.updateAmplifier(this.dummy.getAmplifier() - 1);
		}
		else if (buttonClicked == this.addPotion.id) {
			this.tryAddPotion();
		}
		else if (buttonClicked == this.clearHistory.id) {
			this.dummy.clearHistory();
			this.historyPane.scrollTop();
		}
		else if(button instanceof GuiRemovePotionButton) {
			this.dummy.removePotionEffect(((GuiRemovePotionButton) button).getPotionEffect().getPotion());
		}
		else if (button instanceof GuiRadioButton) {
			GuiRadioButton radioButton = (GuiRadioButton) button;
			this.dummy.setEnumCreatureAttribute(radioButton.getDisplay().getCreatureAttribute());
			this.creatureAttributeButtons.forEach(GuiRadioButton::unset);
			radioButton.set();
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.updateGUI();
		this.drawDefaultBackground();
		this.potionPane.draw();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		this.inputPotion.drawTextBox();
		this.inputAmp.drawTextBox();
		int y = this.potionPane.getY();
		this.potionPane.getViewableContents().forEach((button) -> {
			PotionEffect effect = button.getPotionEffect();
			this.drawString(this.fontRenderer, this.trimPotionToLength(new TextComponentTranslation(effect.getPotion().getName()).getFormattedText(), StringUtils.convertToRomanNumerals(effect.getAmplifier() + 1)), this.inputPotion.x + GuiRemovePotionButton.BUTTON_WIDTH + 5, button.y + button.height/3, TEXT_COLOUR);
		});
		this.historyPane.draw();
		y = this.historyPane.getY();
		for(String s : this.historyPane.getViewableContents()) {
			this.fontRenderer.drawString(s, this.historyPane.getX(), y, TEXT_COLOUR);
			y += this.historyPane.getOffsetY();
		}
		this.helpTooltips.forEach((tooltip) -> this.drawString(this.fontRenderer, "?", tooltip.getX(), tooltip.getY(), TEXT_COLOUR));
		GlStateManager.enableBlend();
		GlStateManager.enableLighting();
		Optional<HelpTooltip> helpTooltip = this.helpTooltips.stream().filter((tooltip) -> tooltip.isHovering(mouseX, mouseY)).findFirst();
		if(helpTooltip.isPresent()) {
			HelpTooltip tip = helpTooltip.get();
			this.drawHoveringText(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? tip.getTooltipText() : HelpTooltip.getDefaultText(), mouseX, mouseY);
		}
		else {
			this.renderHoveredToolTip(mouseX, mouseY);			
		}
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		return;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawString(this.nameFetcher.get(), 80, -40, TEXT_COLOUR);
	}
	
	private void updateGUI() {
		this.buttonList.stream().filter(GuiRemovePotionButton.class::isInstance).collect(Collectors.toList()).forEach(this.buttonList::remove);
		this.potionPane.clear();
		int x = this.potionPane.getX();
		int y = this.potionPane.getY();
		int id = 100;
		for(PotionEffect effect : this.dummy.getPotionEffects()) {
			if(effect == null) {
				break;
			}
			GuiRemovePotionButton button = new GuiRemovePotionButton(effect, id++, x, 0);
			this.potionPane.add(button);
		}
		for(GuiRemovePotionButton button : this.potionPane.getViewableContents()) {
			button.y = y;
			y += this.potionPane.getOffsetY();
			this.addButton(button);
		}
		this.potionPane.getViewableContents().forEach(this::addButton);
		this.historyPane.clear();
		this.dummy.getDamageHistoryText().forEach(this.historyPane::add);
	}
	
	private void updateAmplifier(int amp) {
		int cappedAmp = amp > 127 ? 127 : (amp < 0 ? 0 : amp);
		this.inputAmp.setText(String.valueOf(cappedAmp));
		this.dummy.updateAmplifierSelected(cappedAmp);
	}
	
	private void tryAddPotion() {
		if(this.dummy.canAddPotionEffect()) {
			this.dummy.addPotionEffect();
			this.inputPotion.setText("");
			this.inputAmp.setText("0");
			this.addPotion.enabled = false;
		}
	}
	
	private String trimToLength(String s, int length) {
		if(length < 0) {
			throw new IllegalArgumentException("length to trim can not be negative! "+length);
		}
		if(this.fontRenderer.getStringWidth(s) < length) {
			return s;
		}
		int[] lengths = new int[s.length()];
		for(int i = 0; i < lengths.length; i++) {
			lengths[i] = this.fontRenderer.getCharWidth(s.charAt(i));
		}
		int cutoff;
		int totalLength = 0;
		for(cutoff = 0; totalLength < length - trailingLength && cutoff < lengths.length; cutoff++) {
			totalLength += lengths[cutoff];
		}
		return s.substring(0, cutoff) + "...";
	}
	
	private String trimPotionToLength(String potionEffect, String amplifier) {
		return String.format("%s %s", this.trimToLength(potionEffect, POTION_EFFECT_MAX_LENGTH - this.fontRenderer.getStringWidth(amplifier)), amplifier);
	}

}
