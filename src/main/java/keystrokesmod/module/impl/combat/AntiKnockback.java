package keystrokesmod.module.impl.combat;

import keystrokesmod.event.ReceivePacketEvent;
import keystrokesmod.module.Module;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.impl.movement.LongJump;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.setting.impl.SliderSetting;
import keystrokesmod.util.GeneralUtils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.List;

public class AntiKnockback extends Module {
    private SliderSetting horizontal;
    private SliderSetting vertical;
    private ButtonSetting cancelExplosion;
    private ButtonSetting damageBoost;
    private SliderSetting boostMultiplier;
    private ButtonSetting groundCheck;
    private ButtonSetting lobbyCheck;

    public AntiKnockback() {
        super("AntiKnockback", Category.combat);
        this.registerSetting(new DescriptionSetting("Overrides Velocity."));
        this.registerSetting(horizontal = new SliderSetting("Horizontal", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(vertical = new SliderSetting("Vertical", 0.0, 0.0, 100.0, 1.0));
        this.registerSetting(cancelExplosion = new ButtonSetting("Cancel explosion packet", true));
        this.registerSetting(damageBoost = new ButtonSetting("Damage boost", false));
        this.registerSetting(boostMultiplier = new SliderSetting("Boost multiplier", 2.0, 1.0, 8.0, 0.1));
        this.registerSetting(groundCheck = new ButtonSetting("Ground check", false));
        this.registerSetting(lobbyCheck = new ButtonSetting("Lobby check", false));
    }

    @EventTarget
    public void onReceivePacket(ReceivePacketEvent e) {
        if (!GeneralUtils.nullCheck() || LongJump.stopModules || e.isCancelled()) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
                if (lobbyCheck.isToggled() && isLobby()) {
                    return;
                }
                e.setCancelled(true);
                if (cancel()) {
                    return;
                }
                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity) e.getPacket();
                if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                    mc.thePlayer.motionY = ((double) s12PacketEntityVelocity.getMotionY() / 8000) * vertical.getInput()/100;
                }
                else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                    mc.thePlayer.motionX = ((double) s12PacketEntityVelocity.getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionZ = ((double) s12PacketEntityVelocity.getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                else {
                    mc.thePlayer.motionX = ((double) s12PacketEntityVelocity.getMotionX() / 8000) * horizontal.getInput()/100;
                    mc.thePlayer.motionY = ((double) s12PacketEntityVelocity.getMotionY() / 8000) * vertical.getInput()/100;
                    mc.thePlayer.motionZ = ((double) s12PacketEntityVelocity.getMotionZ() / 8000) * horizontal.getInput()/100;
                }
                e.setCancelled(true);

                if (damageBoost.isToggled()) {
                    if (groundCheck.isToggled() && !mc.thePlayer.onGround) {
                        return;
                    }
                    GeneralUtils.setSpeed(GeneralUtils.getHorizontalSpeed() * boostMultiplier.getInput()); // from croat
                }
            }
        }
        else if (e.getPacket() instanceof S27PacketExplosion) {
            if (lobbyCheck.isToggled() && isLobby()) {
                return;
            }
            e.setCancelled(true);
            if (cancelExplosion.isToggled() || cancel()) {
                return;
            }
            S27PacketExplosion s27PacketExplosion = (S27PacketExplosion) e.getPacket();
            if (horizontal.getInput() == 0 && vertical.getInput() > 0) {
                mc.thePlayer.motionY += s27PacketExplosion.func_149144_d() * vertical.getInput()/100;
            }
            else if (horizontal.getInput() > 0 && vertical.getInput() == 0) {
                mc.thePlayer.motionX += s27PacketExplosion.func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionZ += s27PacketExplosion.func_149147_e() * horizontal.getInput()/100;
            }
            else {
                mc.thePlayer.motionX += s27PacketExplosion.func_149149_c() * horizontal.getInput()/100;
                mc.thePlayer.motionY += s27PacketExplosion.func_149144_d() * vertical.getInput()/100;
                mc.thePlayer.motionZ += s27PacketExplosion.func_149147_e() * horizontal.getInput()/100;
            }
            e.setCancelled(true);
        }
    }

    private boolean cancel() {
        return (vertical.getInput() == 0 && horizontal.getInput() == 0) || ModuleManager.bedAura.cancelKnockback();
    }

    @Override
    public String getInfo() {
        return (horizontal.getInput() == 100 ? "" : (int) horizontal.getInput() + "h") + (horizontal.getInput() != 100 && vertical.getInput() != 100 ? " " : "") + (vertical.getInput() == 100 ? "" : (int) vertical.getInput() + "v");
    }

    private boolean isLobby() {
        if (GeneralUtils.isHypixel()) {
            List<String> sidebarLines = GeneralUtils.getSidebarLines();
            if (!sidebarLines.isEmpty()) {
                String[] parts = GeneralUtils.stripColor(sidebarLines.get(1)).split("  ");
                if (parts.length > 1 && parts[1].charAt(0) == 'L') {
                    return true;
                }
            }
        }
        return false;
    }
}
