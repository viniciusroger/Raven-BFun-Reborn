package keystrokesmod.module.impl.tweaks;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;

public class NoHitDelay extends Module {
	public SliderSetting mode;

	public NoHitDelay() {
		super("NoHitDelay", Category.tweaks);

		this.registerSetting(mode = new SliderSetting("Mode", new String[]{"All", "Partial"}, 1));
	}
}
