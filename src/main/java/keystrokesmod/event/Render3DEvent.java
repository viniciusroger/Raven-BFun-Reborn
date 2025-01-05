package keystrokesmod.event;

import net.lenni0451.asmevents.event.IEvent;

public class Render3DEvent implements IEvent {
	private final float partialTicks;

	public Render3DEvent(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}
