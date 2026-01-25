package yeelp.stoicdummy.client.screen;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class ContentPane<T> {

	private final List<T> contents;
	private final int x, y, width, height, individualContentHeight, itemsInView;
	private int scrollY = 0; 
	private boolean scrollBarClicked = false;
	private static final int SCROLLBAR_WIDTH = 10;
	
	public ContentPane(int x, int y, int width, int height, int contentHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.individualContentHeight = contentHeight;
		this.itemsInView = this.height/this.individualContentHeight;
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
		int start, end;
		if(this.scrollY == this.getContentsHeight() - this.height) {
			end = this.contents.size();
			start = end - this.itemsInView;
		}
		else {
			start = this.scrollY/this.individualContentHeight;
			end = Math.min(this.contents.size(), start + this.itemsInView);			
		}
		return this.contents.subList(start, end);
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
		boolean dragging;
		int scrollBarXMin = this.x + this.width - SCROLLBAR_WIDTH;
		int scrollBarXMax = this.x + this.width;
		int scrollBarYMin = this.toScreenCoords(this.scrollY);
		int scrollBarYMax = this.toScreenCoords((this.scrollY)) + this.getScrollbarHeight();
		boolean withinScrollBar = withinBounds(mouseX, mouseY, scrollBarXMin, scrollBarXMax, scrollBarYMin, scrollBarYMax);
		boolean withinScrollBarLeniently = withinBounds(mouseX, mouseY, scrollBarXMin - 5, scrollBarXMax + 5, scrollBarYMin - 20, scrollBarYMax + 5);
		if(mousePressed) {
			dragging = this.scrollBarClicked && withinScrollBarLeniently;
		}
		else {
			dragging = false;
		}
		this.scrollBarClicked = dragging || (withinScrollBar && mousePressed);
		if(dragging) {
			this.scrollY = this.toScrollCoords(mouseY);
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
	
	void draw() {
		if(!this.scrollable()) {
			return;
		}
		int x1 = this.getScrollBarXMin();
		int x2 = x1 + SCROLLBAR_WIDTH;
		int y1 = this.toScreenCoords(this.scrollY);
		int y2 = y1 + this.getScrollbarHeight();
		double u1 = 0, u2 = 10/256.0, v1 = 75/256.0, v2 = 90/256.0;
		double topV = 74/256.0, botV = 91/256.0;

		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(StoicGuiButton.TEXTURE);
		Tessellator tessel = Tessellator.getInstance();
		drawRect(tessel, x1, x2, y1, y2, u1, u2, v1, v2);
		drawRect(tessel, x1, x2, y1, y1 + 1, u1, u2, topV, topV + 1/256.0);
		drawRect(tessel, x1, x2, y2 - 1, y2, u1, u2, botV, botV + 1/256.0);
	}

	private static void drawRect(Tessellator tessel, int x1, int x2, int y1, int y2, double u1, double u2, double v1, double v2) {
		BufferBuilder buffer = tessel.getBuffer();
		buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(x1, y2, 0).tex(u1, v2).endVertex();
		buffer.pos(x2, y2, 0).tex(u2, v2).endVertex();
		buffer.pos(x2, y1, 0).tex(u2, v1).endVertex();
		buffer.pos(x1, y1, 0).tex(u1, v1).endVertex();
		tessel.draw();
	}
}
