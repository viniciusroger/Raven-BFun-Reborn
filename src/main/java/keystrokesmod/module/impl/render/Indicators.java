package keystrokesmod.module.impl.render;

import keystrokesmod.event.JoinWorldEvent;
import keystrokesmod.event.Render2DEvent;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.ReflectUtil;
import keystrokesmod.util.RenderUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Indicators extends Module {
    private ButtonSetting renderArrows;
    private ButtonSetting renderPearls;
    private ButtonSetting renderFireballs;
    private ButtonSetting renderPlayers;
    private SliderSetting radius;
    private ButtonSetting itemColors;
    private ButtonSetting renderItem;
    private ButtonSetting threatsOnly;
    private HashSet<Entity> threats = new HashSet<>();
    private Map<String, String> lastHeldItems = new ConcurrentHashMap<>();
    private int pearlColor = new Color(173, 12, 255).getRGB();
    private int fireBallColor = new Color(255, 109, 0).getRGB();

    public Indicators() {
        super("Indicators", Category.render);
        this.registerSetting(renderArrows = new ButtonSetting("Render arrows", true));
        this.registerSetting(renderPearls = new ButtonSetting("Render ender pearls", true));
        this.registerSetting(renderFireballs = new ButtonSetting("Render fireballs", true));
        this.registerSetting(renderPlayers = new ButtonSetting("Render players", true));
        this.registerSetting(radius = new SliderSetting("Circle radius", 50, 5, 250, 2));
        this.registerSetting(itemColors = new ButtonSetting("Item colors", true));
        this.registerSetting(renderItem = new ButtonSetting("Render item", true));
        this.registerSetting(threatsOnly = new ButtonSetting("Render only threats", true));
    }

    public void onDisable() {
        this.threats.clear();
        this.lastHeldItems.clear();
    }

    @EventTarget
    public void onRenderTick(Render2DEvent event) {
        if (mc.currentScreen != null || !GeneralUtils.nullCheck()) {
            return;
        }
        if (threats.isEmpty()) {
            return;
        }
        try {
            Iterator<Entity> iterator = threats.iterator();
            while (iterator.hasNext()) {
                Entity e = iterator.next();
                if (e == null || !mc.theWorld.loadedEntityList.contains(e) || !canRender(e) || (e instanceof EntityArrow && ReflectUtil.inGround.getBoolean(e))) {
                    iterator.remove();
                    continue;
                }
                float yaw = GeneralUtils.getYaw(e) - mc.thePlayer.rotationYaw;
                ScaledResolution sr = new ScaledResolution(mc);
                float x = (float) sr.getScaledWidth() / 2;
                float y = (float) sr.getScaledHeight() / 2;
                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0.0);
                GL11.glPopMatrix();
                int color = -1;
                if (renderItem.isToggled()) {
                    ItemStack entityItem = null;
                    if (e instanceof EntityEnderPearl) {
                        color = pearlColor;
                        entityItem = new ItemStack(Items.ender_pearl);
                    } else if (e instanceof EntityArrow) {
                        entityItem = new ItemStack(Items.arrow);
                    } else if (e instanceof EntityFireball) {
                        color = fireBallColor;
                        entityItem = new ItemStack(Items.fire_charge);
                    }
                    if (entityItem != null) {
                        GL11.glPushMatrix();
                        float[] position = getPositionForCircle(yaw, radius.getInput());
                        GL11.glTranslated(x + position[0], y + position[1], 0.0);
                        if (e instanceof EntityArrow) {
                            GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                            GL11.glTranslatef(-7, 0, 0);
                            GL11.glRotatef(-45.0f, 0.0f, 0.0f, 1.0f);
                        }
                        mc.getRenderItem().renderItemAndEffectIntoGUI(entityItem, -8, 0);
                        GL11.glPopMatrix();
                    }
                }

                GL11.glPushMatrix();
                float[] position = getPositionForCircle(yaw, radius.getInput() + 21);
                GL11.glTranslated(x + position[0], y + position[1], 0.0);
                String distanceStr = (int) mc.thePlayer.getDistanceToEntity(e) + "m";
                float textWidth = mc.fontRendererObj.getStringWidth(distanceStr);
                mc.fontRendererObj.drawStringWithShadow(distanceStr, -textWidth / 2, (float) -mc.fontRendererObj.FONT_HEIGHT /2, -1);
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslated(x, y, 0.0);
                GL11.glRotatef(yaw, 0.0f, 0.0f, 1.0f);
                RenderUtils.drawArrow(-5f, (float) -radius.getInput() - 38, itemColors.isToggled() ? color : -1, 3, 5);
                GL11.glPopMatrix();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @EventTarget
    public void onEntityJoin(JoinWorldEvent e) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }
        if (e.getEntity() == mc.thePlayer) {
            this.threats.clear();
        } else if (canRender(e.getEntity()) && (mc.thePlayer.getDistanceSqToEntity(e.getEntity()) > 16.0 || !threatsOnly.isToggled())) {
            this.threats.add(e.getEntity());
        }
    }

    private boolean canRender(Entity entity) {
        try {
            if (entity instanceof EntityArrow && !ReflectUtil.inGround.getBoolean(entity) && renderArrows.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityFireball && renderFireballs.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityEnderPearl && renderPearls.isToggled()) {
                return true;
            }
            else if (entity instanceof EntityPlayer && renderPlayers.isToggled()) {
                return true;
            }
        }
        catch (IllegalAccessException e) {
            GeneralUtils.sendMessage("&cIssue checking entity.");
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public float[] getPositionForCircle(float angle, double radius) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        float wrappedAngle = MathHelper.wrapAngleTo180_float(angle);
        float absAngle = Math.abs(wrappedAngle);
        float[] position = null;
        if (absAngle >= 0 && absAngle <= 90) {
            position = new float[]{(float) (Math.cos(Math.toRadians(90 - absAngle)) * radius), (float) (Math.sin(Math.toRadians(90 - absAngle)) * radius)};
        } else if (absAngle > 90 && absAngle <= 180) {
            position = new float[]{(float) (Math.cos(Math.toRadians(absAngle - 90)) * radius), (float) -(Math.sin(Math.toRadians(absAngle - 90)) * radius)};
        }
        if (position != null) {
            if (wrappedAngle <= 0) {
                position[0] = -position[0];
            }
            position[1] = -position[1];
        }
        return position;
    }
}