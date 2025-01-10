package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.module.impl.other.NameHider;
import keystrokesmod.util.GeneralUtils;

public class CNameCommand extends Command {
	public CNameCommand() {
		super("cname", new String[]{"fakename"});
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			GeneralUtils.sendMessage("Usage: " + this.getNameAlias() + " (name)");
			return;
		}

		NameHider.n = args[0];
		GeneralUtils.sendMessage("Changed name to " + args[0]);
	}
}
