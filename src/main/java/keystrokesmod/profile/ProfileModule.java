package keystrokesmod.profile;

import keystrokesmod.Raven;
import keystrokesmod.ui.clickgui.ClickGui;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.util.GeneralUtils;

public class ProfileModule extends Module {
    private Profile profile;
    public boolean saved = true;

    public ProfileModule(Profile profile, String name, int bind) {
        super(name, Category.profiles, bind);
        this.profile = profile;
        this.registerSetting(new ButtonSetting("Save profile", () -> {
            GeneralUtils.sendMessage("&7Saved profile: &b" + getName());
            Raven.profileManager.saveProfile(this.profile);
            saved = true;
        }));
        this.registerSetting(new ButtonSetting("Remove profile", () -> {
            GeneralUtils.sendMessage("&7Removed profile: &b" + getName());
            Raven.profileManager.deleteProfile(getName());
        }));
    }

    @Override
    public void toggle() {
        if (mc.currentScreen instanceof ClickGui || mc.currentScreen == null) {
            if (this.profile == Raven.currentProfile) {
                return;
            }
            Raven.profileManager.loadProfile(this.getName());

            Raven.currentProfile = profile;

            if (Settings.sendMessage.isToggled()) {
                GeneralUtils.sendMessage("&7Enabled profile: &b" + this.getName());
            }
            saved = true;
        }
    }

    @Override
    public boolean isEnabled() {
        if (Raven.currentProfile == null) {
            return false;
        }
        return Raven.currentProfile.getModule() == this;
    }
}
