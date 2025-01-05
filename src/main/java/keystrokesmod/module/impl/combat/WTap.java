package keystrokesmod.module.impl.combat;

import keystrokesmod.event.AttackEvent;
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.SliderSetting;
import keystrokesmod.utility.Utils;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;

public class WTap extends Module {
    private SliderSetting chance;
    private ButtonSetting playersOnly;
    private final HashMap<Integer, Long> targets = new HashMap<>();
    public static boolean stopSprint = false;
    public WTap() {
        super("WTap", category.combat);
        this.registerSetting(chance = new SliderSetting("Chance", 100, 0, 100, 1, "%"));
        this.registerSetting(playersOnly = new ButtonSetting("Players only", true));
    }

    @EventTarget
    public void onAttack(AttackEvent event) {
        if (!Utils.nullCheck() || !mc.thePlayer.isSprinting()) {
            return;
        }
        if (chance.getInput() == 0) {
            return;
        }
        if (playersOnly.isToggled()) {
            if (!(event.getTarget() instanceof EntityPlayer)) {
                return;
            }
            final EntityPlayer entityPlayer = (EntityPlayer)event.getTarget();
            if (entityPlayer.maxHurtTime == 0 || entityPlayer.hurtTime > 3) {
                return;
            }
        }
        else if (!(event.getTarget() instanceof EntityLivingBase)) {
            return;
        }
        if (((EntityLivingBase)event.getTarget()).deathTime != 0) {
            return;
        }
        final long currentTimeMillis = System.currentTimeMillis();
        final Long n = this.targets.get(event.getTarget().getEntityId());
        if (n != null && Utils.getDifference(n, currentTimeMillis) <= 200L) {
            return;
        }
        if (chance.getInput() != 100.0D) {
            double ch = Math.random();
            if (ch >= chance.getInput() / 100.0D) {
                return;
            }
        }
        this.targets.put(event.getTarget().getEntityId(), currentTimeMillis);
        stopSprint = true;
    }

    public void onDisable() {
        stopSprint = false;
        this.targets.clear();
    }
}
