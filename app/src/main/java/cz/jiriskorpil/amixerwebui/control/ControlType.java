package cz.jiriskorpil.amixerwebui.control;

/**
 * Control types.
 */
public enum ControlType
{
	ENUMERATED,
	BOOLEAN,
	INTEGER,
	UNKNOWN;

	/**
	 * Converts string to control type.
	 *
	 * @param type control type as string
	 * @return control type
	 */
	public static ControlType fromString(String type)
	{
		switch (type)
		{
			case "ENUMERATED":
				return ENUMERATED;
			case "BOOLEAN":
				return BOOLEAN;
			case "INTEGER":
				return INTEGER;
			default:
				return UNKNOWN;
		}
	}
}
