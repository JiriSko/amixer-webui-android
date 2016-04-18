package cz.jiriskorpil.amixerwebui.control;

/**
 * Container for related controls (connects boolean, enumerated and integer control into logical container).
 */
public class ControlContainer
{
	private IEnumeratedControl cSource;
	private IBooleanControl cSwitch;
	private IIntegerControl cVolume;

	private String name;

	/**
	 * Creates new blank control container.
	 *
	 * @param name container or control name (container name is automatically extracted from control name)
	 */
	public ControlContainer(String name)
	{
		super();
		this.name = ControlContainerType.getContainerName(name);
	}

	/**
	 * Returns container name.
	 *
	 * @return container name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Adds control into container. Control type is automatically detected based on its name.
	 *
	 * @param control control which should be added into container
	 */
	public void addControl(Control control)
	{
		switch (ControlContainerType.fromName(control.getName()))
		{
			case SOURCE:
				cSource = (IEnumeratedControl) control;
				break;
			case SWITCH:
				cSwitch = (IBooleanControl) control;
				break;
			case VOLUME:
				cVolume = (IIntegerControl) control;
				break;
		}
	}

	/**
	 * Checks whether container contains enumerated control.
	 *
	 * @return true if container contains enumerated control
	 */
	public boolean hasSourceControl()
	{
		return cSource != null;
	}

	/**
	 * Returns enumerated control.
	 *
	 * @return enumerated control
	 */
	public IEnumeratedControl getSource()
	{
		return cSource;
	}

	/**
	 * Checks whether container contains boolean control.
	 *
	 * @return true if container contains boolean control
	 */
	public boolean hasSwitchControl()
	{
		return cSwitch != null;
	}

	/**
	 * Returns boolean control.
	 *
	 * @return boolean control
	 */
	public IBooleanControl getSwitch()
	{
		return cSwitch;
	}

	/**
	 * Checks whether container contains integer control.
	 *
	 * @return true if container contains boolean control
	 */
	public boolean hasVolumeControl()
	{
		return cVolume != null;
	}

	/**
	 * Returns integer control.
	 *
	 * @return integer control
	 */
	public IIntegerControl getVolume()
	{
		return cVolume;
	}
}
