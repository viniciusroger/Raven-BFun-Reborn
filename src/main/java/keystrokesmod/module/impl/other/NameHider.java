package keystrokesmod.module.impl.other;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.setting.impl.DescriptionSetting;
import keystrokesmod.util.GeneralUtils;
import net.minecraft.client.network.NetworkPlayerInfo;

public class NameHider extends Module {
    public static DescriptionSetting a;
    public static String n = "raven";
    public static ButtonSetting hideAllNames;

    public NameHider() {
        super("Name Hider", Category.other);
        this.registerSetting(a = new DescriptionSetting(GeneralUtils.uf("command") + ": cname [name]"));
        this.registerSetting(hideAllNames = new ButtonSetting("Hide all names", false));
    }

    public static String getFakeName(String s) {
        if (mc.thePlayer != null) {
            if (hideAllNames.isToggled()) {
                s = s.replace(GeneralUtils.getServerName(), "You");
                NetworkPlayerInfo getPlayerInfo = mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
                for (NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (networkPlayerInfo.equals(getPlayerInfo)) {
                        continue;
                    }
                    s = s.replace(networkPlayerInfo.getGameProfile().getName(), n);
                }
            }
            else {
                s = s.replace(GeneralUtils.getServerName(), n);
            }
        }
        return s;
    }
}
