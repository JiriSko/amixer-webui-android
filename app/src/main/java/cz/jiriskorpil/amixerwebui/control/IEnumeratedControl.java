package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface for representing enumerated control (select one from many).
 */
public interface IEnumeratedControl extends IControl
{
	/**
	 * Returns array of items names.
	 *
	 * @return array of names
	 */
	String[] getItems();

	/**
	 * Returns index of currently active item.
	 *
	 * @return active item index
	 */
	int getValue();

	/**
	 * Sets index of active item.
	 *
	 * @param index active item index
	 */
	void setValue(int index);
}
