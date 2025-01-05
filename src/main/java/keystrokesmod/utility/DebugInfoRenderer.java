package keystrokesmod.utility;

import keystrokesmod.Raven;
import keystrokesmod.event.Render2DEvent;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.Minecraft;

public class DebugInfoRenderer extends net.minecraft.client.gui.Gui {
    private static Minecraft mc = Minecraft.getMinecraft();

    @EventTarget
    public void onRenderTick(Render2DEvent ev) {
        if (!Raven.debugger || !Utils.nullCheck()) {
            return;
        }
        if (mc.currentScreen == null) {
            RenderUtils.renderBPS(true, true);
        }
    }
}
