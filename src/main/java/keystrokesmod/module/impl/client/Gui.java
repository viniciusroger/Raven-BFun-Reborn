package keystrokesmod.module.impl.client;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import org.lwjgl.input.Keyboard;

public class Gui extends Module {
    public static SliderSetting guiScale;
    public static ButtonSetting removePlayerModel;
    public static ButtonSetting translucentBackground;
    public static ButtonSetting removeWatermark;
    public static ButtonSetting rainBowOutlines;

    public Gui() {
        super("Gui", category.client, Keyboard.KEY_RSHIFT);

        this.registerSetting(rainBowOutlines = new ButtonSetting("Rainbow outlines", true));
        this.registerSetting(removePlayerModel = new ButtonSetting("Remove player model", false));
        this.registerSetting(removeWatermark = new ButtonSetting("Remove watermark", false));
        this.registerSetting(translucentBackground = new ButtonSetting("Translucent background", true));
    }

    public void onEnable() {
        if (Utils.nullCheck() && mc.currentScreen != Raven.clickGui) {
            mc.displayGuiScreen(Raven.clickGui);
            Raven.clickGui.initMain();
        }

        this.disable();
    }
}
