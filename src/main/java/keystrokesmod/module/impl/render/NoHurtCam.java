package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;

public class NoHurtCam extends Module {
    public SliderSetting mode, multiplier;

    public NoHurtCam() {
        super("NoHurtCam", Category.render);

        this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Cancel", "Custom"}, 0));
        this.registerSetting(new DescriptionSetting("Default is 14x multiplier."));
        this.registerSetting(multiplier = new SliderSetting("Multiplier", 14, -40, 40, 1));
    }
}
