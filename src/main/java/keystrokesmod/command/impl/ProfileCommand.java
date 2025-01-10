package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.module.impl.client.Settings;
import keystrokesmod.profile.Profile;
import keystrokesmod.util.GeneralUtils;

public class ProfileCommand extends Command {
	public ProfileCommand() {
		super("profile", null);
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			GeneralUtils.sendMessage("Usage:");
			GeneralUtils.sendMessage(".profile list");
			GeneralUtils.sendMessage(".profile save/load (profile name)");
			return;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			if (Raven.profileManager.profiles.isEmpty()) {
				GeneralUtils.sendMessage("No profiles available");
				return;
			}

			GeneralUtils.sendMessage("Profiles:");
			for (Profile profile : Raven.profileManager.profiles) {
				GeneralUtils.sendMessage(profile.getName());
			}
		}

		if (args.length == 2) {
			switch (args[0]) {
				case "save":
					Raven.profileManager.saveProfile(new Profile(args[1], 0));
					GeneralUtils.sendMessage("Saved profile as: " + args[1]);
					Raven.profileManager.loadProfiles();
					break;
				case "load":
					for (Profile profile : Raven.profileManager.profiles) {
						if (profile.getName().equalsIgnoreCase(args[1])) {
							Raven.profileManager.loadProfile(profile.getName());
							GeneralUtils.sendMessage("Loaded profile: " + args[1]);
							if (Settings.sendMessage.isToggled()) {
								GeneralUtils.sendMessage("&7Enabled profile: &b" + args[1]);
							}
						}
					}
			}
		}
	}
}
