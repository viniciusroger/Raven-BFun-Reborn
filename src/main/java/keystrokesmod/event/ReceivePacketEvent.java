package keystrokesmod.event;

import net.lenni0451.asmevents.event.wrapper.CancellableEvent;
import net.minecraft.network.Packet;

public class ReceivePacketEvent extends CancellableEvent {
    private Packet<?> packet;

    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
