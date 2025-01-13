package keystrokesmod.module.impl.combat;

import keystrokesmod.event.LivingUpdateEvent;
import keystrokesmod.event.SendPacketEvent;
import keystrokesmod.event.TickEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import keystrokesmod.util.PacketUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class MoreKnockback extends Module {
	private SliderSetting mode, activateMode, chance, hits, hurtTime, packets, noStopTicks;
	private ButtonSetting onlyHurtTime;
	private boolean attacked;
	public boolean resetSprint;
	private int ticks = 0;
	private int attackHits = 0;

	public MoreKnockback() {
		super("MoreKnockback", Category.combat, 0);
		this.registerSetting(mode = new SliderSetting("Mode", new String[]{"Legit", "STap", "LegitFast", "Packet", "DoublePacket", "ShiftPacket", "NoStop", "SprintReset", "LessPacket"}, 0));
		this.registerSetting(activateMode = new SliderSetting("Activate Mode", new String[]{"Chance", "Hit"}, 0));
		this.registerSetting(chance = new SliderSetting("Chance", 70, 0, 100, 1));
		this.registerSetting(hits = new SliderSetting("Hits", 3, 1, 10, 1));
		this.registerSetting(hurtTime = new SliderSetting("HurtTime", 5, 1, 10, 1));
		this.registerSetting(packets = new SliderSetting("Packets", 1, 1, 20, 1));
		this.registerSetting(noStopTicks = new SliderSetting("NoStop Ticks", 3, 2, 8, 1));
		this.registerSetting(onlyHurtTime = new ButtonSetting("Only HurtTime", false));
	}

	@Override
	public void onDisable() {
		attacked = false;
		resetSprint = false;
		ticks = 0;
		attackHits = 0;
	}

	@Override
	public void onUpdate() {
		if (activateMode.getInput() != 1)
			attackHits = 0;
	}

	@EventTarget
	public void onSendPacket(SendPacketEvent event) {
		if (event.getPacket() instanceof C02PacketUseEntity) {
			Entity entity = ((C02PacketUseEntity) event.getPacket()).getEntityFromWorld(mc.theWorld);

			if (!(entity instanceof EntityLivingBase))
				return;

			if (onlyHurtTime.isToggled() && ((EntityLivingBase) entity).hurtTime < (int) hurtTime.getInput())
				return;

			attackHits++;

			switch ((int) activateMode.getInput()) {
				case 0:
					if (Math.random() * 100 > chance.getInput())
						return;
				case 1:
					if (attackHits < (int) hits.getInput())
						return;
			}

			switch ((int) mode.getInput()) {
				case 3:
					for (int i = 0; i < (int) packets.getInput(); i++) {
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					}
					break;
				case 4:
					for (int i = 0; i < (int) packets.getInput(); i++) {
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					}
					break;
				case 5:
					for (int i = 0; i < (int) packets.getInput(); i++) {
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
						PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
					}
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
				case 8:
					if (mc.thePlayer.isSprinting())
						mc.thePlayer.setSprinting(false);

					PacketUtils.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
					break;
			}

			System.out.println("penis");

			attackHits = 0;
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

			attacked = false;
		}
	}

	@EventTarget
	public void onTick(TickEvent event) {
		if (GeneralUtils.nullCheck()) {
			if (mode.getInput() == 6 && attacked) {
				ticks++;

				if (ticks >= 1 && ticks < (int) noStopTicks.getInput()) {
					mc.thePlayer.setSprinting(false);
					return;
				} else if (ticks == (int) noStopTicks.getInput()) {
					mc.thePlayer.setSprinting(true);
				}

				ticks = 0;
				attacked = false;
			}
		}
	}
}
