package keystrokesmod.module.impl.movement;

import keystrokesmod.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;

public class Timer extends Module {
    private SliderSetting speed;
    private ButtonSetting strafeOnly;

    public Timer() {
        super("Timer", Module.category.movement, 0);
        this.registerSetting(speed = new SliderSetting("Speed", 1.0D, 0.5D, 2.5D, 0.01D));
        this.registerSetting(strafeOnly = new ButtonSetting("Strafe only", false));
    }

    public void onUpdate() {
        if (!(mc.currentScreen instanceof ClickGui)) {
            if (strafeOnly.isToggled() && mc.thePlayer.moveStrafing == 0) {
                Utils.resetTimer();
                return;
            }
            Utils.getTimer().timerSpeed = (float) speed.getInput();
        }
        else {
            Utils.resetTimer();
        }

    }

    public void onDisable() {
        Utils.resetTimer();
    }
}
