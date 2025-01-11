package keystrokesmod.module.impl.combat;

import keystrokesmod.event.LivingUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import keystrokesmod.util.PacketUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class MoreKnockback extends Module {
	private SliderSetting mode;
	private boolean attacked;
	public boolean resetSprint;
	private int ticks = 0;

	public MoreKnockback() {
		super("MoreKnockback", Category.combat, 0);
		this.registerSetting(mode = new SliderSetting("Mode", new String[]{"WTap", "STap", "LegitFast", "Packet", "DoublePacket", "ShiftPacket", "NoStop", "SprintReset"}, 0));
	}

	@Override
	public void onDisable() {
		attacked = false;
		resetSprint = false;
		ticks = 0;
	}

	@EventTarget
	public void onSendPacket(SendPacketEvent event) {
		if (event.getPacket() instanceof C02PacketUseEntity) {
			switch ((int) mode.getInput()) {
				case 3:
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					break;
				case 4:
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					break;
				case 5:
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
					break;
				case 7:
					resetSprint = true;
					break;
				case 0:
				case 1:
				case 2:
				case 6:
					attacked = true;
					break;
			}
		}
	}

	@EventTarget
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (attacked) {
			switch ((int) mode.getInput()) {
				case 0:
					mc.thePlayer.movementInput.moveForward = 0;
					mc.thePlayer.setSprinting(false);
					mc.thePlayer.setSprinting(true);
					break;
				case 1:
					mc.thePlayer.movementInput.moveForward -= 10;
					break;
				case 2:
					mc.thePlayer.sprintingTicksLeft = 0;
					break;
			}

			attacked = true;
		}
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (GeneralUtils.nullCheck()) {
			if (mode.getInput() == 6 && attacked) {
				ticks++;

				if (ticks >= 1 && ticks < 6) {
					mc.thePlayer.setSprinting(false);
					return;
				} else if (ticks == 6) {
					mc.thePlayer.setSprinting(true);
				}

				ticks = 0;
			}
		}
	}
}
