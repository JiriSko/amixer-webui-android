package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface for representing boolean control (on/off)
 */
public interface IBooleanControl extends IControl
{
	/**
	 * Checks whether control is active.
	 *
	 * @return true if control is active
	 */
	boolean isChecked();

	/**
	 * Sets control value.
	 *
	 * @param value true if control should be active
	 */
	void setChecked(boolean value);
}
