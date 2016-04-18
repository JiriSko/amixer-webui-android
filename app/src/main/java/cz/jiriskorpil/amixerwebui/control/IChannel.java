package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface for representing one channel in integer control.
 */
public interface IChannel
{
	/**
	 * Returns parent integer control.
	 *
	 * @return parent control
	 */
	IIntegerControl getControl();

	/**
	 * Returns channel name.
	 *
	 * @return name
	 */
	String getName();

	/**
	 * Returns channel value.
	 *
	 * @return value
	 */
	int getValue();

	/**
	 * Sets channel value.
	 *
	 * @param value channel value
	 */
	void setValue(int value);
}
