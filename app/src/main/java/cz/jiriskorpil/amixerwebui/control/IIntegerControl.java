package cz.jiriskorpil.amixerwebui.control;

/**
 * Interface for representing integer control (common volume slider).
 */
public interface IIntegerControl extends IControl
{
	/**
	 * Returns array of volume channels.
	 *
	 * @return array of volume channels
	 */
	IChannel[] getChannels();

	/**
	 * Returns volume slider maximum.
	 *
	 * @return slider minimum
	 */
	int getMin();

	/**
	 * Returns volume slider maximum value.
	 *
	 * @return slider maximum
	 */
	int getMax();

	/**
	 * Returns volume slider step.
	 *
	 * @return slider step
	 */
	int getStep();
}
