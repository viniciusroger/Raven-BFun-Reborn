package keystrokesmod.profile;

import keystrokesmod.Raven;
import keystrokesmod.module.Module;
import keystrokesmod.setting.impl.ButtonSetting;
import keystrokesmod.util.GeneralUtils;

import java.awt.*;
import java.io.IOException;

public class Manager extends Module {
    private ButtonSetting loadProfiles, openFolder, createProfile;

    public Manager() {
        super("Manager", Category.profiles);
        this.registerSetting(createProfile = new ButtonSetting("Create profile", () -> {
            if (GeneralUtils.nullCheck() && Raven.profileManager != null) {
                String name = "profile-";
                for (int i = 1; i <= 100; i++) {
                    if (Raven.profileManager.getProfile(name + i) != null) {
                        continue;
                    }
                    name += i;
                    Raven.profileManager.saveProfile(new Profile(name, 0));
                    GeneralUtils.sendMessage("&7Created profile: &b" + name);
                    Raven.profileManager.loadProfiles();
                    break;
                }
            }
        }));
        this.registerSetting(loadProfiles = new ButtonSetting("Load profiles", () -> {
            if (GeneralUtils.nullCheck() && Raven.profileManager != null) {
                Raven.profileManager.loadProfiles();
            }
        }));
        this.registerSetting(openFolder = new ButtonSetting("Open folder", () -> {
            try {
                Desktop.getDesktop().open(Raven.profileManager.directory);
            }
            catch (IOException ex) {
                Raven.profileManager.directory.mkdirs();
                GeneralUtils.sendMessage("&cError locating folder, recreated.");
            }
        }));
        ignoreOnSave = true;
        canBeEnabled = false;
    }
}