package keystrokesmod.mixin.impl.network;

import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient {

	@Shadow private WorldClient clientWorldController;

	@Shadow private Minecraft gameController;

	/**
	 * @author a
	 * @reason a
	 */
	@Overwrite
	public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
		PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
		Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityId());
		int bruhX = packetIn.getX();
		int bruhZ = packetIn.getZ();

		if (ModuleManager.misplace.isEnabled() && !AntiBot.isBot(entity)) {
			double x = bruhX / 32.0;
			double z = bruhZ / 32.0;
			double minr = ModuleManager.misplace.minRange.getInput();
			double maxr = ModuleManager.misplace.maxRange.getInput();
			double f = RandomUtils.nextDouble(Math.min(minr, maxr), Math.max(maxr, minr)) - 3;

			if (f != 0.0) {
				double c = Math.hypot(Minecraft.getMinecraft().thePlayer.posX - x, Minecraft.getMinecraft().thePlayer.posZ - z);

				if (f > c) {
					f -= c;
				}

				float r = ModuleManager.misplace.real1(x, z);

				if (ModuleManager.misplace.getFov(Minecraft.getMinecraft().thePlayer.rotationYaw, r) <= ModuleManager.misplace.fov.getInput()) {
					double a = Math.cos(Math.toRadians(r + 90.0f));
					double b = Math.sin(Math.toRadians(r + 90.0f));

					x -= a * f;
					z -= b * f;

					bruhX = MathHelper.floor_double(x * 32.0);
					bruhZ = MathHelper.floor_double(z * 32.0);
				}
			}
		}

		if (entity != null)
		{
			entity.serverPosX = bruhX;
			entity.serverPosY = packetIn.getY();
			entity.serverPosZ = bruhZ;
			double d0 = (double)entity.serverPosX / 32.0D;
			double d1 = (double)entity.serverPosY / 32.0D;
			double d2 = (double)entity.serverPosZ / 32.0D;
			float f = (float)(packetIn.getYaw() * 360) / 256.0F;
			float f1 = (float)(packetIn.getPitch() * 360) / 256.0F;

			if (Math.abs(entity.posX - d0) < 0.03125D && Math.abs(entity.posY - d1) < 0.015625D && Math.abs(entity.posZ - d2) < 0.03125D)
			{
				entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, f, f1, 3, true);
			}
			else
			{
				entity.setPositionAndRotation2(d0, d1, d2, f, f1, 3, true);
			}

			entity.onGround = packetIn.getOnGround();
		}
	}
}
