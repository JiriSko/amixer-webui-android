package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface for representing base control.
 */
public interface IControl
{
	/**
	 * Returns control identifier.
	 *
	 * @return identifier
	 */
	int getId();

	/**
	 * Returns interface type.
	 *
	 * @return type of interface
	 */
	InterfaceType getIface();

	/**
	 * Returns access string for control.
	 *
	 * @return access string
	 */
	String getAccess();

	/**
	 * Returns control name.
	 *
	 * @return name
	 */
	String getName();
}
