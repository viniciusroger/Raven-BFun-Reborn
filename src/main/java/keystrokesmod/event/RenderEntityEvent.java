package keystrokesmod.event;

import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.lenni0451.asmevents.event.wrapper.TypedEvent;
import net.minecraft.entity.Entity;

public class RenderEntityEvent extends TypedEvent {
	private final Entity entity;

	public RenderEntityEvent(EnumEventType type, Entity entity) {
		super(type);
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
