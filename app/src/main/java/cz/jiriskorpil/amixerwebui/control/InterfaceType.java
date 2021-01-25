package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface types.
 */
public enum InterfaceType
{
	MIXER,
	PCM,
	UNKNOWN;

	/**
	 * Converts string to interface type.
	 *
	 * @param iface interface type as string
	 * @return interface type
	 */
	public static InterfaceType fromString(String iface)
	{
		switch (iface)
		{
			case "MIXER":
				return MIXER;
			case "PCM":
				return PCM;
			default:
				return UNKNOWN;
		}
	}
}
