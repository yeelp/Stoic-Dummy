package yeelp.stoicdummy.integration.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import yeelp.stoicdummy.client.screen.GuiScreenStoicDummy;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;

@JEIPlugin
@SuppressWarnings("unused")
public class JEIPluginStoicDummy implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.addAdvancedGuiHandlers(new IAdvancedGuiHandler<GuiScreenStoicDummy>() {
            @Override
            @Nonnull
            public Class<GuiScreenStoicDummy> getGuiContainerClass() {
                return GuiScreenStoicDummy.class;
            }

            @Override
            public List<Rectangle> getGuiExtraAreas(@Nonnull GuiScreenStoicDummy guiContainer) {
                return Lists.newArrayList(new Rectangle(160, 30, guiContainer.width/2 + 30, guiContainer.height));
            }
        });
    }
}
