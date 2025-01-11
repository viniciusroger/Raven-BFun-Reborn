package keystrokesmod.event;

import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.lenni0451.asmevents.event.types.ICancellableEvent;
import net.lenni0451.asmevents.event.wrapper.TypedEvent;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class RenderEntityEvent extends TypedEvent implements ICancellableEvent {
	private final Entity entity;
	private final RendererLivingEntity renderer;
	private boolean cancelled;
	private final float partialTicks;
	private double x, y, z;

	public RenderEntityEvent(EnumEventType type, Entity entity, RendererLivingEntity renderer, double x, double y, double z, float partialTicks) {
		super(type);
		this.entity = entity;
		this.renderer = renderer;
		this.x = x;
		this.y = y;
		this.z = z;
		this.partialTicks = partialTicks;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setPos(Vec3 pos) {
		this.x = pos.xCoord;
		this.y = pos.yCoord;
		this.z = pos.zCoord;
	}

	public Entity getEntity() {
		return entity;
	}

	public RendererLivingEntity getRenderer() {
		return renderer;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
