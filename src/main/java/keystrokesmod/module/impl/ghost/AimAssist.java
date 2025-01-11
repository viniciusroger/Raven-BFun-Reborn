package keystrokesmod.module.impl.ghost;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;

public class AimAssist extends Module {
    private SliderSetting speed;
    private SliderSetting fov;
    private SliderSetting distance;
    private ButtonSetting clickAim;
    private ButtonSetting weaponOnly, breakBlocks;
    private ButtonSetting aimInvis, throughBlocks;
    private ButtonSetting blatantMode;
    private ButtonSetting ignoreTeammates;

    public AimAssist() {
        super("AimAssist", Category.ghost, 0);
        this.registerSetting(speed = new SliderSetting("Speed", 45.0D, 1.0D, 100.0D, 1.0D));
        this.registerSetting(fov = new SliderSetting("FOV", 90.0D, 15.0D, 180.0D, 1.0D));
        this.registerSetting(distance = new SliderSetting("Distance", 4.5D, 1.0D, 10.0D, 0.5D));
        this.registerSetting(clickAim = new ButtonSetting("Click aim", true));
        this.registerSetting(weaponOnly = new ButtonSetting("Weapon only", false));
        this.registerSetting(breakBlocks = new ButtonSetting("Break Blocks", false));
        this.registerSetting(aimInvis = new ButtonSetting("Aim invis", false));
        this.registerSetting(throughBlocks = new ButtonSetting("Through Blocks", false));
        this.registerSetting(blatantMode = new ButtonSetting("Blatant mode", false));
        this.registerSetting(ignoreTeammates = new ButtonSetting("Ignore teammates", false));
    }

    public void onUpdate() {
        if (mc.currentScreen == null && mc.inGameHasFocus) {
            if (breakBlocks.isToggled() && mc.objectMouseOver != null && mc.objectMouseOver.getBlockPos() != null) {
                Block broco = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

                if (broco != Blocks.air && !(broco instanceof BlockLiquid))
                    return;
            }

            if (!throughBlocks.isToggled() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                Block broco = mc.theWorld.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

                if (broco != Blocks.air && !(broco instanceof BlockLiquid))
                    return;
            }

            if (!weaponOnly.isToggled() || GeneralUtils.holdingWeapon()) {
                if (!clickAim.isToggled() || GeneralUtils.isClicking()) {
                    Entity en = this.getEnemy();
                    if (en != null) {
                        if (Raven.debugger) {
                            GeneralUtils.sendMessage(this.getName() + " &e" + en.getName());
                        }
                        if (blatantMode.isToggled()) {
                            GeneralUtils.aim(en, 0.0F, false);
                        } else {
                            double n = GeneralUtils.n(en);
                            if (n > 1.0D || n < -1.0D) {
                                float val = (float) (-(n / (101 - (speed.getInput()))));
                                mc.thePlayer.rotationYaw += val;
                            }
                        }
                    }

                }
            }
        }
    }

    private Entity getEnemy() {
        final int n = (int)fov.getInput();
        for (final EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer != mc.thePlayer && entityPlayer.deathTime == 0) {
                if (GeneralUtils.isFriended(entityPlayer)) {
                    continue;
                }
                if (ignoreTeammates.isToggled() && GeneralUtils.isTeamMate(entityPlayer)) {
                    continue;
                }
                if (!aimInvis.isToggled() && entityPlayer.isInvisible()) {
                    continue;
                }
                if (mc.thePlayer.getDistanceToEntity(entityPlayer) > distance.getInput()) {
                    continue;
                }
                if (AntiBot.isBot(entityPlayer)) {
                    continue;
                }
                if (!blatantMode.isToggled() && n != 360 && !GeneralUtils.inFov((float)n, entityPlayer)) {
                    continue;
                }
                return entityPlayer;
            }
        }
        return null;
    }
}
