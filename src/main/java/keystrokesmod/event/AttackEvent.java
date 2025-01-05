package keystrokesmod.event;


import net.lenni0451.asmevents.event.IEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEvent implements IEvent {
	private final Entity target;
	private final EntityPlayer player;

	public AttackEvent(Entity target, EntityPlayer player) {
		this.target = target;
		this.player = player;
	}

	public EntityPlayer getPlayer() {
		return player;
	}

	public Entity getTarget() {
		return target;
	}
}
