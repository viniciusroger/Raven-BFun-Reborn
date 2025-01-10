package keystrokesmod.module.impl.player;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", Category.player);
    }

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent event) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();
            try {
                ReflectUtil.S08PacketPlayerPosLookYaw.set(packet, mc.thePlayer.rotationYaw);
                ReflectUtil.S08PacketPlayerPosLookPitch.set(packet, mc.thePlayer.rotationPitch);
            } catch (Exception e) {
                e.printStackTrace();
                GeneralUtils.sendModuleMessage(this, "&cFailed to modify S08PacketPlayerPosLookPitch. Relaunch your game.");
            }
        }
    }
}
