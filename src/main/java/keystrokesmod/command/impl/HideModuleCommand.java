package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.Module;
import keystrokesmod.util.GeneralUtils;

public class HideModuleCommand extends Command {
	public HideModuleCommand() {
		super("hide", null);
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			GeneralUtils.sendMessage("Usage: .hide (module name)");
			return;
		}

		for (Module module : ModuleManager.getModules()) {
			String name = module.getName().toLowerCase().replace(" ", "");
			if (name.equals(args[0].toLowerCase())) {
				module.setHidden(true);
				GeneralUtils.sendMessage(module.getName() + " is now hidden in HUD");
				return;
			}
		}

		GeneralUtils.sendMessage(args[0] + " not found");
	}
}
