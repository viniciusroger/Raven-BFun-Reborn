package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;

public class ExtendedCamera extends Module {

	public SliderSetting thirdPersonDistance;
	private float lastDistance;

	public ExtendedCamera() {
		super("Camera Distance", Category.render, 0);

		this.registerSetting(new DescriptionSetting("Default distance is 4."));
		this.registerSetting(thirdPersonDistance = new SliderSetting("Third person distance", 4, 0, 40, 1));
	}

	public void onEnable() {
		setThirdPersonDistance((float) thirdPersonDistance.getInput());
	}

	public void onUpdate() {
		try {
			float input = (float) thirdPersonDistance.getInput();
			if (lastDistance != input) {
				setThirdPersonDistance(lastDistance = input);
			}
		} catch (Exception e) {
			GeneralUtils.sendMessage("&cThere was an issue setting third person distance.");
		}
	}

	public void onDisable() {
		setThirdPersonDistance(4.0f);
	}

	private void setThirdPersonDistance(float distance) {
		try {
			ReflectUtil.thirdPersonDistance.set(mc.entityRenderer, distance);
		} catch (Exception e) {
			e.printStackTrace();
			GeneralUtils.sendMessage("&cThere was an issue setting third person distance.");
		}
	}
}
