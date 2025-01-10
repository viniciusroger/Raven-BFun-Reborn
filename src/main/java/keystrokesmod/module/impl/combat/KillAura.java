package keystrokesmod.module.impl.combat;

import keystrokesmod.event.*;
import keystrokesmod.util.PacketUtils;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.*;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventPriority;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import org.lwjgl.input.Mouse;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.util.EnumFacing.DOWN;

public class KillAura extends Module {
    public static EntityLivingBase target;
    private SliderSetting aps;
    public SliderSetting autoBlockMode;
    private SliderSetting fov;
    private SliderSetting attackRange;
    private SliderSetting swingRange;
    private SliderSetting blockRange;
    private SliderSetting rotationMode;
    private SliderSetting rotationSmoothing;
    private SliderSetting sortMode;
    private SliderSetting switchDelay;
    private SliderSetting targets;
    private ButtonSetting targetInvis;
    private ButtonSetting disableInInventory;
    private ButtonSetting disableWhileBlocking;
    private ButtonSetting disableWhileMining;
    private ButtonSetting hitThroughBlocks;
    private ButtonSetting ignoreTeammates;
    public ButtonSetting manualBlock;
    private ButtonSetting prioritizeEnemies;
    private ButtonSetting requireMouseDown;
    private ButtonSetting silentSwing;
    private ButtonSetting weaponOnly;
    private String[] autoBlockModes = new String[]{"Manual", "Vanilla", "Post", "Swap", "Interact A", "Interact B", "Fake", "Partial"};
    private String[] rotationModes = new String[]{"None", "Silent", "Lock view"};
    private String[] sortModes = new String[]{"Health", "Hurttime", "Distance", "Yaw"};
    private List<EntityLivingBase> availableTargets = new ArrayList<>();
    public AtomicBoolean block = new AtomicBoolean();
    private long lastSwitched = System.currentTimeMillis();
    private boolean switchTargets;
    private byte entityIndex;
    public boolean swing;
    // autoclicker vars
    private long i;
    private long j;
    private long k;
    private long l;
    private double m;
    private boolean n;
    private Random rand;
    // autoclicker vars end
    private boolean attack;
    private boolean blocking;
    public boolean blinking;
    public boolean lag;
    private boolean swapped;
    public boolean rmbDown;
    private float[] prevRotations;
    private boolean startSmoothing;
    private ConcurrentLinkedQueue<Packet> blinkedPackets = new ConcurrentLinkedQueue<>();


    public KillAura() {
        super("KillAura", Category.combat);
        this.registerSetting(aps = new SliderSetting("APS", 16.0, 1.0, 20.0, 0.5));
        this.registerSetting(autoBlockMode = new SliderSetting("Autoblock", autoBlockModes, 0));
        this.registerSetting(fov = new SliderSetting("FOV", 360.0, 30.0, 360.0, 4.0));
        this.registerSetting(attackRange = new SliderSetting("Range (attack)", 3.0, 3.0, 6.0, 0.05));
        this.registerSetting(swingRange = new SliderSetting("Range (swing)", 3.3, 3.0, 8.0, 0.05));
        this.registerSetting(blockRange = new SliderSetting("Range (block)", 6.0, 3.0, 12.0, 0.05));
        this.registerSetting(rotationMode = new SliderSetting("Rotation mode", rotationModes, 0));
        this.registerSetting(rotationSmoothing = new SliderSetting("Rotation smoothing", 0, 0, 15, 1));
        this.registerSetting(sortMode = new SliderSetting("Sort mode", sortModes, 0.0));
        this.registerSetting(switchDelay = new SliderSetting("Switch delay", 200.0, 50.0, 1000.0, 25.0, "ms"));
        this.registerSetting(targets = new SliderSetting("Targets", 3.0, 1.0, 10.0, 1.0));
        this.registerSetting(targetInvis = new ButtonSetting("Target invis", true));
        this.registerSetting(disableInInventory = new ButtonSetting("Disable in inventory", true));
        this.registerSetting(disableWhileBlocking = new ButtonSetting("Disable while blocking", false));
        this.registerSetting(disableWhileMining = new ButtonSetting("Disable while mining", false));
        this.registerSetting(hitThroughBlocks = new ButtonSetting("Hit through blocks", true));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", true));
        this.registerSetting(manualBlock = new ButtonSetting("Manual block", false));
        this.registerSetting(prioritizeEnemies = new ButtonSetting("Prioritize enemies", false));
        this.registerSetting(requireMouseDown = new ButtonSetting("Require mouse down", false));
        this.registerSetting(silentSwing = new ButtonSetting("Silent swing while blocking", false));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
    }

