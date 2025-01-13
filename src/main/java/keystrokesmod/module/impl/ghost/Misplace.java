package keystrokesmod.module.impl.ghost;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;

public class Misplace extends Module {
	public SliderSetting minRange, maxRange, fov;

	public Misplace() {
		super("Misplace", Category.ghost, 0);
		this.registerSetting(minRange = new SliderSetting("Min Range", 3.1, 3.0, 6.0, 0.01));
		this.registerSetting(maxRange = new SliderSetting("Max Range", 3.1, 3.0, 6.0, 0.01));
		this.registerSetting(fov = new SliderSetting("Fov", 180, 1, 180, 1));
	}

	public float real1(double ex, double ez) {
		double x = ex - mc.thePlayer.posX;
		double z = ez - mc.thePlayer.posZ;
		float y = (float)Math.toDegrees(-Math.atan(x / z));
		double degrees = Math.toDegrees(Math.atan(z / x));

		if (z < 0.0 && x < 0.0) {
			y = (float)(90.0 + degrees
			);
		}
		else if (z < 0.0 && x > 0.0) {
			y = (float)(-90.0 + degrees);
		}

		return y;
	}

	public double getFov(final float a, final float b) {
		float d = Math.abs(a - b) % 360.0f;
		if (d > 180.0f) {
			d = 360.0f - d;
		}
		return d;
	}
}
