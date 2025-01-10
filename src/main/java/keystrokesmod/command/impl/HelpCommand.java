package keystrokesmod.command.impl;

import keystrokesmod.Raven;
import keystrokesmod.command.Command;
import keystrokesmod.util.GeneralUtils;

public class HelpCommand extends Command {
	public HelpCommand() {
		super("help", null);
	}

	@Override
	public void onExecute(String[] args) {
		if (Raven.commandManager.getCommands().isEmpty()) {
			GeneralUtils.sendMessage("No commands available");
			return;
		}

		GeneralUtils.sendMessage("Commands:");
		for (Command command : Raven.commandManager.getCommands()) {
			GeneralUtils.sendMessage("." + command.getNameAlias());
		}
	}
}
