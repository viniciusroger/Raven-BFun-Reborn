package keystrokesmod.module.impl.movement;

import keystrokesmod.ui.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;

public class Timer extends Module {
    private SliderSetting speed;
    private ButtonSetting strafeOnly;

    public Timer() {
        super("Timer", Category.movement, 0);
        this.registerSetting(speed = new SliderSetting("Speed", 1.0D, 0.5D, 2.5D, 0.01D));
        this.registerSetting(strafeOnly = new ButtonSetting("Strafe only", false));
    }

    public void onUpdate() {
        if (!(mc.currentScreen instanceof ClickGui)) {
            if (strafeOnly.isToggled() && mc.thePlayer.moveStrafing == 0) {
                GeneralUtils.resetTimer();
                return;
            }
            GeneralUtils.getTimer().timerSpeed = (float) speed.getInput();
        }
        else {
            GeneralUtils.resetTimer();
        }

    }

    public void onDisable() {
        GeneralUtils.resetTimer();
    }
}
