package keystrokesmod.util;

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

public class CPSUtil {
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

                    GeneralUtils.sendMessage("&7&m-------------------------");
                    GeneralUtils.sendMessage("n: " + en.getName());
                    GeneralUtils.sendMessage("rn: " + en.getName().replace("§", "%"));
                    GeneralUtils.sendMessage("d: " + en.getDisplayName().getUnformattedText());
                    GeneralUtils.sendMessage("rd: " + en.getDisplayName().getUnformattedText().replace("§", "%"));
                    GeneralUtils.sendMessage("b?: " + AntiBot.isBot(en));
                }
            } else if (d.getButton() == 1) {
                aR();
            }
            else if (d.getButton() == 2 && Settings.middleClickFriends.isToggled()) {
                EntityLivingBase g = GeneralUtils.raytrace(30);
                if (!AntiBot.isBot(g)) {
                    if (GeneralUtils.friends.contains(g.getName())) {
                        GeneralUtils.removeFriend(g.getName());
                    } else {
                        GeneralUtils.addFriend(g.getName());
                    }
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
