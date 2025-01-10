package keystrokesmod.command;

public class Command {
	private String name;

	private String[] alias;

	public Command(String name, String[] alias) {
		this.name = name;
		this.alias = alias;
	}

	public void onExecute(String[] args) {}

	public String getName() {
		return name;
	}

	public String[] getAlias() {
		return alias;
	}

	public String getNameAlias() {
		StringBuilder temp;

		if (alias == null || alias.length == 0)
			temp = new StringBuilder(name);
		else {
			temp = new StringBuilder(name + "/");

			for (String aliasName : alias) {
				temp.append(aliasName).append("/");
			}

			return temp.substring(0, temp.toString().length() - 1);
		}

		return temp.toString();
	}
}
