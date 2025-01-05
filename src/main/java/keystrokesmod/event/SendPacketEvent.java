package keystrokesmod.event;

import net.lenni0451.asmevents.event.wrapper.CancellableEvent;
import net.minecraft.network.Packet;

public class SendPacketEvent extends CancellableEvent {
    private Packet<?> packet;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}