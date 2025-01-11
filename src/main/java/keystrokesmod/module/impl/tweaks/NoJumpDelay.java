package keystrokesmod.module.impl.tweaks;

import keystrokesmod.event.LivingUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.util.ReflectUtil;
import net.lenni0451.asmevents.event.EventTarget;

public class NoJumpDelay extends Module {
	public NoJumpDelay() {
		super("NoJumpDelay", Category.tweaks, 0);
	}

	@EventTarget
	public void onLivingUpdate(LivingUpdateEvent event) throws IllegalAccessException {
		ReflectUtil.jumpTicks.set(mc.thePlayer, 0);
	}
}
