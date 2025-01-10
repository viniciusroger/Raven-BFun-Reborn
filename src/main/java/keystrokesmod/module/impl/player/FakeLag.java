package keystrokesmod.module.impl.player;

import keystrokesmod.event.Render2DEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.PacketUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.status.client.C00PacketServerQuery;

import java.util.concurrent.ConcurrentHashMap;

public class FakeLag extends Module {
    private SliderSetting packetDelay;
    private ConcurrentHashMap<Packet, Long> delayedPackets = new ConcurrentHashMap<>();

    public FakeLag() {
        super("Fake Lag", Category.player);
        this.registerSetting(packetDelay = new SliderSetting("Packet delay", 200, 25, 1000, 5, "ms"));
    }

    public String getInfo() {
        return (int) packetDelay.getInput() + "ms";
    }

    public void onEnable() {
        delayedPackets.clear();
    }

    public void onDisable() {
        sendPacket(true);
    }

    @EventTarget(priority = EnumEventPriority.HIGHEST)
    public void onRenderTick(Render2DEvent ev) {
        if (!GeneralUtils.nullCheck()) {
            sendPacket(false);
            return;
        }
        sendPacket(true);
    }

    @EventTarget(priority = EnumEventPriority.HIGHEST)
    public void onSendPacket(SendPacketEvent e) {
        long receiveTime = System.currentTimeMillis();
        if (!GeneralUtils.nullCheck()) {
            sendPacket(false);
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        Packet packet = e.getPacket();
        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart || packet instanceof C00PacketServerQuery || packet instanceof C01PacketEncryptionResponse) {
            return;
        }
        delayedPackets.put(e.getPacket(), receiveTime);
        e.setCancelled(true);
    }

    private void sendPacket(boolean delay) {
        try {
            delayedPackets.forEach((packet, receivedMs) -> {
                if (packet == null) {
                    return;
                }

                long receiveTime = receivedMs;
                long ms = System.currentTimeMillis();
                if (GeneralUtils.getDifference(ms, receiveTime) > packetDelay.getInput() || !delay) {
                    PacketUtils.sendPacketNoEvent(packet);
                    delayedPackets.remove(packet);
                }
            });
        }
        catch (Exception ignored) {
        }
    }
}