    public void onEnable() {
        this.rand = new Random();
    }

    public void onDisable() {
        reset();

    }

    @EventTarget
    public void onRenderTick(Render2DEvent ev) {
        if (!GeneralUtils.nullCheck()) {
            return;
        }

        if (canAttack()) {
            attack = true;
        }
        if (target != null && rotationMode.getInput() == 2) {
            float[] rotations = RotationUtils.getRotations(target, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            if (rotationSmoothing.getInput() > 0) {
                float[] speed = new float[]{(float) ((rotations[0] - mc.thePlayer.rotationYaw) / ((101 - rotationSmoothing.getInput()) * 3.634542)), (float) ((rotations[1] - mc.thePlayer.rotationPitch) / ((101 - rotationSmoothing.getInput()) * 5.1853))};
                mc.thePlayer.rotationYaw += speed[0];
                mc.thePlayer.rotationPitch += speed[1];
            }
            else {
                mc.thePlayer.rotationYaw = rotations[0];
                mc.thePlayer.rotationPitch = rotations[1];
            }
        }
    }

    @EventTarget
    public void onPreUpdate(PreUpdateEvent e) {
        if (!basicCondition() || !settingCondition()) {
            reset();
            return;
        }

        block();

        if (ModuleManager.bedAura != null && ModuleManager.bedAura.isEnabled() && !ModuleManager.bedAura.allowAura.isToggled() && ModuleManager.bedAura.currentBlock != null) {
            resetBlinkState(true);
            return;
        }
        if ((mc.thePlayer.isBlocking() || block.get()) && disableWhileBlocking.isToggled()) {
            resetBlinkState(true);
            return;
        }
        boolean swingWhileBlocking = !silentSwing.isToggled() || !block.get();
        if (swing && attack) {
            if (swingWhileBlocking) {
                mc.thePlayer.swingItem();
            }
            else {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            }
        }
        if (block.get() && (autoBlockMode.getInput() == 3 || autoBlockMode.getInput() == 4 || autoBlockMode.getInput() == 5) && GeneralUtils.holdingSword()) {
            setBlockState(block.get(), false, false);
            if (ModuleManager.bedAura.stopAutoblock) {
                resetBlinkState(false);
                ModuleManager.bedAura.stopAutoblock = false;
                return;
            }
            switch ((int) autoBlockMode.getInput()) {
                case 3:
                    if (lag) {
                        blinking = true;
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
                        swapped = true;
                        lag = false;
                    }
                    else {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        swapped = false;
                        attackAndInteract(target, swingWhileBlocking, false);
                        sendBlock();
                        releasePackets();
                        lag = true;
                    }
                    break;
                case 4:
                case 5:
                    if (lag) {
                        blinking = true;
                        unBlock();
                        lag = false;
                    }
                    else {
                        attackAndInteract(target, swingWhileBlocking, autoBlockMode.getInput() == 5); // attack while blinked
                        releasePackets(); // release
                        sendBlock(); // block after releasing unblock
                        lag = true;
                    }
                    break;
            }
            return;
        }
        else if (blinking || lag) {
            resetBlinkState(true);
        }
        if (target == null) {
            return;
        }
        if (attack) {
            resetBlinkState(true);
            attack = false;
            if (!aimingEntity()) {
                return;
            }
            switchTargets = true;
            GeneralUtils.attackEntity(target, swingWhileBlocking, !swingWhileBlocking);
        }
    }

    @EventTarget(priority = EnumEventPriority.LOW)
    public void onPreMotion(PreMotionEvent e) {
        if (!basicCondition() || !settingCondition()) {
            reset();
            return;
        }
        setTarget(new float[]{e.getYaw(), e.getPitch()});
        if (target != null && rotationMode.getInput() == 1) {
            float[] rotations = RotationUtils.getRotations(target, e.getYaw(), e.getPitch());
            if (rotationSmoothing.getInput() > 0) {
                if (!startSmoothing) {
                    prevRotations = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
                    startSmoothing = true;
                }
                float[] speed = new float[]{(float) ((rotations[0] - prevRotations[0]) / Math.max(((rotationSmoothing.getInput()) * 0.262843), 1.5)), (float) ((rotations[1] - prevRotations[1]) / Math.max(((rotationSmoothing.getInput()) * 0.1637), 1.5))};
                prevRotations[0] += speed[0];
                prevRotations[1] += speed[1];
                if (prevRotations[1] > 90) {
                    prevRotations[1] = 90;
                }
                else if (prevRotations[1] < -90) {
                    prevRotations[1] = -90;
                }
                e.setYaw(prevRotations[0]);
                e.setPitch(prevRotations[1]);
            }
            else {
                e.setYaw(rotations[0]);
                e.setPitch(rotations[1]);
            }
        }
        else {
            startSmoothing = false;
        }
        if (autoBlockMode.getInput() == 2 && block.get() && GeneralUtils.holdingSword()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem % 8 + 1));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
    }

