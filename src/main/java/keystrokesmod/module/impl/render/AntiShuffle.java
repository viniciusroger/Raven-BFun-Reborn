package keystrokesmod.module.impl.render;

import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.DescriptionSetting;

public class AntiShuffle extends Module {
    private static String shuffleStr = "§k";

    public AntiShuffle() {
        super("AntiShuffle", Category.render, 0);
        this.registerSetting(new DescriptionSetting("Removes obfuscation (" + shuffleStr + "hey" + "§" + "r)."));
    }

    public static String removeObfuscation(String s) {
        return s.replace(shuffleStr, "");
    }
}