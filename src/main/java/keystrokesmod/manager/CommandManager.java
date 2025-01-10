package keystrokesmod.manager;

import keystrokesmod.command.Command;
import keystrokesmod.command.impl.*;
import keystrokesmod.event.SendMessageEvent;
import net.lenni0451.asmevents.EventManager;
import net.lenni0451.asmevents.event.EventTarget;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandManager {
	private ArrayList<Command> commands = new ArrayList<>();

	public void register() {
		EventManager.register(this);
		commands.addAll(Arrays.asList(
				new CNameCommand(),
				new FriendCommand(),
				new HideModuleCommand(),
				new HelpCommand(),
				new ShowModuleCommand(),
				new ProfileCommand()
		));
	}

	@EventTarget
	public void onSendMessage(SendMessageEvent event) {
		String[] temp = event.getMessage().split(" ");

		String name = temp[0].substring(1);
		String[] args = Arrays.copyOfRange(temp, 1, temp.length);

		if (event.getMessage().startsWith(".")) {
			event.setCancelled(true);

			for (Command command : commands) {
				if (name.equalsIgnoreCase(command.getName()))
					command.onExecute(args);
				else {
					if (command.getAlias() != null) {
						for (String aliasName : command.getAlias()) {
							if (name.equalsIgnoreCase(aliasName))
								command.onExecute(args);
						}
					}
				}
			}
		}
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}
}
