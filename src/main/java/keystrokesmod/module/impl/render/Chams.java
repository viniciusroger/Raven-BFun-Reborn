package keystrokesmod.module.impl.render;

import keystrokesmod.event.RenderEntityEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.world.AntiBot;
import keystrokesmod.module.setting.impl.ButtonSetting;
import net.lenni0451.asmevents.event.EventTarget;
import net.lenni0451.asmevents.event.enums.EnumEventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;

public class Chams extends Module {
    private ButtonSetting onlyPlayers;
    private ButtonSetting showYourself;
    private ButtonSetting ignoreBots;
    private HashSet<Entity> bots = new HashSet<>();

    public Chams() {
        super("Chams", Module.category.render, 0);
        this.registerSetting(onlyPlayers = new ButtonSetting("Only Players", false));
        this.registerSetting(showYourself = new ButtonSetting("Show Yourself(what)", false));
        this.registerSetting(ignoreBots = new ButtonSetting("Ignore bots", false));
    }

    @EventTarget
    public void r1(RenderEntityEvent e) {
        if (!showYourself.isToggled() && e.getEntity() == mc.thePlayer) {
            return;
        }

        if (onlyPlayers.isToggled() && !(e.getEntity() instanceof EntityPlayer))
            return;

        if (e.getType() == EnumEventType.PRE) {
            if (ignoreBots.isToggled()) {
                if (AntiBot.isBot(e.getEntity())) {
                    return;
                }
                this.bots.add(e.getEntity());
            }
            GL11.glEnable(32823);
            GL11.glPolygonOffset(1.0f, -1000000.0f);
        }

        if (e.getType() == EnumEventType.POST) {
            if (ignoreBots.isToggled()) {
                if (!this.bots.contains(e.getEntity())) {
                    return;
                }
                this.bots.remove(e.getEntity());
            }
            GL11.glPolygonOffset(1.0f, 1000000.0f);
            GL11.glDisable(32823);
        }
    }

    public void onDisable() {
        this.bots.clear();
    }
}
