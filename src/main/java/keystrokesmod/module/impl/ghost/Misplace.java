package keystrokesmod.module.impl.ghost;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;

public class Misplace extends Module {
	private SliderSetting range;

	public Misplace() {
		super("Misplace", Category.ghost, 0);
		this.registerSetting(range = new SliderSetting("Range", 3.1, 3.0, 6.0, 0.01));
	}

	@EventTarget
	public void onReceivePacket(ReceivePacketEvent event) {
		if (event.getPacket() instanceof S18PacketEntityTeleport) {
			S18PacketEntityTeleport s18 = (S18PacketEntityTeleport) event.getPacket();

			double x = s18.getX() / 32.0;
			double z = s18.getZ() / 32.0;
			double f = range.getInput() - 3;
			if (f == 0.0) {
				return;
			}

			double c = Math.hypot(mc.thePlayer.posX - x, mc.thePlayer.posZ - z);
			if (f > c) {
				f -= c;
			}

			float r = this.real1(x, z);

			double a = Math.cos(Math.toRadians(r + 90.0f));
			double b = Math.sin(Math.toRadians(r + 90.0f));

			x -= a * f;
			z -= b * f;

			try {
				ReflectUtil.s18PosX.set(event.getPacket(), MathHelper.floor_double(x * 32.0));
				ReflectUtil.s18PosZ.set(event.getPacket(), MathHelper.floor_double(z * 32.0));
			} catch (Exception ignored) {}
		}
	}

	private float real1(double ex, double ez) {
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
}
