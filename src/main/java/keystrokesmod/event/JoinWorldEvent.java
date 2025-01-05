package keystrokesmod.event;

import net.lenni0451.asmevents.event.IEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class JoinWorldEvent implements IEvent {
	private final World world;
	private final Entity entity;

	public JoinWorldEvent(World world, Entity entity) {
		this.entity = entity;
		this.world = world;
	}

	public World getWorld() {
		return world;
	}

	public Entity getEntity() {
		return entity;
	}
}
