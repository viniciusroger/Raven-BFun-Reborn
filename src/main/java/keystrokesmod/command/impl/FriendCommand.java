package keystrokesmod.command.impl;

import keystrokesmod.command.Command;
import keystrokesmod.util.GeneralUtils;

public class FriendCommand extends Command {
	public FriendCommand() {
		super("friend", null);
	}

	@Override
	public void onExecute(String[] args) {
		if (args.length == 0) {
			GeneralUtils.sendMessage("Usage:");
			GeneralUtils.sendMessage(".friend add/remove (friend name)");
			GeneralUtils.sendMessage(".friend clear");
			return;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
			GeneralUtils.friends.clear();
			GeneralUtils.sendMessage("Friends cleared");
			return;
		}

		if (args.length == 2) {
			switch (args[0]) {
				case "add":
					GeneralUtils.addFriend(args[1]);
					GeneralUtils.sendMessage("Added " + args[1] + " as friend");
					break;
				case "remove":
					if (GeneralUtils.friends.contains(args[1])) {
						GeneralUtils.removeFriend(args[1]);
						GeneralUtils.sendMessage("Removed " + args[1] + " from friends");
						break;
					}

					GeneralUtils.sendMessage(args[1] + " not found");
			}
		}
	}
}
