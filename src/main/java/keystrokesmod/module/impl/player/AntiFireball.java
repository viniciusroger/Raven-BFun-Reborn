package keystrokesmod.module.impl.player;

import keystrokesmod.event.JoinWorldEvent;
import keystrokesmod.event.PreMotionEvent;
import keystrokesmod.event.PreUpdateEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.RotationUtils;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.item.*;
import org.lwjgl.input.Mouse;

import java.util.HashSet;

public class AntiFireball extends Module {
    private SliderSetting fov;
    private SliderSetting range;
    private ButtonSetting disableWhileFlying;
    private ButtonSetting disableWhileScaffold;
    private ButtonSetting blocksRotate;
    private ButtonSetting projectileRotate;
    public ButtonSetting silentSwing;
    public EntityFireball fireball;
    private HashSet<Entity> fireballs = new HashSet<>();
    public boolean attack;

    public AntiFireball() {
        super("AntiFireball", Category.player);
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(range = new SliderSetting("Range", 8.0, 3.0, 15.0, 0.5));
        this.registerSetting(disableWhileFlying = new ButtonSetting("Disable while flying", false));
        this.registerSetting(disableWhileScaffold = new ButtonSetting("Disable while scaffold", false));
        this.registerSetting(blocksRotate = new ButtonSetting("Rotate with blocks", false));
        this.registerSetting(projectileRotate = new ButtonSetting("Rotate with projectiles", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing", false));
    }

    @EventTarget(priority = EnumEventPriority.LOW)
    public void onPreMotion(PreMotionEvent e) {
        if (!condition() || stopAttack()) {
            return;
        }
        if (fireball != null) {
            final ItemStack getHeldItem = mc.thePlayer.getHeldItem();
            if (getHeldItem != null && getHeldItem.getItem() instanceof ItemBlock && !blocksRotate.isToggled() && Mouse.isButtonDown(1)) {
                return;
            }
            if (getHeldItem != null && (getHeldItem.getItem() instanceof ItemBow || getHeldItem.getItem() instanceof ItemSnowball || getHeldItem.getItem() instanceof ItemEgg || getHeldItem.getItem() instanceof ItemFishingRod) && !projectileRotate.isToggled()) {
                return;
            }
            if (ModuleManager.scaffold != null && ModuleManager.scaffold.stopRotation()) {
                return;
            }
            float[] rotations = RotationUtils.getRotations(fireball, e.getYaw(), e.getPitch());
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }
    }

    @EventTarget
    public void onPreUpdate(PreUpdateEvent e) {
        if (!condition() || stopAttack()) {
            return;
        }
        if (fireball != null) {
            if (ModuleManager.killAura != null && ModuleManager.killAura.isEnabled() && ModuleManager.killAura.block.get() && (ModuleManager.killAura.autoBlockMode.getInput() == 3 || ModuleManager.killAura.autoBlockMode.getInput() == 4)) {
                if (KillAura.target != null) {
                    attack = false;
                    return;
                }
                attack = true;
            } else {
                GeneralUtils.attackEntity(fireball, !silentSwing.isToggled(), silentSwing.isToggled());
            }
        }
    }

    private EntityFireball getFireball() {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFireball)) {
                continue;
            }
            if (!this.fireballs.contains(entity)) {
                continue;
            }
            if (mc.thePlayer.getDistanceSqToEntity(entity) > range.getInput() * range.getInput()) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !GeneralUtils.inFov(n, entity)) {
                continue;
            }
            return (EntityFireball) entity;
        }
        return null;
    }

    @EventTarget
    public void onEntityJoin(JoinWorldEvent e) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }
        if (e.getEntity() == mc.thePlayer) {
            this.fireballs.clear();
        } else if (e.getEntity() instanceof EntityFireball && mc.thePlayer.getDistanceSqToEntity(e.getEntity()) > 16.0) {
            this.fireballs.add(e.getEntity());
        }
    }

    public void onDisable() {
        this.fireballs.clear();
        this.fireball = null;
        this.attack = false;
    }

    public void onUpdate() {
        if (!condition()) {
            return;
        }
        if (mc.currentScreen != null) {
            attack = false;
            fireball = null;
            return;
        }
        fireball = this.getFireball();
    }

    private boolean stopAttack() {
        return (ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled() && ModuleManager.bedAura.currentBlock != null) || (ModuleManager.killAura != null && ModuleManager.killAura.isEnabled() && KillAura.target != null);
    }

    private boolean condition() {
        if (!GeneralUtils.nullCheck()) {
            return false;
        }
        if (mc.thePlayer.capabilities.isFlying && disableWhileFlying.isToggled()) {
            return false;
        }

        return ModuleManager.scaffold == null || !ModuleManager.scaffold.isEnabled() || !disableWhileScaffold.isToggled();
	}
}
