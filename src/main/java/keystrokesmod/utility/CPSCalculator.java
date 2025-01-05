package keystrokesmod.utility;

import keystrokesmod.Raven;
import keystrokesmod.event.MouseEvent;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.module.impl.world.AntiBot;
import net.lenni0451.asmevents.event.EventTarget;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.ArrayList;
import java.util.List;

public class CPSCalculator {
    private static Minecraft mc = Minecraft.getMinecraft();
    private static List<Long> a = new ArrayList<>();
    private static List<Long> b = new ArrayList<>();
    public static long LL = 0L;
    public static long LR = 0L;

    @EventTarget
    public void onMouseUpdate(MouseEvent d) {
        if (d.isButtonState()) {
            if (d.getButton() == 0) {
                aL();
                if (Raven.debugger && mc.objectMouseOver != null) {
                    Entity en = mc.objectMouseOver.entityHit;
                    if (en == null) {
                        return;
                    }

                    Utils.sendMessage("&7&m-------------------------");
                    Utils.sendMessage("n: " + en.getName());
                    Utils.sendMessage("rn: " + en.getName().replace("§", "%"));
                    Utils.sendMessage("d: " + en.getDisplayName().getUnformattedText());
                    Utils.sendMessage("rd: " + en.getDisplayName().getUnformattedText().replace("§", "%"));
                    Utils.sendMessage("b?: " + AntiBot.isBot(en));
                }
            } else if (d.getButton() == 1) {
                aR();
            }
            else if (d.getButton() == 2 && Settings.middleClickFriends.isToggled()) {
                EntityLivingBase g = Utils.raytrace(30);
                if (!AntiBot.isBot(g) && !Utils.addFriend(g.getName())) {
                    Utils.removeFriend(g.getName());
                }
            }
        }
    }

    public static void aL() {
        a.add(LL = System.currentTimeMillis());
    }

    public static void aR() {
        b.add(LR = System.currentTimeMillis());
    }

    public static int f() {
        a.removeIf(o -> (Long) o < System.currentTimeMillis() - 1000L);
        return a.size();
    }

    public static int i() {
        b.removeIf(o -> (Long) o < System.currentTimeMillis() - 1000L);
        return b.size();
    }
}
