package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;

public class Boost extends Module {
    public static DescriptionSetting c;
    public static SliderSetting a;
    public static SliderSetting b;
    private int i = 0;
    private boolean t = false;

    public Boost() {
        super("Boost", Category.movement, 0);
        this.registerSetting(c = new DescriptionSetting("20 ticks are in 1 second"));
        this.registerSetting(a = new SliderSetting("Multiplier", 2.0D, 1.0D, 3.0D, 0.05D));
        this.registerSetting(b = new SliderSetting("Time (ticks)", 15.0D, 1.0D, 80.0D, 1.0D));
    }

    public void onEnable() {
        if (ModuleManager.timer.isEnabled()) {
            this.t = true;
            ModuleManager.timer.disable();
        }

    }

    public void onDisable() {
        this.i = 0;
        if (GeneralUtils.getTimer().timerSpeed != 1.0F) {
            GeneralUtils.resetTimer();
        }

        if (this.t) {
            ModuleManager.timer.enable();
        }

        this.t = false;
    }

    public void onUpdate() {
        if (this.i == 0) {
            this.i = mc.thePlayer.ticksExisted;
        }

        GeneralUtils.getTimer().timerSpeed = (float) a.getInput();
        if ((double) this.i == (double) mc.thePlayer.ticksExisted - b.getInput()) {
            GeneralUtils.resetTimer();
            this.disable();
        }

    }
}
