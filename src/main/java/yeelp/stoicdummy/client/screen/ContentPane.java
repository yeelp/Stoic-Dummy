package yeelp.stoicdummy.client.screen;

import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import yeelp.stoicdummy.SDLogger;

public final class ContentPane<T> {

	private final List<T> contents;
	private final int x, y, width, height, individualContentHeight;
	private int scrollY = 0; 
	private boolean scrollBarClicked = false, dragging = false;
	private static final int SCROLLBAR_WIDTH = 10;
	
	public ContentPane(int x, int y, int width, int height, int contentHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.individualContentHeight = contentHeight;
		this.contents = Lists.newArrayList();
	}
	
	public void clear() {
		this.contents.clear();
	}
	
	public void add(T t) {
		this.contents.add(t);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getOffsetY() {
		return this.individualContentHeight;
	}
	
	public boolean isHovering(int mouseX, int mouseY) {
		return withinBounds(mouseX, mouseY, this.x, this.x + this.width, this.y, this.y + this.height);
	}
	
	public void scrollTop() {
		this.scrollY = 0;
	}
	
	public void scrollBot() {
		this.scrollY = Math.max(0, this.getContentsHeight() - this.height);
	}
	
	public void scrollUp() {
		this.scrollY = Math.max(this.scrollY - this.individualContentHeight, 0);
	}
	
	public void scrollDown() {
		this.scrollY = Math.min(this.scrollY + this.individualContentHeight, this.getContentsHeight() - this.height);
	}
	
	public int getContentsHeight() {
		return this.contents.size() * this.individualContentHeight;
	}
	
	public boolean scrollable() {
		return this.getContentsHeight() > this.height;
	}
	
	private void updateScrollBounds() {
		this.scrollY = MathHelper.clamp(this.scrollY, 0, this.getContentsHeight() - this.height);
	}
	
	public Iterable<T> getViewableContents() {
		this.updateScrollBounds();
		if(!this.scrollable()) {
			return this.contents;
		}
		int start = this.scrollY/this.individualContentHeight;
		int end = start + (this.height/this.individualContentHeight);
		return this.contents.subList(start, Math.min(this.contents.size() - 1, end));
	}
	
	private int getScrollbarHeight() {
		int viewCount = 0;
		for(@SuppressWarnings("unused") T t : this.getViewableContents()) {
			viewCount++;
		}
		return this.scrollable() ? (int) ((float) (viewCount) / this.contents.size() * this.height) : 0;
	}
	
	void handleMouseInput(int mouseX, int mouseY) {
		boolean mousePressed = Mouse.isButtonDown(0);
		int scrollBarXMin = this.x + this.width - SCROLLBAR_WIDTH;
		int scrollBarXMax = this.x + this.width;
		int scrollBarYMin = this.toScreenCoords(this.scrollY);
		int scrollBarYMax = this.toScreenCoords((this.scrollY)) + this.getScrollbarHeight();
		boolean withinScrollBar = withinBounds(mouseX, mouseY, scrollBarXMin, scrollBarXMax, scrollBarYMin, scrollBarYMax);
		boolean withinScrollBarLeniently = withinBounds(mouseX, mouseY, scrollBarXMin - 5, scrollBarXMax + 5, scrollBarYMin - 20, scrollBarYMax + 5);
		if(mousePressed) {
			SDLogger.debug("scrollbarHeight: {}, size: {}, x: {}, y: {}, xmin: {}, xmax: {}, ymin: {}, ymax: {}", this.getScrollbarHeight(), this.contents.size(), mouseX, mouseY, scrollBarXMin, scrollBarXMax, scrollBarYMin, scrollBarYMax);
			if(this.scrollBarClicked && withinScrollBarLeniently) {
				SDLogger.debug("dragging");
				this.dragging = true;
			}
			else {
				this.dragging = false;
			}
		}
		else {
			this.dragging = false;
		}
		this.scrollBarClicked = this.dragging || (withinScrollBar && mousePressed);
		if(this.dragging) {
			this.scrollY = this.toScrollCoords(mouseY);
			SDLogger.debug("{}", this.scrollY);
			this.updateScrollBounds();
		}
	}
	
	private static boolean withinBounds(int xIn, int yIn, int xMin, int xMax, int yMin, int yMax) {
		return xIn <= xMax && xIn >= xMin && yIn <= yMax && yIn >= yMin;
	}
	
	private int toScrollCoords(int yScreen) {
		float m = ((float) this.getContentsHeight() - this.height)/(this.height - this.getScrollbarHeight());
		float b = -this.y*m;
		return (int) ((m*yScreen + b));
	}
	
	private int toScreenCoords(int yScroll) {
		float m = ((float) this.getContentsHeight() - this.height)/(this.height - this.getScrollbarHeight());
		float b = -this.y*m;
		return (int) ((yScroll - b)/m);
	}
	
	private int getScrollBarXMin() {
		return this.x + this.width - SCROLLBAR_WIDTH;
	}
	
	private int getScrollBarXMax() {
		return this.x + this.width;
	}
	
	void draw() {
		if(!this.scrollable()) {
			return;
		}
		int x1 = this.getScrollBarXMin();
		int y1 = this.toScreenCoords(this.scrollY);
		//SDLogger.debug("{}, {}, {}, {}", x1, x2, y1, y2);
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.OPTIONS_BACKGROUND);
		Gui.drawModalRectWithCustomSizedTexture(x1, y1, 0, 0, SCROLLBAR_WIDTH, this.getScrollbarHeight(), 100, 100);
//		Tessellator tessel = Tessellator.getInstance();
//		BufferBuilder buffer = tessel.getBuffer();
//		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
//		buffer.color(128, 128, 128, 255);
//		buffer.pos(x2, y1, 0).tex(5, 0).endVertex();
//		buffer.pos(x1, y1, 0).tex(0, 0).endVertex();
//		buffer.pos(x1, y2, 0).tex(0, 5).endVertex();
//		buffer.pos(x2, y2, 0).tex(5, 5).endVertex();
//		tessel.draw();
//		Minecraft.getMinecraft().ingameGUI.drawRect(left, top, right, bottom, color);
		//y -> scrollY
		//this.y -> 0
		//this.y + this.height - this.getScrollBarHeight() -> this.contents.size() * this.individualContentHeight
	}
}
