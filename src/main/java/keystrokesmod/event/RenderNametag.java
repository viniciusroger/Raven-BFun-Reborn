package keystrokesmod.event;

import net.lenni0451.asmevents.event.wrapper.CancellableEvent;
import net.minecraft.entity.Entity;

public class RenderNametag extends CancellableEvent {
	private final Entity entity;

	public RenderNametag(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
