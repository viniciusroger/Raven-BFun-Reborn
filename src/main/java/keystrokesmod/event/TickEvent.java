package keystrokesmod.event;

import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.lenni0451.asmevents.event.wrapper.TypedEvent;

public class TickEvent extends TypedEvent {
	public TickEvent(EnumEventType type) {
		super(type);
	}
}
