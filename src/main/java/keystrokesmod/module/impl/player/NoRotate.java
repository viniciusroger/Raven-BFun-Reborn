package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.utility.ReflectHelper;
import keystrokesmod.utility.Utils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", category.player);
    }

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent event) {
        if (!Utils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            try {
                ReflectHelper.S08PacketPlayerPosLookYaw.set(packet, mc.thePlayer.rotationYaw);
                ReflectHelper.S08PacketPlayerPosLookPitch.set(packet, mc.thePlayer.rotationPitch);
            } catch (Exception e) {
                e.printStackTrace();
                Utils.sendModuleMessage(this, "&cFailed to modify S08PacketPlayerPosLookPitch. Relaunch your game.");
            }
        }
    }
}
