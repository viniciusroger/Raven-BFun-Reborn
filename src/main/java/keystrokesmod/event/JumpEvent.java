package keystrokesmod.event;

import net.lenni0451.asmevents.event.wrapper.CancellableEvent;

public class JumpEvent extends CancellableEvent {
    private float motionY, yaw;

    public JumpEvent(float motionY, float yaw) {
        this.motionY = motionY;
        this.yaw = yaw;
    }

    public float getMotionY() {
        return motionY;
    }

    public void setMotionY(float motionY) {
        this.motionY = motionY;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
