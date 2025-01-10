package keystrokesmod.ui.debuginfo;

import keystrokesmod.Raven;
import keystrokesmod.event.Render2DEvent;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.Minecraft;

public class DebugInfoRenderer extends net.minecraft.client.gui.Gui {
    private static Minecraft mc = Minecraft.getMinecraft();

    @EventTarget
    public void onRenderTick(Render2DEvent ev) {
        if (!Raven.debugger) {
            return;
        }

        if (mc.currentScreen == null) {
            RenderUtils.renderBPS(true, true);
        }
    }
}
