package keystrokesmod.module.impl.ghost;

import keystrokesmod.event.MouseEvent;
import keystrokesmod.event.Render3DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

public class HitBox extends Module {
	public static SliderSetting expand;
    public static SliderSetting multiplier;
	public static SliderSetting hitboxType;
    public ButtonSetting showHitbox;
    public ButtonSetting playersOnly;
    public ButtonSetting weaponOnly;
	public static ButtonSetting useExpand;
    private Entity pointedEntity;
    private MovingObjectPosition mv;

    public HitBox() {
        super("HitBox", Category.ghost, 0);
        this.registerSetting(expand = new SliderSetting("Expand", 0.04, 0, 1.0, 0.01));
		this.registerSetting(multiplier = new SliderSetting("Multiplier", 1.2, 1.0, 5.0, 0.01, "x"));
        this.registerSetting(hitboxType = new SliderSetting("Hitbox Type", new String[]{"Normal", "Raven"}, 1));
		this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
        this.registerSetting(showHitbox = new ButtonSetting("Show new hitbox", false));
        this.registerSetting(useExpand = new ButtonSetting("Use Expand", true));
		this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
	}

    @Override
    public String getInfo() {
        return ((int) multiplier.getInput() == multiplier.getInput() ? (int) multiplier.getInput() + "" : multiplier.getInput()) + multiplier.getInfo();
    }

    @EventTarget
    public void onMouse(MouseEvent e) {
        if (GeneralUtils.nullCheck()) {
			if (hitboxType.getInput() == 0)
				return;

            if (e.getButton() != 0 || !e.isButtonState() || multiplier.getInput() == 1 || mc.thePlayer.isBlocking() || mc.currentScreen != null) {
                return;
            }
            if (weaponOnly.isToggled() && !GeneralUtils.holdingWeapon()) {
                return;
            }

            EntityLivingBase c = getEntity(1.0F);
            if (c == null) {
                return;
            }
            if (c instanceof EntityPlayer) {
                if (playersOnly.isToggled()) {
                    return;
                }
            }
            mc.objectMouseOver = mv;
        }
    }

    @EventTarget
    public void onRenderWorld(Render3DEvent e) {
        if (showHitbox.isToggled() && GeneralUtils.nullCheck()) {
            for (Entity en : mc.theWorld.loadedEntityList) {
                if (en != mc.thePlayer && en instanceof EntityLivingBase && ((EntityLivingBase) en).deathTime == 0 && !(en instanceof EntityArmorStand) && !en.isInvisible()) {
                    this.rh(en, Color.WHITE);
                }
            }
        }
    }

    public static float getExpand(float s) {
        return (float) (useExpand.isToggled() ? s + expand.getInput() : s * multiplier.getInput());
    }

    public EntityLivingBase getEntity(float partialTicks) {
        if (mc.getRenderViewEntity() != null && mc.theWorld != null) {
            mc.pointedEntity = null;
            pointedEntity = null;
            double d0 = mc.playerController.extendedReach() ? 6.0 : (ModuleManager.reach.isEnabled() ? GeneralUtils.getRandomValue(Reach.min, Reach.max, GeneralUtils.getRandom()) : 3.0);
            mv = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
            double d2 = d0;
            Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);

            if (mv != null) {
                d2 = mv.hitVec.distanceTo(vec3);
            }

            Vec3 vec4 = mc.getRenderViewEntity().getLook(partialTicks);
            Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            Vec3 vec6 = null;
            float f1 = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand((double) f1, (double) f1, (double) f1));
            double d3 = d2;

            for (Entity entity : list) {
                if (entity.canBeCollidedWith()) {
                    float ex = getExpand(entity.getCollisionBorderSize());
                    AxisAlignedBB ax = entity.getEntityBoundingBox().expand(ex, ex, ex);
                    MovingObjectPosition mop = ax.calculateIntercept(vec3, vec5);
                    if (ax.isVecInside(vec3)) {
                        if (0.0D < d3 || d3 == 0.0D) {
                            pointedEntity = entity;
                            vec6 = mop == null ? vec3 : mop.hitVec;
                            d3 = 0.0D;
                        }
                    } else if (mop != null) {
                        double d4 = vec3.distanceTo(mop.hitVec);
                        if (d4 < d3 || d3 == 0.0D) {
                            if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                                if (d3 == 0.0D) {
                                    pointedEntity = entity;
                                    vec6 = mop.hitVec;
                                }
                            } else {
                                pointedEntity = entity;
                                vec6 = mop.hitVec;
                                d3 = d4;
                            }
                        }
                    }
                }
            }

            if (pointedEntity != null && (d3 < d2 || mv == null)) {
                mv = new MovingObjectPosition(pointedEntity, vec6);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    return (EntityLivingBase) pointedEntity;
                }
            }
        }
        return null;
    }

    private void rh(Entity e, Color c) {
        if (e instanceof EntityLivingBase) {
            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * (double) GeneralUtils.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * (double) GeneralUtils.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * (double) GeneralUtils.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;
            float ex = hitboxType.getInput() == 1 ? getExpand(e.getCollisionBorderSize()) : e.getCollisionBorderSize();
            AxisAlignedBB bbox = e.getEntityBoundingBox().expand(ex, ex, ex);
            AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            GL11.glColor3d((double) c.getRed(), (double) c.getGreen(), (double) c.getBlue());
            RenderGlobal.drawSelectionBoundingBox(axis);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }
}