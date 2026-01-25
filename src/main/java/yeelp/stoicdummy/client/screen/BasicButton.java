package yeelp.stoicdummy.client.screen;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BasicButton extends StoicGuiButton {

    private final int u, v;
    protected BasicButton(int buttonId, int x, int y, int widthIn, int heightIn, int u, int v) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.u = u;
        this.v = v;
    }

    @Override
    protected int getU() {
        return this.u;
    }

    @Override
    protected int getV() {
        return this.v + (!this.enabled ? 2 * this.height : this.hovered ? this.height : 0);
    }
}