    @EventTarget
    public void onPostMotion(PostMotionEvent e) {
        if (autoBlockMode.getInput() == 2 && block.get() && GeneralUtils.holdingSword()) {
            sendBlock();
        }
    }

    @EventTarget(priority = EnumEventPriority.HIGH)
    public void onSendPacket(SendPacketEvent e) {
        if (!GeneralUtils.nullCheck() || !blinking) {
            return;
        }
        Packet packet = e.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        if (packet instanceof C00PacketKeepAlive) {
            return;
        }
        blinkedPackets.add(e.getPacket());
        e.setCancelled(true);
    }

    @EventTarget
    public void onMouse(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 0 && mouseEvent.isButtonState()) {
            if (target != null || swing) {
                mouseEvent.setCancelled(true);
            }
        }
        else if (mouseEvent.getButton() == 1) {
            rmbDown = mouseEvent.isButtonState();
            if (autoBlockMode.getInput() >= 1 && GeneralUtils.holdingSword() && block.get()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                if (target == null && mc.objectMouseOver != null) {
                    if (mc.objectMouseOver.entityHit != null && AntiBot.isBot(mc.objectMouseOver.entityHit)) {
                        return;
                    }
                    final BlockPos getBlockPos = mc.objectMouseOver.getBlockPos();
                    if (getBlockPos != null && (BlockUtils.check(getBlockPos, Blocks.chest) || BlockUtils.check(getBlockPos, Blocks.ender_chest))) {
                        return;
                    }
                }
                mouseEvent.setCancelled(true);
            }
        }
    }

    @Override
    public String getInfo() {
        return rotationModes[(int) rotationMode.getInput()];
    }

    private boolean aimingEntity() {
        if (rotationMode.getInput() > 0 && rotationSmoothing.getInput() > 0) {
            Object[] raycast = Reach.getEntity(attackRange.getInput(), 0, rotationMode.getInput() == 1 ? prevRotations : null);
            if (raycast == null || raycast[0] != target) {
                return false;
            }
        }
        return true;
    }

    private void reset() {
        target = null;
        availableTargets.clear();
        block.set(false);
        ModuleManager.targetHUD.renderEntity = null;
        startSmoothing = false;
        swing = false;
        rmbDown = false;
        attack = false;
        this.i = 0L;
        this.j = 0L;
        block();
        resetBlinkState(true);
        swapped = false;
    }

    private void block() {
        if (!block.get() && !blocking) {
            return;
        }
        if (manualBlock.isToggled() && !rmbDown) {
            block.set(false);
        }
        if (!GeneralUtils.holdingSword()) {
            block.set(false);
        }
        switch ((int) autoBlockMode.getInput()) {
            case 0:
                setBlockState(false, false, true);
                break;
            case 1: // vanilla
                setBlockState(block.get(), true, true);
                break;
            case 2: // post
                setBlockState(block.get(), false, true);
                break;
            case 3: // interact
            case 4:
            case 5:
                setBlockState(block.get(), false, false);
                break;
            case 6: // fake
                setBlockState(block.get(), false, false);
                break;
            case 7: // partial
                boolean down = (target == null || target.hurtTime >= 5) && block.get();
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), down);
                ReflectUtil.setButton(1, down);
                blocking = down;
        }
    }

    private void setBlockState(boolean state, boolean sendBlock, boolean sendUnBlock) {
        if (GeneralUtils.holdingSword()) {
            if (sendBlock && !blocking && state && GeneralUtils.holdingSword()) {
                sendBlock();
            } else if (sendUnBlock && blocking && !state) {
                unBlock();
            }
        }
        blocking = ReflectUtil.setBlocking(state);
    }

    private void setTarget(float[] rotations) {
        availableTargets.clear();
        block.set(false);
        ModuleManager.targetHUD.renderEntity = null;
        swing = false;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (availableTargets.size() > targets.getInput()) {
                continue;
            }
            if (entity == null) {
                continue;
            }
            if (entity == mc.thePlayer) {
                continue;
            }
            if (!(entity instanceof EntityLivingBase)) {
                continue;
            }
            if (entity instanceof EntityPlayer) {
                if (GeneralUtils.isFriended((EntityPlayer) entity)) {
                    continue;
                }
                if (((EntityPlayer) entity).deathTime != 0) {
                    continue;
                }
                if (AntiBot.isBot(entity) || (GeneralUtils.isTeamMate(entity) && ignoreTeammates.isToggled())) {
                    continue;
                }
            }
            else {
                continue;
            }
            if (entity.isInvisible() && !targetInvis.isToggled()) {
                continue;
            }
            if (!hitThroughBlocks.isToggled() && behindBlocks(rotations)) {
                continue;
            }
            final float n = (float) fov.getInput();
            if (n != 360.0f && !GeneralUtils.inFov(n, entity)) {
                continue;
            }
            double distance = mc.thePlayer.getDistanceSqToEntity(entity); // need a more accurate distance check
            if (distance <= blockRange.getInput() * blockRange.getInput() && autoBlockMode.getInput() > 0 && GeneralUtils.holdingSword()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                block.set(true);
                ModuleManager.targetHUD.renderEntity = (EntityLivingBase) entity;
            }
            if (distance <= swingRange.getInput() * swingRange.getInput()) {
                swing = true;
                ModuleManager.targetHUD.renderEntity = (EntityLivingBase) entity;
            }
            if (distance > attackRange.getInput() * attackRange.getInput()) {
                continue;
            }
            availableTargets.add((EntityLivingBase) entity);
        }
        if (Math.abs(System.currentTimeMillis() - lastSwitched) > switchDelay.getInput() && switchTargets) {
            switchTargets = false;
            if (entityIndex < availableTargets.size() - 1) {
                entityIndex++;
            } else {
                entityIndex = 0;
            }
            lastSwitched = System.currentTimeMillis();
        }
        if (!availableTargets.isEmpty()) {
            List<EntityLivingBase> enemies = new ArrayList<>();
            if (prioritizeEnemies.isToggled()) {
                for (EntityLivingBase entity : availableTargets) {
                    if (GeneralUtils.isEnemy((EntityPlayer) entity)) {
                        enemies.add(entity);
                    }
                }
                if (!enemies.isEmpty()) {
                    availableTargets = enemies;
                }
            }
            Comparator<EntityLivingBase> comparator = null;
            switch ((int) sortMode.getInput()) {
                case 0:
                    comparator = Comparator.comparingDouble(entityPlayer -> (double)entityPlayer.getHealth());
                    break;
                case 1:
                    comparator = Comparator.comparingDouble(entityPlayer2 -> (double)entityPlayer2.hurtTime);
                    break;
                case 2:
                    comparator = Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceSqToEntity(entity));
                    break;
                case 3:
                    comparator = Comparator.comparingDouble(entity2 -> RotationUtils.distanceFromYaw(entity2, false));
                    break;
            }
            Collections.sort(availableTargets, comparator);
            if (entityIndex > availableTargets.size() - 1) {
                entityIndex = 0;
            }
            target = availableTargets.get(entityIndex);
        } else {
            target = null;
        }
    }

    private boolean basicCondition() {
        if (!GeneralUtils.nullCheck()) {
            return false;
        }
        if (mc.thePlayer.isDead) {
            return false;
        }
        return true;
    }

    private boolean settingCondition() {
        if (!Mouse.isButtonDown(0) && requireMouseDown.isToggled()) {
            return false;
        }
        else if (!GeneralUtils.holdingWeapon() && weaponOnly.isToggled()) {
            return false;
        }
        else if (isMining() && disableWhileMining.isToggled()) {
            return false;
        }
        else if (mc.currentScreen != null && disableInInventory.isToggled()) {
            return false;
        }
        return true;
    }

    private void attackAndInteract(EntityLivingBase target, boolean swingWhileBlocking, boolean predict) {
        if (target != null && attack) {
            attack = false;
            if (!aimingEntity()) {
                return;
            }
            if (predict && target.hurtResistantTime > 16) {
                return;
            }
            switchTargets = true;
            GeneralUtils.attackEntity(target, !swing && swingWhileBlocking, !swingWhileBlocking);
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
        }
        else if (ModuleManager.antiFireball != null && ModuleManager.antiFireball.isEnabled() && ModuleManager.antiFireball.fireball != null && ModuleManager.antiFireball.attack) {
            GeneralUtils.attackEntity(ModuleManager.antiFireball.fireball, !ModuleManager.antiFireball.silentSwing.isToggled(), ModuleManager.antiFireball.silentSwing.isToggled());
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(ModuleManager.antiFireball.fireball, C02PacketUseEntity.Action.INTERACT));
        }
    }

    private boolean isMining() {
        return Mouse.isButtonDown(0) && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK;
    }

    private boolean canAttack() {
        if (this.j > 0L && this.i > 0L) {
            if (System.currentTimeMillis() > this.j) {
                this.gd();
                return true;
            } else if (System.currentTimeMillis() > this.i) {
                return false;
            }
        } else {
            this.gd();
        }
        return false;
    }

    public void gd() {
        double c = aps.getInput() + 0.4D * this.rand.nextDouble();
        long d = (long) ((int) Math.round(1000.0D / c));
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1D + this.rand.nextDouble() * 0.15D;
            } else {
                this.n = false;
            }

            this.k = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        if (this.n) {
            d = (long) ((double) d * this.m);
        }

        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + (long) this.rand.nextInt(100);
            }

            this.l = System.currentTimeMillis() + 500L + (long) this.rand.nextInt(1500);
        }

        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - (long) this.rand.nextInt(10);
    }

    private void unBlock() {
        if (!GeneralUtils.holdingSword()) {
            return;
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, DOWN));
    }

    public void resetBlinkState(boolean unblock) {
        releasePackets();
        blocking = false;
        if (swapped) {
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            swapped = false;
        }
        if (lag && unblock) {
            unBlock();
        }
        lag = false;
    }

    private void releasePackets() {
        try {
            synchronized (blinkedPackets) {
                for (Packet packet : blinkedPackets) {
                    PacketUtils.sendPacketNoEvent(packet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            GeneralUtils.sendModuleMessage(this, "&cThere was an error releasing blinked packets");
        }
        blinkedPackets.clear();
        blinking = false;
    }

    private boolean behindBlocks(float[] rotations) {
        switch ((int) rotationMode.getInput()) {
            case 0:
            case 2:
                if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    BlockPos p = mc.objectMouseOver.getBlockPos();
                    if (p != null && mc.theWorld.getBlockState(p).getBlock() != Blocks.air) {
                        return true;
                    }
                }
                break;
            case 1:
                if (rotationSmoothing.getInput() > 0) {
                    return RotationUtils.rayCast(attackRange.getInput(), prevRotations != null ? prevRotations[0] : mc.thePlayer.rotationYaw, prevRotations != null ? prevRotations[1] : mc.thePlayer.rotationPitch) != null;
                }
                return RotationUtils.rayCast(attackRange.getInput(), rotations[0], rotations[1]) != null;
        }
        return false;
    }

    private void sendBlock() {
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }
}
