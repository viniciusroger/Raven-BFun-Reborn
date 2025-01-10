package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.manager.ModuleManager;
import keystrokesmod.module.Module;
import keystrokesmod.util.GeneralUtils;

public class ShowModuleCommand extends Command {
	public ShowModuleCommand() {
		super("show", null);
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			GeneralUtils.sendMessage("Usage: .show (module name)");
			return;
		}

		for (Module module : ModuleManager.getModules()) {
			String name = module.getName().replace(" ", "");
			if (name.equalsIgnoreCase(args[0])) {
				module.setHidden(false);
				GeneralUtils.sendMessage(module.getName() + " is now visible in HUD");
				return;
			}
		}

		GeneralUtils.sendMessage(args[0] + " not found");
	}
}
