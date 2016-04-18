package cz.jiriskorpil.amixerwebui.control;

/**
 * Control container types.
 */
public enum ControlContainerType
{
	SOURCE,
	SWITCH,
	VOLUME,
	UNKNOWN;

	/**
	 * Converts control name to control container type.
	 *
	 * @param name control as string
	 * @return control container type
	 */
	public static ControlContainerType fromName(String name)
	{
		if (name.matches("^(.*) Source$")) {
			return SOURCE;
		} else if (name.matches("^(.*) Switch")) {
			return SWITCH;
		} else if (name.matches("^(.*) Volume$")) {
			return VOLUME;
		}
		return UNKNOWN;
	}

	/**
	 * Returns container name based on control name.
	 *
	 * @param name control name
	 * @return container name
	 */
	public static String getContainerName(String name)
	{
		return name.replaceAll("^(.*) (Source|Switch|Volume)$", "$1");
	}
}
